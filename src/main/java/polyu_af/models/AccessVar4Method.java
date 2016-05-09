package polyu_af.models;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jdt.core.Signature;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liushanchen on 16/5/4.
 */
public class AccessVar4Method {
    private static Logger logger = LogManager.getLogger();

    private String methodName;
    private List<String> paramTypes;
    private List<AccessibleVars> varsList;

    public AccessVar4Method(String methodName) {
        this.methodName = methodName;
        varsList = new ArrayList<AccessibleVars>();
        paramTypes = new ArrayList<String>();

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
