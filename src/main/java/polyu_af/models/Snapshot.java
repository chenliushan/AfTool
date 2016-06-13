package polyu_af.models;

import org.eclipse.jdt.core.dom.InfixExpression;

/**
 * Created by liushanchen on 16/6/13.
 */
public class Snapshot {
    private MyExp leftOperand;
    private MyExp rightOperand;
    private InfixExpression.Operator operator;
    private static String divider = " ";
    public static String nullContent = "null";

    public Snapshot(MyExp rightOperand, InfixExpression.Operator operator, MyExp leftOperand) {
        this.rightOperand = rightOperand;
        this.operator = operator;
        this.leftOperand = leftOperand;
    }

    public Snapshot(InfixExpression.Operator operator, MyExp rightOperand) {
        this.operator = operator;
        this.rightOperand = rightOperand;
    }

    public Snapshot(MyExp leftOperand) {
        this.leftOperand = leftOperand;
    }

    public Snapshot() {
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

    public String getSnapshot() {
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
    public String toString() {
        return "Snapshot{" +
                "leftOperand:" + leftOperand +
                ", rightOperand:" + rightOperand +
                ", operator:" + operator +
                '}';
    }
}
