package polyu_af;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jdt.core.dom.CompilationUnit;
import polyu_af.exception.NotFoundException;
import polyu_af.models.*;
import polyu_af.process.AccessibleVarVisitor4M;
import polyu_af.process.FixRuntime;
import polyu_af.utils.AstUtils;

import java.util.List;

/**
 * Created by liushanchen on 16/3/17.
 */
public class GlobalProcess {
    private static Logger logger = LogManager.getLogger(GlobalProcess.class.getName());

    public static void main(String arg[]) {
        //read input file
        GetConfiguration getConfiguration=null;
        try {
//            getConfiguration = new GetConfiguration(System.getProperty("user.dir") + "/input/InputFile_AfTest_1");
            getConfiguration = new GetConfiguration(System.getProperty("user.dir") + "/input/InputFile_Test4Javassist.txt");
        } catch (NotFoundException e) {
            e.printStackTrace();
        }
        TargetProgram targetProgram = getConfiguration.getTargetProgram();
        TargetClass targetClass = null;
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
        AccessibleVarVisitor4M accessibleVariables4m = new AccessibleVarVisitor4M(root);
        root.accept(accessibleVariables4m);
        List<AccessVar4Method> accessVar4MethodList =accessibleVariables4m.getAccessVar4MethodList();
        logger.info("accessVar4MethodList: \n"+ accessVar4MethodList.toString());
        logger.info("accessVar4MethodList-size:"+ accessVar4MethodList.size());

        FixRuntime fixRuntime=new FixRuntime(targetProgram);
        fixRuntime.process(accessVar4MethodList,targetClass.getSourceName());

        //build expression with accessible variables
//        BuildIntegerExp buildIntegerExp= new BuildIntegerExp(accessibleVar.get(34));
//        buildIntegerExp.buildingExps();
//        CommonUtils.printExpList(buildIntegerExp.getBuildExp());
//        BuildBooleanExp buildBooleanExp=new BuildBooleanExp(accessibleVar.get(148));
//        buildBooleanExp.buildingExps();
//        CommonUtils.printExpList(buildBooleanExp.getBuildExp());


//        Runtime runtime=Runtime.getRuntime();
//        logger.info("freeMemory:"+runtime.freeMemory()+"; totalMemory:"+runtime.totalMemory());

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
//                       getConfiguration.saveNewFaultClass(
//                               AstUtils.parseExpRecordModifications(
//                                       root, fu.getExpression(), faultFileSource_));
//
//                    }
                }
            }
        }
    }
}
