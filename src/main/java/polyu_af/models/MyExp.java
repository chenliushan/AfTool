package polyu_af.models;

import org.eclipse.jdt.core.dom.*;
import polyu_af.exception.NotAcceptExpNodeTypeException;

/**
 * Created by liushanchen on 16/5/16.
 */
public class MyExp {
    private ASTNode astNode = null;

    public MyExp(ASTNode astNode) throws NotAcceptExpNodeTypeException {
        int nType = astNode.getNodeType();
        switch (nType) {
            case ASTNode.VARIABLE_DECLARATION_FRAGMENT:
                break;
            case ASTNode.SINGLE_VARIABLE_DECLARATION:
                break;
            case ASTNode.SINGLE_MEMBER_ANNOTATION:
                break;
            case ASTNode.INFIX_EXPRESSION:
                break;
            case ASTNode.METHOD_INVOCATION:
                break;
            default:
                throw new NotAcceptExpNodeTypeException(getAstNodeType(astNode));
        }
        this.astNode = astNode;
    }

    public ASTNode getAstNode() {
        return astNode;
    }

    public String getAstNodeString() {
        return astNode.toString();
    }

    public String getAstNodeVar() {
        int nType = astNode.getNodeType();
        switch (nType) {
            case ASTNode.VARIABLE_DECLARATION_FRAGMENT:
                VariableDeclarationFragment vdf = (VariableDeclarationFragment) astNode;
                return vdf.getName().getIdentifier();
            case ASTNode.SINGLE_VARIABLE_DECLARATION:
                SingleVariableDeclaration svd = (SingleVariableDeclaration) astNode;
                return svd.getName().toString();
            case ASTNode.SINGLE_MEMBER_ANNOTATION:
                SingleMemberAnnotation sma = (SingleMemberAnnotation) astNode;
                return sma.getValue().toString();
            case ASTNode.INFIX_EXPRESSION:
                InfixExpression ife = (InfixExpression) astNode;
                return ife.toString();
            case ASTNode.METHOD_INVOCATION:
                MethodInvocation mi = (MethodInvocation) astNode;
                return mi.toString();
            default:
                return astNode.toString();
        }
    }

    public String getAstNodeType() {
        return ASTNode.nodeClassForType(astNode.getNodeType()).getSimpleName();
    }

    public static String getAstNodeType(ASTNode node) {
        return ASTNode.nodeClassForType(node.getNodeType()).getSimpleName();
    }

    public void setAstNode(ASTNode astNode) {
        this.astNode = astNode;
    }

    public ITypeBinding getType() {
        int nType = astNode.getNodeType();
        switch (nType) {
            case ASTNode.VARIABLE_DECLARATION_FRAGMENT:
                VariableDeclarationFragment vdf = (VariableDeclarationFragment) astNode;
                return vdf.resolveBinding().getType();
            case ASTNode.SINGLE_VARIABLE_DECLARATION:
                SingleVariableDeclaration svd = (SingleVariableDeclaration) astNode;
                return svd.resolveBinding().getType();
            case ASTNode.SINGLE_MEMBER_ANNOTATION:
                SingleMemberAnnotation sma = (SingleMemberAnnotation) astNode;
                return sma.resolveTypeBinding();
            case ASTNode.INFIX_EXPRESSION:
                InfixExpression ife = (InfixExpression) astNode;
                return ife.resolveTypeBinding();
            case ASTNode.METHOD_INVOCATION:
                MethodInvocation mi = (MethodInvocation) astNode;
                return mi.resolveTypeBinding();
            default:
                return null;
        }
    }


    public String getTypeName() {
        ITypeBinding type = getType();
        if (type.isPrimitive()) {
            return type.getQualifiedName();
        } else {
            return type.getBinaryName();
        }
    }


    @Override
    public String toString() {
        return "\nExp{" +
                "astNode:" + getAstNodeVar() +
                ", nodeType:" + getAstNodeType() +
                "; type:" + getTypeName() +
                '}';
    }
}
