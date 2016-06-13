package polyu_af.models;

import org.eclipse.jdt.core.dom.InfixExpression;
import org.eclipse.jdt.core.dom.PrimitiveType;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by liushanchen on 16/6/13.
 */
public class BuildSnapshot {
    private List<Snapshot> snapshots;

    public BuildSnapshot() {
        this.snapshots = new ArrayList<Snapshot>();
    }

    /**
     * build the snapshots of a line
     *
     * @param myExps all the accessible variables of a line
     * @return
     */
    public List<Snapshot> buildSnapShot(List<MyExp> myExps) {
        List<Snapshot> possibleSS = new ArrayList<>();
        Iterator<MyExp> myExpIter = myExps.iterator();
        for (MyExp me : myExps) {
            if (me.getType().equals(PrimitiveType.BOOLEAN.toString())) {
                possibleSS.addAll(buildBooleanSnapshot(me));
            }
            while (myExpIter.hasNext()) {
                MyExp meI = myExpIter.next();
//                if (meI.equals(me)) {
//                    myExpIter.remove();//这里要使用Iterator的remove方法移除当前对象，如果使用List的remove方法，则同样会出现ConcurrentModificationException
//                }
                if (meI.getType().equals(me.getType())) {
                    if (meI.getType().equals(PrimitiveType.INT.toString())) {
                        possibleSS.addAll(buildIntSnapshot(me, meI));
                    }
                    if (meI.getType().equals(PrimitiveType.BOOLEAN.toString())) {
                        possibleSS.addAll(buildBooleanSnapshot(me, meI));
                    }
                }
            }
        }

        return possibleSS;
    }

    /**
     * public static final InfixExpression.Operator LESS = new InfixExpression.Operator("<");
     * public static final InfixExpression.Operator GREATER = new InfixExpression.Operator(">");
     * public static final InfixExpression.Operator LESS_EQUALS = new InfixExpression.Operator("<=");
     * public static final InfixExpression.Operator GREATER_EQUALS = new InfixExpression.Operator(">=");
     * public static final InfixExpression.Operator EQUALS = new InfixExpression.Operator("==");
     * public static final InfixExpression.Operator NOT_EQUALS = new InfixExpression.Operator("!=");
     * public static final InfixExpression.Operator CONDITIONAL_OR = new InfixExpression.Operator("||");
     * public static final InfixExpression.Operator CONDITIONAL_AND = new InfixExpression.Operator("&&");
     *
     * @param left
     * @param right
     * @return
     */
    private List<Snapshot> buildIntSnapshot(MyExp left, MyExp right) {
        if (left == null || right == null) return null;
        List<Snapshot> possibleSS = new ArrayList<>();
        addSS(possibleSS, new Snapshot(left, InfixExpression.Operator.LESS, right));
        addSS(possibleSS, new Snapshot(left, InfixExpression.Operator.GREATER, right));
        addSS(possibleSS, new Snapshot(left, InfixExpression.Operator.LESS_EQUALS, right));
        addSS(possibleSS, new Snapshot(left, InfixExpression.Operator.GREATER_EQUALS, right));
        addSS(possibleSS, new Snapshot(left, InfixExpression.Operator.EQUALS, right));
        addSS(possibleSS, new Snapshot(left, InfixExpression.Operator.NOT_EQUALS, right));
        return possibleSS;
    }


    private List<Snapshot> buildBooleanSnapshot(MyExp left, MyExp right) {
        if (left == null || right == null) return null;
        List<Snapshot> possibleSS = new ArrayList<>();
        addSS(possibleSS, new Snapshot(left, InfixExpression.Operator.CONDITIONAL_AND, right));
        addSS(possibleSS, new Snapshot(left, InfixExpression.Operator.CONDITIONAL_OR, right));
        addSS(possibleSS, new Snapshot(left, InfixExpression.Operator.EQUALS, right));
        addSS(possibleSS, new Snapshot(left, InfixExpression.Operator.NOT_EQUALS, right));
        addSS(possibleSS, new Snapshot(left));
        addSS(possibleSS, new Snapshot(right));
        return possibleSS;
    }

    private List<Snapshot> buildBooleanSnapshot(MyExp left) {
        if (left == null) return null;
        List<Snapshot> possibleSS = new ArrayList<>();
        addSS(possibleSS, new Snapshot(left));
        return possibleSS;
    }


    /**
     * not useful. reference should be an object and has an boolean method invocation --".isEmpty()"
     *
     * @param left
     * @return
     */
    @Deprecated
    private List<Snapshot> buildReferenceSnapshot(MyExp left) {
        if (left == null) return null;
        List<Snapshot> possibleSS = new ArrayList<>();

        return possibleSS;
    }

    private void addSS(List<Snapshot> sslist, Snapshot ss) {
        if (sslist == null) return;
        int idx = snapshots.indexOf(ss);
        if (idx > -1) {
            sslist.add(snapshots.get(idx));
        } else {
            sslist.add(ss);
            snapshots.add(ss);
        }

    }


}
