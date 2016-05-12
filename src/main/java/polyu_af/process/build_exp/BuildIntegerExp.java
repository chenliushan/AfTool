package polyu_af.process.build_exp;

import org.eclipse.jdt.core.dom.IVariableBinding;
import polyu_af.models.MyExpression;

import java.util.List;

/**
 * Created by liushanchen on 16/4/12.
 */
public class BuildIntegerExp extends BuildExpressions {

    public BuildIntegerExp(List<MyExpression> accessibleVar) {
        super(accessibleVar);
    }

    @Override
    public void enumerating(IVariableBinding variableA, IVariableBinding variableB) {
        if (variableA.getType().getName().equals("int") && variableB.getType().getName().equals("int")) {
            MyExpression myExpression = new MyExpression(null, variableA.getName() + " + " + variableB.getName(),"int");
            buildExp.add(myExpression);
        }
    }

}
