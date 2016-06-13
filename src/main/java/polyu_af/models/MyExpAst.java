package polyu_af.models;

import org.eclipse.jdt.core.dom.*;
import polyu_af.exception.NotAcceptExpNodeTypeException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liushanchen on 16/5/16.
 */
public class MyExpAst implements MyExp {

    private transient ASTNode astNode = null;

    public MyExpAst(ASTNode astNode) throws NotAcceptExpNodeTypeException {
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
            case ASTNode.PREFIX_EXPRESSION:
                break;
            default:
                throw new NotAcceptExpNodeTypeException(getAstNodeType(astNode));
        }
        this.astNode = astNode;
    }

    public ASTNode getAstNode() {
        return astNode;
    }

    public String getNodeString() {
        return astNode.toString();
    }

    public String getExpVar() {
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
                return sma.getValue().resolveConstantExpressionValue().toString();
            case ASTNode.INFIX_EXPRESSION:
                InfixExpression ife = (InfixExpression) astNode;
                return ife.toString();
            case ASTNode.METHOD_INVOCATION:
                MethodInvocation mi = (MethodInvocation) astNode;
                return mi.toString();
            case ASTNode.PREFIX_EXPRESSION:
                PrefixExpression pi = (PrefixExpression) astNode;
                return pi.toString();
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

    public ITypeBinding getTypeBinding() {
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
            case ASTNode.PREFIX_EXPRESSION:
                PrefixExpression pi = (PrefixExpression) astNode;
                return pi.resolveTypeBinding();
            default:
                return null;
        }
    }


    public String getType() {

        ITypeBinding type = getTypeBinding();
        if (type == null) {
            return null;
        }
        if (type.isPrimitive()) {
            return type.getQualifiedName();
        } else {
            return type.getBinaryName();
        }
    }

    @Override
    public void setType(String type) {

    }

    @Override
    public void setExpVar(String expVar) {

    }

    public static String getTypeName(ITypeBinding type) {
        if (type == null) {
            return null;
        }
        if (type.isPrimitive()) {
            return type.getQualifiedName();
        } else {
            return type.getBinaryName();
        }
    }

//    public MyExpString getME() {
//        return new MyExpString(getTypeName(getTypeBinding()), getExpVar());
//    }


    @Override
    public String toString() {
        return "Exp{" +
                "nodeType: " + getAstNodeType() +
                "; type: " + getType() +
                "; var: " + getExpVar() +
                "}\n";
    }

}
