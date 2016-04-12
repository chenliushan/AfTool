package polyu_af;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jdt.core.dom.CompilationUnit;
import polyu_af.models.*;
import polyu_af.process.AccessibleVariables;
import polyu_af.process.BuildIntegerExp;
import polyu_af.utils.AstUtils;
import polyu_af.utils.CommonUtils;
import polyu_af.utils.ReadFileUtils;

import java.util.List;
import java.util.Map;

/**
 * Created by liushanchen on 16/3/17.
 */
public class GlobalProcess {
    private static Logger logger = LogManager.getLogger(GlobalProcess.class.getName());

    public static void main(String arg[]) {
        //read input file
        GetConfiguration getConfiguration=new GetConfiguration(System.getProperty("user.dir") + "/input/InputFile_AfTest_1");
        TargetProgram targetProgram = getConfiguration.getTargetProgram();
        FaultClass faultClass = null;
        List<FaultUnit> faultUnitList = null;
        if (targetProgram != null && targetProgram.getFaultClassList() != null) {
            faultClass = targetProgram.getFaultClassList().get(0);
        } else {
            return;
        }
        //create faultFileAST --root
        String faultFileSource_ = targetProgram.getSource(faultClass.getSourceName());
        CompilationUnit root = AstUtils.createResolvedAST(faultFileSource_,
                targetProgram.getClasspathEntries(), targetProgram.getSourcepathEntries(),
                targetProgram.getEncodings(), faultClass.getSourceName());

//        root.accept(AstUtils.findDeclaration);
        //get accessible variables
        AccessibleVariables accessibleVariables=new AccessibleVariables(root);
        root.accept(accessibleVariables);
        Map<Integer, List<MyExpression>> accessibleVar=accessibleVariables.getAccessibleVariables();
        CommonUtils.printMap(accessibleVar);

        //build expression with accessible variables
        BuildIntegerExp buildIntegerExp= new BuildIntegerExp(accessibleVar.get(34));
        buildIntegerExp.buildingExps();

        Runtime runtime=Runtime.getRuntime();
        logger.info("freeMemory:"+runtime.freeMemory()+"; totalMemory:"+runtime.totalMemory());

//        ReadFileUtils.printMap(AstUtils.astForm);
//        logger.info("${logger}"+${logger});

        if (faultClass != null) {
            faultUnitList = faultClass.getFaults();
            if (faultUnitList != null) {
                for (FaultUnit fu : faultUnitList) {
                    //find fault node in root
//                    ASTNode faultNode = AstUtils.findNodeInRoot(root, fu);
                    //resolve the input exp in the fault node
//                    ResolveExp resolveExp = new ResolveExp(faultNode, fu, root);
//                    resolveExp.resolveExp();
                    //testing modify the expression
                    if (!fu.getExpression().equals("exp")) {
                       getConfiguration.saveNewFaultClass(
                               AstUtils.parseExpRecordModifications(
                                       root, fu.getExpression(), faultFileSource_));

                    }
                }
            }
        }
    }
}
