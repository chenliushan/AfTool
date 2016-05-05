package polyu_af.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liushanchen on 16/5/4.
 */
public class AccessibleVars {
    private int location;
    private List<MyExpression> vars;

    public AccessibleVars(int location) {
        this.location=location;
        vars = new ArrayList<MyExpression>();
    }

    public AccessibleVars(int location, List<MyExpression> vars) {
        this.location = location;
        this.vars = vars;
    }

    public void addVar(MyExpression var) {
        this.vars.add(var);
    }
    public void addVar(List<MyExpression> var) {
        this.vars.addAll(var);
    }

    public int getLocation() {
        return location;
    }

    public List<MyExpression> getVars() {
        return vars;
    }

    @Override
    public String toString() {
        return "AccessibleVars{" +
                "location:" + location +
                ", vars:" + vars +
                "}\n";
    }
}
