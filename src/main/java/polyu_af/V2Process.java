package polyu_af;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jdt.core.dom.CompilationUnit;
import polyu_af.exception.NotFoundException;
import polyu_af.models.*;
import polyu_af.new_model.*;
import polyu_af.process.*;
import polyu_af.utils.AstUtils;
import polyu_af.utils.FileUtils;
import polyu_af.visitors.AccessVar2TableVisitor;
import polyu_af.visitors.MAccessVarVisitor;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

/**
 * Created by liushanchen on 16/7/24.
 */
public class V2Process {
    private static Logger logger = LogManager.getLogger(V2Process.class.getName());

    public static void run() {
        TargetConfig targetConfig = null;
        TargetProgram targetProgram = null;
        /**************************** Get target program's configuration *********************************/

        TargetConfigReader targetConfigReader = null;
        try {
            targetConfigReader = new TargetConfigReader(System.getProperty("user.dir") + "/input/InputFile_AfTest_2");
        } catch (NotFoundException e) {
            e.printStackTrace();
            return;
        }
        targetConfig = targetConfigReader.getTc();
        if (targetConfig != null) {
            targetProgram = new TargetProgram(targetConfig);
        } else {
            return;
        }

        /**************************** Run all tests with MLogAgent *********************************/

        ExeCommandBuilder exeMLogAg = new ExeMLogAgCommandBuilder(targetConfig);
        Runner.process(exeMLogAg.runTestClass(targetProgram.getTargetTestsClasses()));
        exeMLogAg = null;

        /**************************** Analyse MJR (MyJunit test result log) and find failure tests *********************************/

        List<TestCluster> testClusters = FileUtils.json2TestClusterList();
        List<TestUnit> allFailures = new ArrayList<TestUnit>();
        for (TestCluster cluster : testClusters) {
            if (!cluster.isSuccessful()) {
                allFailures.addAll(cluster.getFailureTestList());
            }
            System.out.println("TestUnit"+cluster.getFailureTestList());

        }
        testClusters = null;

        /**************************** Analyze MLog (all invoked method is log here) & find related class *********************************/

        /* 分析 */
        List<String> relatedClass = null;
        List<MLogAnalyResult> allTestUnitsResults = null;
        MLogAnalyzerAllTest mLogAnalyzerAllTest = new MLogAnalyzerAllTest();
        mLogAnalyzerAllTest.analyze(allFailures);
        relatedClass = mLogAnalyzerAllTest.getRelatedClass();
        allTestUnitsResults = mLogAnalyzerAllTest.getResultList();
        for(MLogAnalyResult mar:allTestUnitsResults){
            System.out.println("mar"+mar);

        }

        mLogAnalyzerAllTest = null;

        /**************************** Analyze related classes' AST *********************************/

        LineVars.addInvokingMethod = true;
        Hashtable<TargetLine, List<MyExp>> lineVarsTable = new Hashtable<TargetLine, List<MyExp>>();
        for (String qname : relatedClass) {
            /* create faultFile's AST */
            TargetFile tf = targetProgram.getTargetFile(qname);//根据qualified name从targetprogram中找到对应的TargetFile

            TargetFileOld tfOld = targetProgram.getTarget(qname);//根据qualified name从targetprogram中找到对应的TargetFile
            if (tf != null) {//对TargetFile进行AST分析,得到 access variables 并存在TargetFile的method的line里。
                CompilationUnit root = AstUtils.createResolvedAST(tf.getSource(),
                        targetConfig.getClasspathEntries(), new String[]{targetConfig.getSourcePath()},
                        targetConfig.getEncodings(), tf.getQualifyFileName());

                AccessVar2TableVisitor accessVar2TableVisitor = new AccessVar2TableVisitor(root, tf, lineVarsTable);
                root.accept(accessVar2TableVisitor);
                /* 准备用于输出json的对象 */
                MAccessVarVisitor mvv = new MAccessVarVisitor(root);
                root.accept(mvv);
                /* 所有 access astExp of line 以 method 分组 */
                List<MyMethod> methodLineLists = mvv.getMyMethodAccessVars();
                /* 所有 access astExp of line */
                mvv = null;
                tfOld.setMyMethodWithAccessVars(methodLineLists);
            }
        }


        /* out put (AST analysis) results of target source code as json file*/
        FileUtils.output(targetProgram.getTargetFileOlds());
        targetProgram.setTargetFileOlds(null);

        /**************************** Build Predicate *********************************/

        //根据targetFile中的line access variables 构造 line predicate,同样存储在TargetFile的MyMethod的LineVars中。
        Hashtable<TargetLine, List<Predicate>> linePredicateTable = buildPredicate(lineVarsTable);
//        System.out.println("getLineExpTable:" + linePredicateTable);

        /**************************** Run all test units with VarLogAgent and analyze Log *********************************/

        //monitor access variables的值,并构建snapshot,都存储在TESTCaseR的TcMethod的TcLine中
        ExeCommandBuilder exeVarLogAg = new ExeVarLogAgCommandBuilder(targetConfig);
//        Hashtable<TargetLine, List<ExpValue>> lineVarsValTable = new Hashtable<>();
        Hashtable<TestUnit, List<SnapshotV3>> testSnapshotTable = new Hashtable<>();
        SnapshotBuilderV3 ssb = new SnapshotBuilderV3();

        for (MLogAnalyResult mLogResult : allTestUnitsResults) {
            Hashtable<TargetLine, List<ExpValue>> lineVarsValTable = new Hashtable<>();
            TestUnit testUnit = mLogResult.getTestUnit();
            Runner.process(exeVarLogAg.runTestUnit(testUnit));
            VarLogAnalyzerV2 varLog = new VarLogAnalyzerV2(lineVarsTable, lineVarsValTable);
            varLog.tcLogAnalyze();
            lineVarsValTable = varLog.getLineVarsValTable();
//            System.out.println("lineVarsValTable:" + lineVarsValTable);
            /**************************** Build SnapshotV3 *********************************/
            try {
//                System.out.println("lineSnapshotTable:" + testSnapshotTable);
                List<SnapshotV3> snapshotV3List = buildSnapshotV3(ssb, linePredicateTable, lineVarsValTable);
                testSnapshotTable.put(testUnit, snapshotV3List);
            } catch (Exception e) {
                e.printStackTrace();
            }


        }
        exeVarLogAg = null;
//        System.out.println("testSnapshotTable:" + testSnapshotTable);

        /**************************** Build SnapshotV2 *********************************/
//        try {
//            Hashtable<TargetLine, List<SnapshotV2>> lineSnapshotTable = buildSnapshot(linePredicateTable, lineVarsValTable);
//            System.out.println("lineSnapshotTable:" + lineSnapshotTable);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        /**************************** Finding fault location and status (evaluating the appearance of snapshot) *********************************/
        SnapshotEvaluator sse=new SnapshotEvaluator();
        Hashtable<SnapshotV3, SnapshotScore> sssTable=sse.evaluate(testSnapshotTable);
        System.out.println("sssTable:" + sssTable);


    }

    private static Hashtable<TargetLine, List<Predicate>> buildPredicate(Hashtable<TargetLine, List<MyExp>> lineVarsTable) {
        PredicateBuilder bss = new PredicateBuilder();
        Hashtable<TargetLine, List<Predicate>> linePredicate = new Hashtable<>();
        for (Map.Entry<TargetLine, List<MyExp>> lineVarsE : lineVarsTable.entrySet()) {
            List<Predicate> pl = bss.buildPredicate(lineVarsE.getValue());
            linePredicate.put(lineVarsE.getKey(), pl);
        }
        return linePredicate;
    }

    private static Hashtable<TargetLine, List<SnapshotV2>> buildSnapshot(Hashtable<TargetLine, List<Predicate>> linePredicateTable
            , Hashtable<TargetLine, List<ExpValue>> lineVarsValTable) throws Exception {
        Hashtable<TargetLine, List<SnapshotV2>> lineSnapshotTable = new Hashtable<>();
        /**
         * lineVarsValTable 的size小
         */
        SnapshotBuilderV2 ess = new SnapshotBuilderV2();
        for (TargetLine tl : lineVarsValTable.keySet()) {
            List<SnapshotV2> ssl = ess.buildSnapshot(linePredicateTable.get(tl), lineVarsValTable.get(tl));
            lineSnapshotTable.put(tl, ssl);
        }
        return lineSnapshotTable;
    }

    private static List<SnapshotV3> buildSnapshotV3(SnapshotBuilderV3 ssb, Hashtable<TargetLine, List<Predicate>> linePredicateTable
            , Hashtable<TargetLine, List<ExpValue>> lineVarsValTable) throws Exception {
        List<SnapshotV3> lineSnapshots = new ArrayList<>();
        System.out.println("linePredicateTable.key size=" + linePredicateTable.keySet().size());
        System.out.println("lineVarsValTable.key size=" + lineVarsValTable.keySet().size());
        /**
         * lineVarsValTable 的size小
         */
        for (TargetLine tl : lineVarsValTable.keySet()) {
            lineSnapshots.addAll(ssb.buildSnapshot(tl, linePredicateTable.get(tl), lineVarsValTable.get(tl)));
        }
        return lineSnapshots;
    }

}
