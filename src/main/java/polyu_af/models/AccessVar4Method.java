package polyu_af.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liushanchen on 16/5/4.
 */
public class AccessVar4Method {
    private String methodName;
    private List<AccessibleVars> varsList;

    public AccessVar4Method(String methodName) {
        this.methodName = methodName;
        varsList = new ArrayList<AccessibleVars>();
    }

    public void addLine(AccessibleVars vars) {
        varsList.add(vars);
    }

    public void addLine(List<AccessibleVars> vars) {
        varsList.addAll(vars);
    }

    public String getMethodName() {
        return methodName;
    }

    public List<AccessibleVars> getVarsList() {
        return varsList;
    }

    @Override
    public String toString() {
        return "\n\nAccessVar4Method{" +
                "methodName:'" + methodName + '\'' +
                ", varsList:" + varsList +
                '}';
    }
}
