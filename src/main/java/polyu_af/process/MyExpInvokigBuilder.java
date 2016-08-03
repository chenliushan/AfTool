package polyu_af.process;

import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.PrimitiveType;
import polyu_af.exception.NotAcceptExpNodeTypeException;
import polyu_af.models.MyExpAst;
import polyu_af.models.MyExpInv;
import polyu_af.utils.CommonUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liushanchen on 16/6/12.
 */
public class MyExpInvokigBuilder {


    public static List<MyExpInv> getInvokingMethod(MyExpAst myExpAst) {
        ITypeBinding type = myExpAst.getTypeBinding();
        if (type == null) return null;
        if (type.isPrimitive()) {
            return null;
        } else {
            List<MyExpInv> invokingMyExpList = new ArrayList<MyExpInv>();
            try {
                invokingMyExpList.add(newIfExpNullInv(myExpAst));
            } catch (NotAcceptExpNodeTypeException e) {
                e.printStackTrace();
            }
            IMethodBinding[] methodBindings = type.getDeclaredMethods();
            if (methodBindings == null) return null;
            for (int i = 0; i < methodBindings.length; i++) {
                String returnType = MyExpAst.getTypeName(methodBindings[i].getReturnType());
                String invokingName = methodBindings[i].getName();
                if (methodBindings[i].getParameterTypes().length == 0
                        && !returnType.equals(PrimitiveType.VOID.toString()) && isValidInvoking(invokingName)) {
                    String invocation = CommonUtils.appendInvoking(myExpAst.getExpVar(), invokingName);
                    MyExpInv invokingMyExp = null;
                    try {
                        invokingMyExp = new MyExpInv(myExpAst.getAstNode(), returnType, invocation);
                    } catch (NotAcceptExpNodeTypeException e) {
                        e.printStackTrace();
                    }
                    invokingMyExpList.add(invokingMyExp);
                }
            }
            return invokingMyExpList;
        }
    }


    public static boolean isValidInvoking(String invokingName) {
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

    private static MyExpInv newIfExpNullInv(MyExpAst myExpAst) throws NotAcceptExpNodeTypeException {
        StringBuilder sb = new StringBuilder("(");
        sb.append(myExpAst.getExpVar()).append("==null)");
        MyExpInv ifExpNull = new MyExpInv(myExpAst.getAstNode(), PrimitiveType.BOOLEAN.toString(), sb.toString());
        return ifExpNull;
    }

}
