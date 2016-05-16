package polyu_af.models;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liushanchen on 16/5/4.
 */
public class MethodAccessVars {
    private static Logger logger = LogManager.getLogger();

    private String methodName;
    private List<String> paramTypes;
    private List<LineAccessVars> varsList;

    public MethodAccessVars(String methodName) {
        this.methodName = methodName;
        varsList = new ArrayList<LineAccessVars>();
        paramTypes = new ArrayList<String>();

    }


    public void addLine(LineAccessVars vars) {
        varsList.add(vars);
    }

    public void addLine(List<LineAccessVars> vars) {
        varsList.addAll(vars);
    }

    public String getMethodName() {
        return methodName;
    }

    public List<LineAccessVars> getVarsList() {
        return varsList;
    }

    public List<String> getParams() {
        return paramTypes;
    }

    public List<CtClass> getParams(ClassPool classPool) {
        List<CtClass> ccParamTypes = new ArrayList<CtClass>();
        for (String p : paramTypes) {
            try {
                CtClass ccp = classPool.getCtClass(p);
                ccParamTypes.add(ccp);
            } catch (NotFoundException e) {
                logger.error("p:" + p);
                e.printStackTrace();
            }
        }
        return ccParamTypes;
    }

    public void addParamType(String paramType) {
        this.paramTypes.add(paramType);

    }


    @Override
    public String toString() {
        return "\n\nAccessVar4Method{" +
                "methodName='" + methodName + '\'' +
                ", paramTypes=" + paramTypes +
                ", varsList=" + varsList +
                '}';
    }
}
