package polyu_af.process.build_exp;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jdt.core.dom.IVariableBinding;
import polyu_af.models.MyExpression;
import polyu_af.utils.CommonUtils;

import java.util.List;

/**
 * Created by liushanchen on 16/4/12.
 */
public class BuildBooleanExp extends BuildExpressions {
    private static Logger logger = LogManager.getLogger(BuildBooleanExp.class.getName());

    public BuildBooleanExp(List<MyExpression> accessibleVar) {
        super(accessibleVar);
    }

    @Override
    public void enumerating(IVariableBinding variableA, IVariableBinding variableB) {
        if(variableA==null||variableB==null){
            return;
        }
//        logger.info("xxxxxx:"+variableB.getType().getName()+variableB.getName());

        if (variableA.getType().getName().equals("boolean") && variableB.getType().getName().equals("boolean")) {
            MyExpression myExpression = new MyExpression(null, variableA.getName() + " && " + variableB.getName(),"boolean");
            buildExp.add(myExpression);
        }
    }

}
