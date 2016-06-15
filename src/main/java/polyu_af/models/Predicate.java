package polyu_af.models;


import java.util.HashMap;
import java.util.Map;

/**
 * Created by liushanchen on 16/6/13.
 */
public class Predicate {

    private static String divider = " ";
    public static String nullContent = "null";

    private MyExp leftOperand;
    private MyExp rightOperand;
    private Predicate.Operator operator;


    public Predicate(MyExp rightOperand) {
        this.rightOperand = rightOperand;
    }

    public Predicate(Predicate.Operator operator, MyExp rightOperand) {
        this.operator = operator;
        this.rightOperand = rightOperand;
    }

    public Predicate(MyExp leftOperand, Predicate.Operator operator, MyExp rightOperand) {
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

    public Predicate.Operator getOperator() {
        return operator;
    }

    public void setOperator(Predicate.Operator operator) {
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
        } else if (rightOperand != null) {
            return rightOperand.getExpVar();
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
        return getPredicate() ;
    }


    public static class Operator {
        private String token;
        public static final Predicate.Operator NOT = new Predicate.Operator("!");
        public static final Predicate.Operator PLUS = new Predicate.Operator("+");
        public static final Predicate.Operator MINUS = new Predicate.Operator("-");
        public static final Predicate.Operator LESS = new Predicate.Operator("<");
        public static final Predicate.Operator GREATER = new Predicate.Operator(">");
        public static final Predicate.Operator LESS_EQUALS = new Predicate.Operator("<=");
        public static final Predicate.Operator GREATER_EQUALS = new Predicate.Operator(">=");
        public static final Predicate.Operator EQUALS = new Predicate.Operator("==");
        public static final Predicate.Operator NOT_EQUALS = new Predicate.Operator("!=");
        public static final Predicate.Operator CONDITIONAL_OR = new Predicate.Operator("||");
        public static final Predicate.Operator CONDITIONAL_AND = new Predicate.Operator("&&");
        private static final Map CODES = new HashMap(20);

        static {
            Predicate.Operator[] ops = new Predicate.Operator[]{NOT, PLUS, MINUS, LESS, GREATER, LESS_EQUALS, GREATER, GREATER_EQUALS, EQUALS, NOT_EQUALS, CONDITIONAL_AND, CONDITIONAL_OR};
            for (int i = 0; i < ops.length; ++i) {
                CODES.put(ops[i].toString(), ops[i]);
            }

        }

        private Operator(String token) {
            this.token = token;
        }

        public String toString() {
            return this.token;
        }

        public static Predicate.Operator toOperator(String token) {
            return (Predicate.Operator) CODES.get(token);
        }
    }
}
