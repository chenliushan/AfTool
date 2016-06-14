package polyu_af.process;

import polyu_af.models.ExpValue;
import polyu_af.models.MyExp;
import polyu_af.models.Predicate;
import polyu_af.models.Snapshot;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by liushanchen on 16/6/13.
 */
public class EvaluateSnapshot {
    List<ExpValue> expValueList;//ExpValues of a line;
    int location;//line number

    public EvaluateSnapshot(int location, List<ExpValue> expValueList) {
        this.expValueList = expValueList;
        this.location = location;
    }

    /**
     * @param predicates of a line
     * @return
     */
    public List<Snapshot> buildSnapshot(List<Predicate> predicates) {
        if (predicates == null) return null;
        List<Snapshot> ssList = new ArrayList<Snapshot>();
        for (Predicate p : predicates) {
            if (p.getLeftOperand() != null && p.getRightOperand() != null) {
                String operator = p.getOperator().toString();
                String leftVal = getExpValue(p.getLeftOperand());
                String rightVal = getExpValue(p.getRightOperand());
                if (leftVal == null || rightVal == null) continue;
                boolean value = false;
                switch (operator) {
                    case "<":
                        value = lessLeft_Right(leftVal, rightVal);
                        break;
                    case ">":
                        value = greaterLeft_Right(leftVal, rightVal);
                        break;
                    case "<=":
                        value = lessEqualsLeft_Right(leftVal, rightVal);
                        break;
                    case ">=":
                        value = greaterEqualsLeft_Right(leftVal, rightVal);
                        break;
                    case "||":
                        value = conditionalOrLeft_Right(leftVal, rightVal);
                        break;
                    case "&&":
                        value = conditionalAndLeft_Right(leftVal, rightVal);
                        break;
                    case "!=":
                        value = !equalsLeft_Right(leftVal, rightVal);
                        break;
                    case "==":
                        value = equalsLeft_Right(leftVal, rightVal);
                        break;
                    default:
                        continue;
                }
                ssList.add(new Snapshot(location, p, value));
            }
        }
        return ssList;
    }

    private String getExpValue(MyExp me) {
        for (ExpValue ev : expValueList) {
//            if (ev.getExp().getType().equals(me.getType()) && ev.getExp().getExpVar().equals(me.getExpVar())) {
            if (ev.getExp().equals(me)) {
                System.out.println("ev.getExp().equals(me)"+me);
                return ev.getValueString();
            }
        }
        System.out.println("cannot getExpValue"+me);
        return null;
    }

    private boolean equalsLeft_Right(String leftVal, String rightVal) {
        return leftVal.equals(rightVal);
    }

    private boolean greaterLeft_Right(String leftVal, String rightVal) {
        boolean value = Integer.valueOf(leftVal) > Integer.valueOf(rightVal);
        return value;
    }

    private boolean greaterEqualsLeft_Right(String leftVal, String rightVal) {
        boolean value = Integer.valueOf(leftVal) >= Integer.valueOf(rightVal);
        return value;
    }

    private boolean lessLeft_Right(String leftVal, String rightVal) {
        boolean value = Integer.valueOf(leftVal) < Integer.valueOf(rightVal);
        return value;
    }

    private boolean lessEqualsLeft_Right(String leftVal, String rightVal) {
        boolean value = Integer.valueOf(leftVal) <= Integer.valueOf(rightVal);
        return value;
    }


    private boolean conditionalOrLeft_Right(String leftVal, String rightVal) {
        boolean value = Boolean.valueOf(leftVal) || Boolean.valueOf(rightVal);
        return value;
    }

    private boolean conditionalAndLeft_Right(String leftVal, String rightVal) {
        boolean value = Boolean.valueOf(leftVal) && Boolean.valueOf(rightVal);
        return value;
    }

}
