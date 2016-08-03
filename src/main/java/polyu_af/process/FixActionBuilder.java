package polyu_af.process;

import org.eclipse.jdt.core.dom.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liushanchen on 16/7/27.
 */
public class FixActionBuilder {

    private void building(Expression suspect) {
        if (suspect instanceof InfixExpression) {
            InfixExpression infixSuspect = (InfixExpression) suspect;
            building(infixSuspect);
        } else if (suspect instanceof PrefixExpression) {
            PrefixExpression prefixSuspect = (PrefixExpression) suspect;
            building(prefixSuspect);
        }

    }

    private void building(InfixExpression suspect) {
        ITypeBinding suspectLeftType =suspect.getLeftOperand().resolveTypeBinding();
        analyzeITypeBinding(suspectLeftType);
        ITypeBinding suspectRightType =suspect.getLeftOperand().resolveTypeBinding();
        analyzeITypeBinding(suspectRightType);

    }

    private void building(PrefixExpression suspect) {
        ITypeBinding suspectType = suspect.resolveTypeBinding();
        analyzeITypeBinding(suspectType);
        ITypeBinding suspectOperandType =suspect.getOperand().resolveTypeBinding();
        analyzeITypeBinding(suspectOperandType);

    }

    private void analyzeITypeBinding(ITypeBinding type) {
        String name = type.getQualifiedName();
        if (type.isPrimitive()) {

        } else {
            IMethodBinding[] methodBindings = type.getDeclaredMethods();
            System.out.println("Start:"+name);
            printMethodBinding(selectMethods(methodBindings));
            System.out.println("End:"+name);

        }
    }

    private IMethodBinding[] selectMethods(IMethodBinding[] methodBindings) {
        List<IMethodBinding> selectedMethod = new ArrayList<IMethodBinding>();
        for (int i = 0; i < methodBindings.length; i++) {
            IMethodBinding method = methodBindings[i];
            if (isValidParams(method) &&
//                    isValidMethodName(method)&&
                    isValidReturn(method)) {
                selectedMethod.add(method);
            }
        }
        return (IMethodBinding[]) selectedMethod.toArray();
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


    private void printMethodBinding(IMethodBinding[] imbs) {
        for (int i = 0; i < imbs.length; i++) {
            String retrun = imbs[i].getReturnType().getBinaryName();
            ITypeBinding[] params = imbs[i].getParameterTypes();
            StringBuilder sb = new StringBuilder(retrun).append("  ");
            sb.append(imbs[i].getName());
            sb.append(" (");
            for (int j = 0; j < params.length; j++) {
                sb.append(params[i].getName()).append(",");
            }
            sb.append(");");
            System.out.println(sb.toString());
        }

    }
}
