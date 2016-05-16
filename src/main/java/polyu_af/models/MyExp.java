package polyu_af.models;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;

/**
 * Created by liushanchen on 16/5/16.
 */
public class MyExp {
    private ASTNode astNode = null;
    private ITypeBinding type = null;
    private Class<?> value = null;

    public MyExp(ASTNode astNode, ITypeBinding type) {
        this.astNode = astNode;
        this.type = type;

    }

    public ASTNode getAstNode() {
        return astNode;
    }

    public String getAstNodeString() {
        return astNode.toString();
    }

    public String getAstNodeVar() {
        int nType = astNode.getNodeType();
        if (nType == ASTNode.VARIABLE_DECLARATION_FRAGMENT) {
            VariableDeclarationFragment vdf = (VariableDeclarationFragment) astNode;
            return vdf.getName().getIdentifier();
        } else if (nType == ASTNode.SINGLE_VARIABLE_DECLARATION) {
            SingleVariableDeclaration svd = (SingleVariableDeclaration) astNode;
            return svd.getName().toString();
        }
        return null;
    }

    public String getAstNodeType() {
        return ASTNode.nodeClassForType(astNode.getNodeType()).getSimpleName();
    }

    public void setAstNode(ASTNode astNode) {
        this.astNode = astNode;
    }

    public ITypeBinding getType() {
        return type;
    }


    public String getTypeName() {
        if (type.isPrimitive()) {
            return type.getQualifiedName();
        } else {
            return type.getBinaryName();
        }

    }

    public String getTypeQName() {
        return type.getQualifiedName();
    }

    public String getTypeBName() {
        return type.getBinaryName();
    }

    public void setType(ITypeBinding type) {
        this.type = type;
    }

    public Class<?> getValue() {
        return value;
    }

    public void setValue(Class<?> value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "\nExp{" +
                "astNode:" + getAstNodeVar() +
                ", nodeType:" + getAstNodeType() +
                "; type:" + getTypeName() +
//                "; value: " + value +
                '}';
    }
}
