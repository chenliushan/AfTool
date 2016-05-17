package polyu_af.models;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ITypeBinding;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liushanchen on 16/5/4.
 */
public class LineAccessVars {
    private int location;
    private List<MyExp> vars;
    private List<MyExp> exps;

    public LineAccessVars(int location) {
        this.location = location;
        vars = new ArrayList<MyExp>();
        exps = new ArrayList<MyExp>();
    }

    public LineAccessVars(int location, List<MyExp> vars) {
        this.location = location;
        this.vars = vars;
    }

    public void addVar(MyExp var) {
        this.vars.add(var);
    }

    public void addVar(List<MyExp> vars) {
        this.vars.addAll(vars);
    }

    public void addExp(List<MyExp> exps) {
        this.exps.addAll(exps);
    }

    public void addExp(MyExp exp) {
        this.exps.add(exp);
    }

    public int getLocation() {
        return location;
    }

    public List<MyExp> getVars() {
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

    public class ExpVal extends MyExp {


        public ExpVal(ASTNode astNode, ITypeBinding type) {
            super(astNode, type);
        }



    }
}
