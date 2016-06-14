package polyu_af.models;

import org.eclipse.jdt.core.dom.InfixExpression;

/**
 * Created by liushanchen on 16/6/13.
 */
public class Predicate {

    private static String divider = " ";
    public static String nullContent = "null";

    private MyExp leftOperand;
    private MyExp rightOperand;
    private InfixExpression.Operator operator;

    public Predicate() {
    }

    public Predicate(MyExp leftOperand) {
        this.leftOperand = leftOperand;
    }

    public Predicate(InfixExpression.Operator operator, MyExp rightOperand) {
        this.operator = operator;
        this.rightOperand = rightOperand;
    }

    public Predicate(MyExp rightOperand, InfixExpression.Operator operator, MyExp leftOperand) {
        this.rightOperand = rightOperand;
        this.operator = operator;
        this.leftOperand = leftOperand;
    }

    public MyExp getLeftOperand() {
        return leftOperand;
    }

    public void setLeftOperand(MyExp leftOperand) {
        this.leftOperand = leftOperand;
    }

    public InfixExpression.Operator getOperator() {
        return operator;
    }

    public void setOperator(InfixExpression.Operator operator) {
        this.operator = operator;
    }

    public MyExp getRightOperand() {
        return rightOperand;
    }

    public void setRightOperand(MyExp rightOperand) {
        this.rightOperand = rightOperand;
    }

    public String getPredicate() {
        if (operator != null && leftOperand != null && rightOperand != null) {
            return leftOperand.getExpVar() + divider + operator.toString() + divider + rightOperand.getExpVar();
        } else if (operator != null && rightOperand != null) {
            return operator.toString() + divider + rightOperand.getExpVar();
        } else if (leftOperand != null) {
            return leftOperand.getExpVar();
        }
        return null;
    }

    @Override
    public boolean equals(Object obj) {
        if (super.equals(obj)) return true;
        if (obj != null && obj instanceof Predicate) {
            Predicate anotherP = (Predicate) obj;
            if (anotherP == this ||
                    (this.leftOperand == anotherP.getLeftOperand()
                            && rightOperand == anotherP.getRightOperand()
                            && operator == anotherP.getOperator())) {
                return true;
            }
        }

        return false;
    }

    @Override
    public String toString() {
        return getPredicate() + "\n";
    }
}
