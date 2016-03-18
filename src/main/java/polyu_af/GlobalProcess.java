package polyu_af;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;
import polyu_af.domain.FaultFile;
import polyu_af.domain.FaultUnit;
import polyu_af.domain.InputFile;
import polyu_af.utils.AstUtils;
import polyu_af.utils.MyUtils;

import java.util.List;

/**
 * Created by liushanchen on 16/3/17.
 */
public class GlobalProcess {
    private static Logger logger = LogManager.getLogger(GlobalProcess.class.getName());

    public static void main(String arg[]) {
        /*
        read input file
         */
        InputFile inputFile = MyUtils.getInput(System.getProperty("user.dir") + "/input/InputFile_AfTest_1");
        FaultFile faultFile = null;
        List<FaultUnit> faultUnitList = null;
        if (inputFile != null && inputFile.getFaultFileList() != null){
            faultFile = inputFile.getFaultFileList().get(0);
        }

        /*
        create faultFileAST --root
         */
        char[] faultFileSource = inputFile.getSource(faultFile.getSourceName());
        CompilationUnit root = AstUtils.createResolvedAST(faultFileSource, inputFile.getClasspathEntries(), inputFile.getSourcepathEntries(), inputFile.getEncodings());

        if (faultFile != null){
            faultUnitList = faultFile.getFaults();
            if (faultUnitList != null) {
                for (FaultUnit fu : faultUnitList) {
                /*
                find fault nodes in root
                visit the node's children and resolve their type
                 */
                    ASTNode faultNode = AstUtils.findNodeInRoot(root, fu);
                    faultNode.accept(AstUtils.resolveType);
                }
            }
        }
    }

}
