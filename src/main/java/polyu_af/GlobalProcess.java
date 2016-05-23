package polyu_af;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jdt.core.dom.CompilationUnit;
import polyu_af.exception.NotFoundException;
import polyu_af.models.*;
import polyu_af.process.*;
import polyu_af.utils.AstUtils;

import java.util.List;

/**
 * Created by liushanchen on 16/3/17.
 */
public class GlobalProcess {
    private static Logger logger = LogManager.getLogger(GlobalProcess.class.getName());

    public static void main(String arg[]) {
        long startTime = System.currentTimeMillis();
        TargetProgram targetProgram=null;
        TargetClass targetClass = null;

        //read input file
        GetTargetProgram getTargetProgram =null;
        try {
            getTargetProgram = new GetTargetProgram(System.getProperty("user.dir") + "/input/InputFile_AfTest_1");
//            getTargetProgram = new GetTargetProgram(System.getProperty("user.dir") + "/input/InputFile_Test4Javassist.txt");
        } catch (NotFoundException e) {
            e.printStackTrace();
            return;
        }
         targetProgram = getTargetProgram.getTargetProgram();

        List<FaultUnit> faultUnitList = null;
        if (targetProgram != null && targetProgram.getTargetClassList() != null) {
            targetClass = targetProgram.getTargetClassList().get(0);
        } else {
            return;
        }
        //create faultFileAST --root
        String faultFileSource_ = targetProgram.getSource(targetClass.getSourceName());
        CompilationUnit root = AstUtils.createResolvedAST(faultFileSource_,
                targetProgram.getClasspathEntries(), new String[]{targetProgram.getSourcePath()},
                targetProgram.getEncodings(), targetClass.getSourceName());

        //get accessible variables

        MAccessVarVisitor mvv =new MAccessVarVisitor(root);
        root.accept(mvv);
        List<MethodAccessVars> lineLists= mvv.getMethodAccessVars();
        logger.info("List<MethodAccessVars> : \n"+ lineLists.toString());
        logger.info("List<MethodAccessVars> -size:"+ lineLists.size());

        ByteCodeP byteCodeP =new ByteCodeP(targetProgram);
        byteCodeP.process(lineLists);


        //build expression with accessible variables
//        BuildIntegerExp buildIntegerExp= new BuildIntegerExp(accessibleVar.get(34));
//        buildIntegerExp.buildingExps();
//        CommonUtils.printExpList(buildIntegerExp.getBuildExp());
//        BuildBooleanExp buildBooleanExp=new BuildBooleanExp(accessibleVar.get(148));
//        buildBooleanExp.buildingExps();
//        CommonUtils.printExpList(buildBooleanExp.getBuildExp());


//
//        ReadFileUtils.printMap(AstUtils.astForm);
//        logger.info("${logger}"+${logger});

        if (targetClass != null) {
            faultUnitList = targetClass.getFaults();
            if (faultUnitList != null) {
                for (FaultUnit fu : faultUnitList) {
                    //find fault node in root
//                    ASTNode faultNode = AstUtils.findNodeInRoot(root, fu);
                    //resolve the input exp in the fault node
//                    ResolveExp resolveExp = new ResolveExp(faultNode, fu, root);
//                    resolveExp.resolveExp();
                    //testing modify the expression
//                    if (!fu.getExpression().equals("exp")) {
//                       getTargetProgram.saveNewFaultClass(
//                               AstUtils.parseExpRecordModifications(
//                                       root, fu.getExpression(), faultFileSource_));
//
//                    }
                }
            }
        }
        Runtime runtime=Runtime.getRuntime();
        logger.info("freeMemory:"+runtime.freeMemory()+"; totalMemory:"+runtime.totalMemory());
        long usedMemory=runtime.totalMemory()-runtime.freeMemory();
        logger.info("usedMemory:"+usedMemory);

        long endTime   = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        logger.info("totalTime:"+totalTime);

    }
}
