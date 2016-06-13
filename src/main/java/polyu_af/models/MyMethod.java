package polyu_af.models;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liushanchen on 16/5/4.
 */
public class MyMethod {
//    private Logger logger = LogManager.getLogger();
    private String declaringClass=null;//this attribute is null in the TargetFile method list
    private String methodName;
    private List<String> paramTypes;
    private List<LineVars> varsList;
    private List<LineState> lineStateList;

    public MyMethod(String methodName) {
        this.methodName = methodName;
        varsList = new ArrayList<LineVars>(0);
        paramTypes = new ArrayList<String>(0);
        lineStateList = new ArrayList<LineState>(0);

    }

    public MyMethod(String declaringClass, String methodName) {
        this.declaringClass = declaringClass;
        this.methodName = methodName;
        varsList = new ArrayList<LineVars>();
        paramTypes = new ArrayList<String>();
    }

    public void addLine(LineVars vars) {
        varsList.add(vars);
    }

    public void addLine(List<LineVars> vars) {
        varsList.addAll(vars);
    }

    public String getMethodName() {
        return methodName;
    }
    public String getLongName() {
        StringBuilder sb=new StringBuilder(methodName);
        sb.append("(");
        for(String p:paramTypes){
            sb.append(p);
            sb.append(",");
        }
        if(sb.toString().endsWith(",")){
            sb.deleteCharAt(sb.length()-1);
        }

        sb.append(")");
        return sb.toString();
    }

    public List<LineVars> getVarsList() {
        return varsList;
    }

    public List<String> getParams() {
        return paramTypes;
    }

    /**
     * 获取参数类型的qualified name
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

    public List<LineState> getLineStateList() {
        return lineStateList;
    }

    public void addLineState(LineState lineState) {
        this.lineStateList.add(lineState);
    }

    public void setVarsList(List<LineVars> varsList) {
        this.varsList = varsList;
    }

    @Override
    public String toString() {
        return "\nMyMethod{" +
                "declaringClass='" + declaringClass + '\'' +
                ", methodName='" + methodName + '\'' +
                ", paramTypes=" + paramTypes +
                ", varsList=" + varsList +
                '}';
    }

    public static class MavPara{
        public static final String METHOD_NAME="methodName";
        public static final String PARAM_TYPES="paramTypes";
        public static final String VARS_LIST="varsList";
    }
}
