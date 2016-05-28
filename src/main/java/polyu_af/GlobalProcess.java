package polyu_af;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jdt.core.dom.CompilationUnit;
import polyu_af.exception.NotFoundException;
import polyu_af.models.*;
import polyu_af.process.ExeTargetRuntime;
import polyu_af.process.GetTargetConfig;
import polyu_af.utils.AstUtils;
import polyu_af.visitors.MAccessVarVisitor;

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


        //read input file
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

        List<FaultUnit> faultUnitList = null;

        //create faultFileAST --root
        TargetFile tf = targetProgram.getCurrentTarget();
        CompilationUnit root = AstUtils.createResolvedAST(tf.getSource(),
                targetConfig.getClasspathEntries(), new String[]{targetConfig.getSourcePath()},
                targetConfig.getEncodings(), targetProgram.getCurrentTarget().getQualifyFileName());

        //get accessible variables

        MAccessVarVisitor mvv = new MAccessVarVisitor(root);
        root.accept(mvv);
        List<MethodAccessVars> methodLineLists = mvv.getMethodAccessVars();
        mvv = null;
//        logger.info("List<MethodAccessVars> : \n" + methodLineLists.toString());
//        logger.info("List<MethodAccessVars> -size:" + methodLineLists.size());
        tf.setMethodAccessVars(methodLineLists);

        polyu_af.utils.FileUtils.outputTfList(targetProgram.getTargetFiles());

        ExeCommand exeCommand = new ExeCommand(targetConfig);
        String command=exeCommand.getMLogAgCommand(tf.getQualifyFileName());
//        String command = exeCommand.getVarLogAgCommand(tf.getQualifyFileName());


        ExeTargetRuntime.process(command);

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

        if (tf != null) {
            faultUnitList = tf.getFaults();
            if (faultUnitList != null) {
                for (FaultUnit fu : faultUnitList) {
                    //find fault node in root
//                    ASTNode faultNode = AstUtils.findNodeInRoot(root, fu);
                    //resolve the input exp in the fault node
//                    ResolveExp resolveExp = new ResolveExp(faultNode, fu, root);
//                    resolveExp.resolveExp();
                    //testing modify the expression
//                    if (!fu.getExpression().equals("exp")) {
//                       getTargetConfig.saveNewFaultClass(
//                               AstUtils.parseExpRecordModifications(
//                                       root, fu.getExpression(), faultFileSource_));
//
//                    }
                }
            }
        }
        Runtime runtime = Runtime.getRuntime();
        logger.info("freeMemory:" + runtime.freeMemory() + "; totalMemory:" + runtime.totalMemory());
        long usedMemory = runtime.totalMemory() - runtime.freeMemory();
        logger.info("usedMemory:" + usedMemory);

        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        logger.info("totalTime:" + totalTime);

    }
}
