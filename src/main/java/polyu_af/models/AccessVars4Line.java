package polyu_af.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liushanchen on 16/5/4.
 */
public class AccessVars4Line {
    private int location;
    private List<MyExpression> vars;
    private List<MyExpression> exps;

    public AccessVars4Line(int location) {
        this.location = location;
        vars = new ArrayList<MyExpression>();
        exps = new ArrayList<MyExpression>();
    }

    public AccessVars4Line(int location, List<MyExpression> vars) {
        this.location = location;
        this.vars = vars;
    }

    public void addVar(MyExpression var) {
        this.vars.add(var);
    }

    public void addVar(List<MyExpression> vars) {
        this.vars.addAll(vars);
    }

    public void addExp(List<MyExpression> exps) {
        this.exps.addAll(exps);
    }

    public void addExp(MyExpression exp) {
        this.exps.add(exp);
    }

    public int getLocation() {
        return location;
    }

    public List<MyExpression> getVars() {
        return vars;
    }

    @Override
    public String toString() {
        return "\n\nAccessVars4Line{" +
                "location=" + location +
                ", \n--vars:" + vars +
                ", \n--exps:" + exps +
                '}';
    }
}
