package polyu_af.process;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import polyu_af.models.*;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by liushanchen on 16/6/13.
 */
public class BuildSnapshot {
    private static Logger logger = LogManager.getLogger(BuildSnapshot.class.getName());

    private TcLine tcLine;
    private ExpValue tmpEv;

    public BuildSnapshot(TcLine tcLine) {
        this.tcLine = tcLine;
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
                ssList.add(new Snapshot(tcLine.getLocation(), p, value));

            } else if (p.getRightOperand() != null) {
                String rightVal = getExpValue(p.getRightOperand());
                if (p.getOperator() == null) {
                    ssList.add(new Snapshot(tcLine.getLocation(), p, getBoolean(rightVal)));
                } else if (Predicate.Operator.NOT.toString().equals(p.getOperator().toString())) {
                    ssList.add(new Snapshot(tcLine.getLocation(), p, !getBoolean(rightVal)));
                } else {
                }
            }
        }
        return ssList;
    }

    private String getExpValue(MyExp me) {
        if (tmpEv != null && tmpEv.getExp().equals(me)) {
            return tmpEv.getValueString();
        }
        for (ExpValue ev : tcLine.getExpValueList()) {
            if (ev.getExp().equals(me)) {
                tmpEv = ev;
                return ev.getValueString();
            }
        }
        return null;
    }

    private boolean equalsLeft_Right(String leftVal, String rightVal) {
        if (leftVal != null && rightVal != null) {
            return leftVal.equals(rightVal);
        } else if (leftVal == null && rightVal == null) {
            return true;
        } else {
            return false;
        }
    }

    private boolean greaterLeft_Right(String leftVal, String rightVal) {
        return getIntFormStringV(leftVal) > getIntFormStringV(rightVal);
    }

    private boolean greaterEqualsLeft_Right(String leftVal, String rightVal) {
        return getIntFormStringV(leftVal) >= getIntFormStringV(rightVal);
    }

    private boolean lessLeft_Right(String leftVal, String rightVal) {
        return getIntFormStringV(leftVal) < getIntFormStringV(rightVal);
    }

    private boolean lessEqualsLeft_Right(String leftVal, String rightVal) {
        return getIntFormStringV(leftVal) <= getIntFormStringV(rightVal);
    }


    private boolean conditionalOrLeft_Right(String leftVal, String rightVal) {
        return Boolean.valueOf(leftVal) || Boolean.valueOf(rightVal);
    }

    private boolean conditionalAndLeft_Right(String leftVal, String rightVal) {
        return Boolean.valueOf(leftVal) && Boolean.valueOf(rightVal);
    }

    private boolean getBoolean(String val) {
        return Boolean.valueOf(val);
    }

    private double getIntFormStringV(String val) {
        if (val != null && val.length() > 0) {
            return Double.valueOf(val);
        }
        logger.info("var is not initialized" + tmpEv);
        return -1;
    }

}
