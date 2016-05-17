package polyu_af.models;

import org.eclipse.jdt.core.dom.*;

/**
 * Created by liushanchen on 16/5/16.
 */
public class MyExp {
    private ASTNode astNode = null;
    private ITypeBinding type = null;
    private Object value = null;

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
        }else if(nType==ASTNode.SINGLE_MEMBER_ANNOTATION){
            SingleMemberAnnotation sma=(SingleMemberAnnotation)astNode;
            return sma.getValue().toString();
        }
//        else if(nType==ASTNode.ASSIGNMENT){
//            Assignment assign= (Assignment)astNode;
//            int rh=assign.getRightHandSide().getNodeType();
//            return ASTNode.nodeClassForType(rh).getName();
//        }else if(nType==ASTNode.METHOD_INVOCATION){
//            MethodInvocation mi= (MethodInvocation)astNode;
//            return mi.toString();
//        }else if(nType==ASTNode.METHOD_INVOCATION){
//            PostfixExpression pe= (PostfixExpression)astNode;
//            return pe.toString();
//        }

        return astNode.toString();
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

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
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
