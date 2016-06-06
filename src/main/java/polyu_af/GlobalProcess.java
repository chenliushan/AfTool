package polyu_af;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jdt.core.dom.CompilationUnit;
import polyu_af.exception.NotFoundException;
import polyu_af.models.*;
import polyu_af.process.AnalyzeMLogAllTest;
import polyu_af.process.AnalyzeVarLog;
import polyu_af.process.ExeTargetRuntime;
import polyu_af.process.GetTargetConfig;
import polyu_af.utils.AstUtils;
import polyu_af.utils.FileUtils;
import polyu_af.visitors.MAccessVarVisitor;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liushanchen on 16/3/17.
 */
public class GlobalProcess {
    private static Logger logger = LogManager.getLogger(GlobalProcess.class.getName());

    public static void main(String arg[]) {
        long startTime = System.currentTimeMillis();
        TargetConfig targetConfig = null;
        TargetProgram targetProgram = null;
        /****************************Get target program's configuration *********************************/
        GetTargetConfig getTargetConfig = null;
        try {
            getTargetConfig = new GetTargetConfig(System.getProperty("user.dir") + "/input/InputFile_AfTest_2");
//            getTargetConfig = new GetTargetConfig(System.getProperty("user.dir") + "/input/InputFile_Test4Javassist.txt");
        } catch (NotFoundException e) {
            e.printStackTrace();
            return;
        }
        targetConfig = getTargetConfig.getTc();
        if (targetConfig != null) {
            targetProgram = new TargetProgram(targetConfig);
        } else {
            return;
        }
        /****************************Run all test with MLogAgent*********************************/
        AbsExeCommand exeMLogAg = new ExeMLogAgCommand(targetConfig);
        ExeTargetRuntime.process(exeMLogAg.testClass(targetProgram.getTargetTestsClasses()));
        exeMLogAg = null;
        /****************************Analyse of MJR log finding failure tests*********************************/
        List<TestCluster> testClusters = FileUtils.json2TestClusterList();
        List<TestUnit> allFailures = new ArrayList<TestUnit>();
        for (TestCluster cluster : testClusters) {
            if (!cluster.isSuccessful()) {
                allFailures.addAll(cluster.getFailureTestList());
            }
        }
        testClusters = null;
        /****************************Analysis of MLog & finding related class*********************************/
        List<String> relatedClass = null;
        List<MLogAnalyResult> allTestUnitsResults = null;
//            AnalyzeMLogFailure analyzeMLogFailure = new AnalyzeMLogFailure();
        AnalyzeMLogAllTest analyzeMLog = new AnalyzeMLogAllTest();
        analyzeMLog.analyze(allFailures);
        relatedClass = analyzeMLog.getRelatedClass();
        allTestUnitsResults = analyzeMLog.getResultList();
        analyzeMLog = null;
//            logger.info("mLogAnalyResults:" + analyzeMLog.getResultList());
        /****************************Analysis of the AST of the related class*********************************/
        /* analyze related classes */
        LineAccessVars.addInvokingMethod = false;
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
                 /* 所有line access ExpString 以method分组*/
                List<MyMethod> methodLineLists = mvv.getMyMethodAccessVars();
                /* 所有line access astExp*/
                List<LineAccessAstVars> lineAccessAstVarsList = mvv.getAccessAstVars4LineList();
                mvv = null;
                tf.setMyMethodAccessVars(methodLineLists);
            }
        }
        /* out put first step (AST analysis) results */
        FileUtils.outputTfList(targetProgram.getTargetSources());
        /**************************** ###Run tests with VarLogAgent*********************************/
        AbsExeCommand exeVarLogAg = new ExeVarLogAgCommand(targetConfig);
//        ExeTargetRuntime.process(exeVarLogAg.runTestUnits(allFailures));
        /****************************Analysis of the Var log*********************************/
        AnalyzeVarLog varLog = new AnalyzeVarLog(targetProgram.getTargetSources());
//        List<LineState> lsList = varLog.analyze();
//        logger.info("List<LineState>:"+lsList);
        /**************************** ###Run all test units and analyze Log*********************************/
        for (MLogAnalyResult mLogResult : allTestUnitsResults) {
            ExeTargetRuntime.process(exeVarLogAg.runTestUnit(mLogResult.getTestCase()));
            List<LineState> lsList = varLog.analyze();
            logger.info("\nList<LineState>:" + lsList);
            break;
        }

        exeVarLogAg = null;
        /**************************** ###Finding fault location and status*********************************/


        //build expression with accessible variables
//        BuildIntegerExp buildIntegerExp= new BuildIntegerExp(accessibleVar.get(34));
//        buildIntegerExp.buildingExps();
//        CommonUtils.printExpList(buildIntegerExp.getBuildExp());
//        BuildBooleanExp buildBooleanExp=new BuildBooleanExp(accessibleVar.get(148));
//        buildBooleanExp.buildingExps();
//        CommonUtils.printExpList(buildBooleanExp.getBuildExp());


//
//        FileUtils.printMap(AstUtils.astForm);
//        logger.info("${logger}"+${logger});
//        List<FaultUnit> faultUnitList = null;
//        if (tf != null) {
//            faultUnitList = tf.getFaults();
//            if (faultUnitList != null) {
//                for (FaultUnit fu : faultUnitList) {
//                    //find fault node in root
////                    ASTNode faultNode = AstUtils.findNodeInRoot(root, fu);
//                    //resolve the input exp in the fault node
////                    ResolveExp resolveExp = new ResolveExp(faultNode, fu, root);
////                    resolveExp.resolveExp();
//                    //testing modify the expression
////                    if (!fu.getExpression().equals("exp")) {
////                       getTargetConfig.saveNewFaultClass(
////                               AstUtils.parseExpRecordModifications(
////                                       root, fu.getExpression(), faultFileSource_));
////
////                    }
//                }
//            }
//        }
        Runtime runtime = Runtime.getRuntime();
        logger.info("freeMemory:" + runtime.freeMemory() + "; totalMemory:" + runtime.totalMemory());
        long usedMemory = runtime.totalMemory() - runtime.freeMemory();
        logger.info("usedMemory:" + usedMemory);

        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        logger.info("totalTime:" + totalTime);

    }
}
