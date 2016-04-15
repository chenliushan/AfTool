package polyu_af.process.build_exp;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jdt.core.dom.IVariableBinding;
import polyu_af.models.MyExpression;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by liushanchen on 16/4/12.
 */
public abstract class BuildExpressions {


    protected List<MyExpression> accessibleVar = null;
    protected List<MyExpression> buildExp = null;


    public BuildExpressions(List<MyExpression> accessibleVar) {
        this.accessibleVar = accessibleVar;
        buildExp = new ArrayList<MyExpression>();
    }

    public List<MyExpression> getBuildExp() {
        return buildExp;
    }

    public void buildingExps() {
        List<MyExpression> variablesList = new ArrayList<MyExpression>(accessibleVar);
        for (Iterator<MyExpression> it = variablesList.iterator(); it.hasNext(); ) {
            IVariableBinding varA = it.next().getTypeBinding();
            it.remove();
            if (varA != null) {
                for (MyExpression e : variablesList) {
                    enumerating(varA, e.getTypeBinding());
                }
            }
        }
    }

    public abstract void enumerating(IVariableBinding variableA, IVariableBinding variableB);
}
