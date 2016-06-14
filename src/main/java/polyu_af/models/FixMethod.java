package polyu_af.models;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liushanchen on 16/6/14.
 */
public abstract class FixMethod {
    String methodName;
    List<String> paramTypes;

    public FixMethod(String methodName) {
        this.methodName = methodName;
        paramTypes = new ArrayList<String>(0);
    }

    public FixMethod(String methodName, List<String> paramTypes) {
        this.methodName = methodName;
        this.paramTypes = paramTypes;
    }

    public String getMethodName() {
        return methodName;
    }

    public String getLongName() {
        StringBuilder sb = new StringBuilder(methodName);
        sb.append("(");
        for (String p : paramTypes) {
            sb.append(p);
            sb.append(",");
        }
        if (sb.toString().endsWith(",")) {
            sb.deleteCharAt(sb.length() - 1);
        }

        sb.append(")");
        return sb.toString();
    }

    public List<String> getParams() {
        return paramTypes;
    }

    /**
     * 获取参数类型的qualified name
     *
     * @param classPool
     * @return
     */
    public List<CtClass> getParams(ClassPool classPool) {
        List<CtClass> ccParamTypes = new ArrayList<CtClass>();
        for (String p : paramTypes) {
            try {
                CtClass ccp = classPool.getCtClass(p);
                ccParamTypes.add(ccp);
            } catch (NotFoundException e) {
                e.printStackTrace();
            }
        }
        return ccParamTypes;
    }

    public void addParamType(String paramType) {
        this.paramTypes.add(paramType);

    }

    public void setParamTypes(List<String> paramTypes) {
        this.paramTypes = paramTypes;
    }

}
