package polyu_af.deprecated;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;

/**
 * Created by liushanchen on 16/3/29.
 */
public class VariableScope {
    private int startLine;
    private int endLine;
    private static Logger logger = LogManager.getLogger(VariableScope.class.getName());

    public VariableScope(int startLine, int endLine) {

        this.endLine = endLine;
        this.startLine = startLine;
    }

    public int getEndLine() {
        return endLine;
    }

    public int getStartLine() {
        return startLine;
    }

    @Override
    public String toString() {
        return "VariableScope{" +
                "startLine=" + startLine +
                ", endLine=" + endLine +
                '}';
    }
    public static void expressionScope(final CompilationUnit root) {
        ASTVisitor test = new ASTVisitor() {
            @Override
            public void postVisit(ASTNode node) {

                logger.info("start line: " + root.getLineNumber(node.getStartPosition()));
                logger.info("end line: " + root.getLineNumber(node.getStartPosition() + node.getLength()));
                logger.info("node: " + node.toString());
                logger.info("getParent: " + ASTNode.nodeClassForType(node.getParent().getNodeType()));
                logger.info("============== ");
                super.postVisit(node);
            }
        };
        root.accept(test);
    }
}
