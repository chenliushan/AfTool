package polyu_af;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jdt.core.dom.CompilationUnit;
import polyu_af.exception.NotFoundException;
import polyu_af.models.*;
import polyu_af.process.*;
import polyu_af.utils.AstUtils;
import polyu_af.utils.FileUtils;
import polyu_af.visitors.MAccessVarVisitor;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liushanchen on 16/3/17.
 */
public class Application {
    private static Logger logger = LogManager.getLogger(Application.class.getName());

    public static void main(String arg[]) {
        long startTime = System.currentTimeMillis();
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
        }
        testClusters = null;

        /**************************** Analyze MLog (all invoked method is log here) & find related class *********************************/

        List<String> relatedClass = null;
        List<MLogAnalyResult> allTestUnitsResults = null;
        MLogAnalyzerAllTest mLogAnalyzerAllTest = new MLogAnalyzerAllTest();
        mLogAnalyzerAllTest.analyze(allFailures);
        relatedClass = mLogAnalyzerAllTest.getRelatedClass();
        allTestUnitsResults = mLogAnalyzerAllTest.getResultList();
        mLogAnalyzerAllTest = null;

        /**************************** Analyze related classes' AST *********************************/

        LineVars.addInvokingMethod = true;
        for (String qname : relatedClass) {
            /* create faultFile's AST */
            TargetFile tf = targetProgram.getTarget(qname);
            if (tf != null) {
                CompilationUnit root = AstUtils.createResolvedAST(tf.getSource(),
                        targetConfig.getClasspathEntries(), new String[]{targetConfig.getSourcePath()},
                        targetConfig.getEncodings(), tf.getQualifyFileName());
                /* get accessible variables */
                MAccessVarVisitor mvv = new MAccessVarVisitor(root);
                root.accept(mvv);
                /* 所有 access astExp of line 以 method 分组*/
                List<MyMethod> methodLineLists = mvv.getMyMethodAccessVars();
                /* 所有 access astExp of line */
//                List<LineVars> lineAccessAstVarsList = mvv.getAccessibleVars();
                mvv = null;
                tf.setMyMethodWithAccessVars(methodLineLists);
            }
        }
        /* out put (AST analysis) results of target source code as json file*/
        FileUtils.outputTfList(targetProgram.getTargetSources());

        /**************************** Build Predicate *********************************/

        buildPredicate(targetProgram.getTargetSources());

        /**************************** Run all test units with VarLogAgent and analyze Log *********************************/

        ExeCommandBuilder exeVarLogAg = new ExeVarLogAgCommandBuilder(targetConfig);
        List<TestCaseR> testCaseRList = new ArrayList<>();
        for (MLogAnalyResult mLogResult : allTestUnitsResults) {
            Runner.process(exeVarLogAg.runTestUnit(mLogResult.getTestCase()));
            VarLogAnalyzer varLog = new VarLogAnalyzer(targetProgram.getTargetSources());
            varLog.tcLogAnalyze();
            TestCaseR testCaseR = varLog.getTestCaseR();
            testCaseRList.add(testCaseR);
            /**************************** Build Snapshot*********************************/
            buildSnapshot(testCaseR, targetProgram.getTargetSources());
        }
        exeVarLogAg = null;

        /**************************** Finding fault location and status (count the appearance of snapshot)*********************************/

        for (TestCaseR tcr : testCaseRList) {
            logger.info("tcr:" + tcr);
        }

        /*************************************************************/

        Runtime runtime = Runtime.getRuntime();
        logger.info("freeMemory:" + runtime.freeMemory() + "; totalMemory:" + runtime.totalMemory());
        long usedMemory = runtime.totalMemory() - runtime.freeMemory();
        logger.info("usedMemory:" + usedMemory);

        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        logger.info("totalTime:" + totalTime);

    }

    private static void buildPredicate(List<TargetFile> tfList) {
        PredicateBuilder bss = new PredicateBuilder();
        for (TargetFile tf : tfList) {
            for (MyMethod mm : tf.getMyMethodWithAccessVars()) {
                for (LineVars lv : mm.getLineVarsList()) {
                    List<Predicate> pl = bss.buildPredicate(lv.getVarsList());
                    lv.setPredicates(pl);
                }
            }
        }
    }

    private static void buildSnapshot(TestCaseR testCaseR, List<TargetFile> tfList) {
        for (TcMethod tcMethod : testCaseR.getTcMethodList()) {
            MyMethod mm = findTFMethod(tfList, tcMethod.getLongName());
            if (mm != null) {
                for (TcLine tcLine : tcMethod.getTcLineList()) {
                    SnapshotBuilder ess = new SnapshotBuilder(tcLine);
                    List<Snapshot> ssl = ess.buildSnapshot(findPredicate(mm, tcLine.getLocation()));
                    tcLine.setSnapshotList(ssl);
                    tcLine.setExpValueList(null);
                }
            }
        }
    }

    private static MyMethod findTFMethod(List<TargetFile> tfList, String methodLongName) {
        for (TargetFile tf : tfList) {
            for (MyMethod mm : tf.getMyMethodWithAccessVars()) {
                if (mm.getLongName().equals(methodLongName)) {
                    return mm;
                }
            }
        }
        logger.error("findTFMethod null");
        return null;
    }

    private static List<Predicate> findPredicate(MyMethod mm, int location) {
        for (LineVars lv : mm.getLineVarsList()) {
            if (lv.getLocation() == location) {
                lv.getPredicates();
                return lv.getPredicates();
            }
        }
        logger.error("findPredicate null");
        return null;
    }

    private static void printTargetFiles(List<TargetFile> targetFiles) {
        for (TargetFile tf : targetFiles) {
            for (MyMethod mm : tf.getMyMethodWithAccessVars()) {
                if (mm != null) {
                    logger.info("targetSources:" + mm.toString() + "\n");
                }
            }
        }

    }
}
