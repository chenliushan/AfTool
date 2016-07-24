package polyu_af.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liushanchen on 16/5/4.
 */
public class MyMethod extends FixMethod {
    private String declaringClass = null;//this attribute is null in the TargetFileOld method list

    private List<LineVars> varsList;

    public MyMethod(String methodName) {
        super(methodName);
        varsList = new ArrayList<LineVars>(0);

    }

    public MyMethod(String declaringClass, String methodName) {
        super(methodName);
        this.declaringClass = declaringClass;
        varsList = new ArrayList<LineVars>();
    }

    public void addLine(LineVars vars) {
        varsList.add(vars);
    }

    public void addLine(List<LineVars> vars) {
        varsList.addAll(vars);
    }


    public List<LineVars> getLineVarsList() {
        return varsList;
    }


    public void setVarsList(List<LineVars> varsList) {
        this.varsList = varsList;
    }

    @Override
    public String toString() {
        return "MyMethod{" +
                "declaringClass='" + declaringClass + '\'' +
                ", methodName='" + methodName + '\'' +
                ", paramTypes=" + paramTypes +
                ", varsList=" + varsList +
                '}';
    }

    public static class MavPara {
        public static final String METHOD_NAME = "methodName";
        public static final String PARAM_TYPES = "paramTypes";
        public static final String VARS_LIST = "varsList";
    }
}
