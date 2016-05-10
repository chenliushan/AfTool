package polyu_af.models;

import org.eclipse.jdt.core.dom.*;

/**
 * Created by liushanchen on 16/4/1.
 */
public class MyExpression {
    private ASTNode astNode = null;
    private String text = null;

    public MyExpression(ASTNode astNode, String text) {
        this.astNode = astNode;
        this.text = text;
    }

    public ASTNode getAstNode() {
        return astNode;
    }

    public void setAstNode(ASTNode astNode) {
        this.astNode = astNode;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public IVariableBinding getTypeBinding() {
        if (astNode instanceof VariableDeclarationFragment) {
            VariableDeclarationFragment varFragment = (VariableDeclarationFragment) astNode;
            return varFragment.resolveBinding();
        } else if (astNode instanceof SingleVariableDeclaration) {
            SingleVariableDeclaration singleVarDec = (SingleVariableDeclaration) astNode;
            return singleVarDec.resolveBinding();
        } else {
            return null;
        }
    }

    @Override
    public String toString() {
        return "\nMyExp{" +
                "astNode:" + astNode+
                ", text:\'" + text + '\'' +
                '}';
    }
}
