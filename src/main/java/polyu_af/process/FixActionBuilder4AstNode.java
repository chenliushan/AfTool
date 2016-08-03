package polyu_af.process;

import org.eclipse.jdt.core.dom.*;
import polyu_af.models.Predicate;
import polyu_af.utils.CommonUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by liushanchen on 16/7/27.
 */
public class FixActionBuilder4AstNode {
    final static int NUM=1;

    public Set<String> building(ASTNode suspect) {
        Set<String> fixs = new HashSet<>();

        if (suspect instanceof InfixExpression) {
            InfixExpression infixSuspect = (InfixExpression) suspect;
            building(fixs, infixSuspect);
        } else if (suspect instanceof PrefixExpression) {
            PrefixExpression prefixSuspect = (PrefixExpression) suspect;
            building(fixs, prefixSuspect);
        } else {
            int nType = suspect.getNodeType();
            switch (nType) {
                case ASTNode.VARIABLE_DECLARATION_FRAGMENT:
                    VariableDeclarationFragment vdf = (VariableDeclarationFragment) suspect;
                    analyzeITypeBinding(fixs, vdf.resolveBinding().getType(), vdf.getName().getIdentifier());
                    break;

                case ASTNode.SINGLE_VARIABLE_DECLARATION:
                    SingleVariableDeclaration svd = (SingleVariableDeclaration) suspect;
                    analyzeITypeBinding(fixs, svd.resolveBinding().getType(), svd.getName().toString());
                    break;

                case ASTNode.SINGLE_MEMBER_ANNOTATION:
                    SingleMemberAnnotation sma = (SingleMemberAnnotation) suspect;
                    analyzeITypeBinding(fixs, sma.resolveTypeBinding(), sma.getValue().resolveConstantExpressionValue().toString());
                    break;

                case ASTNode.INFIX_EXPRESSION:
                    InfixExpression ife = (InfixExpression) suspect;
                    analyzeITypeBinding(fixs, ife.resolveTypeBinding(), ife.toString());
                    break;

                case ASTNode.METHOD_INVOCATION:
                    MethodInvocation mi = (MethodInvocation) suspect;
                    analyzeITypeBinding(fixs, mi.resolveTypeBinding(), mi.toString());
                    break;

                case ASTNode.PREFIX_EXPRESSION:
                    PrefixExpression pi = (PrefixExpression) suspect;
                    analyzeITypeBinding(fixs, pi.resolveTypeBinding(), pi.toString());
                    break;

                default:
                    return null;

            }
        }
        return fixs;
    }


    /**
     * 不确定Expression如何得到var
     *
     * @param suspect
     */
    private void building(Set<String> fixs, InfixExpression suspect) {
        ITypeBinding suspectType = suspect.resolveTypeBinding();
        analyzeITypeBinding(fixs, suspectType, suspect.toString());
        ITypeBinding suspectLeftType = suspect.getLeftOperand().resolveTypeBinding();
        analyzeITypeBinding(fixs, suspectLeftType, suspect.getLeftOperand().toString());
        ITypeBinding suspectRightType = suspect.getLeftOperand().resolveTypeBinding();
        analyzeITypeBinding(fixs, suspectRightType, suspect.getLeftOperand().toString());

    }

    private void building(Set<String> fixs, PrefixExpression suspect) {
        ITypeBinding suspectType = suspect.resolveTypeBinding();
        analyzeITypeBinding(fixs, suspectType, suspect.toString());
        ITypeBinding suspectOperandType = suspect.getOperand().resolveTypeBinding();
        analyzeITypeBinding(fixs, suspectOperandType, suspect.getOperand().toString());

    }

    private void analyzeITypeBinding(Set<String> fixs, ITypeBinding type, String varString) {
        String name = type.getQualifiedName();
        if (type.isPrimitive()) {
            if (name.equals(PrimitiveType.BOOLEAN.toString())) {
                fixAction4Boolean(fixs, varString);
            } else if (name.equals(PrimitiveType.DOUBLE.toString()) || type.equals(PrimitiveType.FLOAT.toString())) {
                fixAction4Num(fixs, varString);

            } else if (name.equals(PrimitiveType.INT.toString()) || type.equals(PrimitiveType.LONG.toString())) {
                fixAction4Num(fixs, varString);
            }
        } else {
            IMethodBinding[] methodBindings = type.getDeclaredMethods();
            fixAction4Ref(fixs, varString, selectMethods(methodBindings));
        }
    }

    private void fixAction4Boolean(Set<String> fixs, String varString) {
        String action = Predicate.Operator.NOT + varString;
        fixs.add(action);
    }

    private void fixAction4Num(Set<String> fixs, String varString) {
        String action1 = varString + Predicate.Operator.PLUS + NUM;
        fixs.add(action1);
        String action2 = varString + Predicate.Operator.MINUS + NUM;
        fixs.add(action2);
    }

    private void fixAction4Ref(Set<String> fixs, String varString, List<IMethodBinding> imbs) {
        for (IMethodBinding imb : imbs) {
            String action = CommonUtils.appendInvoking(varString, imb.getName());
            fixs.add(action);
        }
    }


    private List<IMethodBinding> selectMethods(IMethodBinding[] methodBindings) {
        List<IMethodBinding> selectedMethod = new ArrayList<IMethodBinding>();
        for (int i = 0; i < methodBindings.length; i++) {
            IMethodBinding method = methodBindings[i];
            if (isValidParams(method) &&
//                    isValidMethodName(method)&&
                    isValidReturn(method)) {
                selectedMethod.add(method);
            }
        }
        return selectedMethod;
    }

    private boolean isValidMethodName(IMethodBinding method) {
        String invokingName = method.getName();
        switch (invokingName) {
            case "size":
                return true;
            case "isEmpty":
                return true;
            case "toString":
                return true;
            case "length":
                return true;

        }
        if (invokingName.startsWith("get")) {
            return true;
        }
        return false;
    }

    private boolean isValidReturn(IMethodBinding method) {
        String ReturnName = method.getReturnType().getName();
        if (ReturnName.equals(PrimitiveType.VOID.toString())) {
            return true;
        }
        return false;
    }

    private boolean isValidParams(IMethodBinding method) {
        ITypeBinding[] params = method.getParameterTypes();
        if (params.length == 0) {
            return true;
        }
        return false;
    }


    private void printMethodBinding(List<IMethodBinding> imbs) {
        for (IMethodBinding imb : imbs) {
            String retrun = imb.getReturnType().getBinaryName();
            ITypeBinding[] params = imb.getParameterTypes();
            StringBuilder sb = new StringBuilder(retrun).append("  ");
            sb.append(imb.getName());
            sb.append(" (");
            for (int j = 0; j < params.length; j++) {
                sb.append(params[j].getName()).append(",");
            }
            sb.append(");");
            System.out.println(sb.toString());
        }

    }
}
