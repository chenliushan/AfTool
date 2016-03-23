package polyu_af;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jdt.core.ITypeRoot;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.InfixExpression;
import org.eclipse.jface.text.Document;
import polyu_af.domain.FaultFile;
import polyu_af.domain.FaultUnit;
import polyu_af.domain.InputFile;
import polyu_af.process.ResolveExp;
import polyu_af.utils.AstUtils;
import polyu_af.utils.ReadFileUtils;

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
        InputFile inputFile = ReadFileUtils.getInput(System.getProperty("user.dir") + "/input/InputFile_AfTest_1");
        FaultFile faultFile = null;
        List<FaultUnit> faultUnitList = null;
        if (inputFile != null && inputFile.getFaultFileList() != null) {
            faultFile = inputFile.getFaultFileList().get(0);
        } else {
            return;
        }
        /*
        create faultFileAST --root
         */
        String faultFileSource_ = inputFile.getSource(faultFile.getSourceName());
        CompilationUnit root = AstUtils.createResolvedAST(faultFileSource_,
                inputFile.getClasspathEntries(), inputFile.getSourcepathEntries(),
                inputFile.getEncodings(), faultFile.getSourceName());

        if (faultFile != null) {
            faultUnitList = faultFile.getFaults();
            if (faultUnitList != null) {
                for (FaultUnit fu : faultUnitList) {
                    //find fault node in root
                    ASTNode faultNode = AstUtils.findNodeInRoot(root, fu);
                    //resolve the input exp in the fault node
                    ResolveExp resolveExp = new ResolveExp(faultNode, fu, root);
                    resolveExp.resolveExp();
                    //testing modify the expression
//                    if (!fu.getExpression().equals("exp")) {
//                        AstUtils.parseExpRecordModifications(root, fu.getExpression(), faultFileSource_);
//                    }
                }
            }
        }
    }
}
