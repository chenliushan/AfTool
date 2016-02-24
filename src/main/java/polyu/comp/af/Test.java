package polyu.comp.af;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;

/**
 * Created by liushanchen on 16/2/24.
 */
public class Test {
    public static void main(String arg[]){
        ASTParser parser = ASTParser.newParser(AST.JLS8);
        parser.setSource("".toCharArray());
        CompilationUnit unit = (CompilationUnit) parser.createAST(new NullProgressMonitor());
        unit.recordModifications();
        AST ast = unit.getAST();
    }



}
