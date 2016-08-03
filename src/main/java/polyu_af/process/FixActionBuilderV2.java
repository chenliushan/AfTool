package polyu_af.process;

import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.PrimitiveType;
import polyu_af.models.Predicate;
import polyu_af.utils.CommonUtils;

import java.util.*;

/**
 * Created by liushanchen on 16/7/27.
 */
public class FixActionBuilderV2 {
    final static int NUM = 1;
    final static String join = "-";
    Map<String, Set<String>> fixActions;

    public FixActionBuilderV2() {
        this.fixActions = new Hashtable();
    }

    public Set<String> getFixActions(String key) {
        return fixActions.get(key);
    }

    private String generateKey(String leftVarS, String rightVarS) {
        return new StringBuilder(leftVarS).append(join).append(rightVarS).toString();
    }

    public Set<String> build(ITypeBinding type, String leftVarS, String rightVarS) {
        if (type == null) return null;
        if (leftVarS == null && rightVarS != null) {
            return building(type, rightVarS);
        } else if (leftVarS != null && rightVarS != null) {
            return building(type, leftVarS, rightVarS);
        }
        return null;
    }

    private Set<String> building(ITypeBinding type, String leftVarS, String rightVarS) {
        String name = type.getQualifiedName();
        String key = generateKey(leftVarS, rightVarS);
        if (fixActions.keySet().contains(key)) {
            return fixActions.get(key);
        } else {
            Set<String> fixs = new HashSet<>();
            if (type.isPrimitive()) {
                if (name.equals(PrimitiveType.BOOLEAN.toString())) {
                    fixAction4Boolean(fixs, leftVarS);
                    fixAction4Boolean(fixs, rightVarS);
                    fixAction4Boolean(fixs, leftVarS, rightVarS);
                    fixAction4Boolean(fixs, rightVarS, leftVarS);
                } else if (name.equals(PrimitiveType.DOUBLE.toString()) || type.equals(PrimitiveType.FLOAT.toString())
                        || name.equals(PrimitiveType.INT.toString()) || type.equals(PrimitiveType.LONG.toString())) {
                    fixAction4Num(fixs, leftVarS);
                    fixAction4Num(fixs, rightVarS);
                    fixAction4Num(fixs, leftVarS, rightVarS);
                    fixAction4Num(fixs, rightVarS, leftVarS);
                }
            } else {
                IMethodBinding[] methodBindings = type.getDeclaredMethods();
                fixAction4Ref(fixs, leftVarS, selectMethods(methodBindings));
                fixAction4Ref(fixs, rightVarS, selectMethods(methodBindings));
            }
            fixActions.put(key, fixs);
            fixActions.put(generateKey(rightVarS, leftVarS), fixs);
            return fixs;
        }
    }

    private Set<String> building(ITypeBinding type, String leftVarS) {
        String name = type.getQualifiedName();
        String key = leftVarS;
        if (fixActions.keySet().contains(key)) {
            return fixActions.get(key);
        } else {
            Set<String> fixs = new HashSet<>();
            if (type.isPrimitive()) {
                if (name.equals(PrimitiveType.BOOLEAN.toString())) {
                    fixAction4Boolean(fixs, leftVarS);
                } else if (name.equals(PrimitiveType.DOUBLE.toString()) || type.equals(PrimitiveType.FLOAT.toString())
                        || name.equals(PrimitiveType.INT.toString()) || type.equals(PrimitiveType.LONG.toString())) {
                    fixAction4Num(fixs, leftVarS);
                }
            } else {
                IMethodBinding[] methodBindings = type.getDeclaredMethods();
                fixAction4Ref(fixs, leftVarS, selectMethods(methodBindings));
            }
            fixActions.put(key, fixs);
            return fixs;
        }
    }

    private void fixAction4Boolean(Set<String> fixs, String varString) {
        fixs.add(new StringBuilder(varString).append(Assignment.Operator.ASSIGN)
                .append(varString).toString());
        fixs.add(new StringBuilder(varString).append(Assignment.Operator.ASSIGN)
                .append(Boolean.TRUE.toString()).toString());
        fixs.add(new StringBuilder(varString).append(Assignment.Operator.ASSIGN)
                .append(Boolean.FALSE.toString()).toString());
    }

    private void fixAction4Boolean(Set<String> fixs, String leftVarS, String rightVarS) {
        fixs.add(new StringBuilder(leftVarS).append(Assignment.Operator.ASSIGN)
                .append(rightVarS).toString());
        fixs.add(new StringBuilder(leftVarS).append(Assignment.Operator.ASSIGN)
                .append(Predicate.Operator.NOT).append(rightVarS).toString());
        fixs.add(new StringBuilder(leftVarS).append(Assignment.Operator.ASSIGN)
                .append(leftVarS).append(Predicate.Operator.CONDITIONAL_AND).append(rightVarS).toString());
        fixs.add(new StringBuilder(leftVarS).append(Assignment.Operator.ASSIGN)
                .append(leftVarS).append(Predicate.Operator.CONDITIONAL_OR).append(rightVarS).toString());

    }

    private void fixAction4Num(Set<String> fixs, String varString) {
        fixs.add(new StringBuilder(varString).append(Predicate.Operator.PLUS)
                .append(Predicate.Operator.PLUS).toString());
        fixs.add(new StringBuilder(varString).append(Predicate.Operator.MINUS)
                .append(Predicate.Operator.MINUS).toString());
    }

    private void fixAction4Num(Set<String> fixs, String leftVarS, String rightVarS) {
        fixs.add(new StringBuilder(leftVarS).append(Assignment.Operator.PLUS_ASSIGN)
                .append(rightVarS).toString());
        fixs.add(new StringBuilder(leftVarS).append(Assignment.Operator.MINUS_ASSIGN)
                .append(rightVarS).toString());
        fixs.add(new StringBuilder(leftVarS).append(Assignment.Operator.ASSIGN)
                .append(rightVarS).append(Predicate.Operator.PLUS).append(NUM).toString());
        fixs.add(new StringBuilder(leftVarS).append(Assignment.Operator.ASSIGN)
                .append(rightVarS).append(Predicate.Operator.MINUS).append(NUM).toString());
        fixs.add(new StringBuilder(leftVarS).append(Assignment.Operator.ASSIGN)
                .append(rightVarS).toString());
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


    private void printMethodBinding(Set<IMethodBinding> imbs) {
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
