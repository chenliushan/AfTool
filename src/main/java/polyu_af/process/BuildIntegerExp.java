package polyu_af.process;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jdt.core.dom.IVariableBinding;
import polyu_af.GlobalProcess;
import polyu_af.models.MyExpression;

import java.util.List;

/**
 * Created by liushanchen on 16/4/12.
 */
public class BuildIntegerExp extends BuildExpressions {

    private static Logger logger = LogManager.getLogger(GlobalProcess.class.getName());

    public BuildIntegerExp(List<MyExpression> accessibleVar) {
        super(accessibleVar);
    }

    @Override
    public void enumerating(IVariableBinding variableA, IVariableBinding variableB) {
        logger.info("enumeratingA: " + variableA.getName());
        logger.info("enumeratingB: " + variableB.getName());
        if (variableA.getType().getName().equals("int")&&variableB.getType().getName().equals("int")){
//            logger.info("build: " +);
        }
    }

}
