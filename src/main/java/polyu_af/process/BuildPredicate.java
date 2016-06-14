package polyu_af.process;

import org.eclipse.jdt.core.dom.InfixExpression;
import org.eclipse.jdt.core.dom.PrimitiveType;
import polyu_af.models.MyExp;
import polyu_af.models.Predicate;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by liushanchen on 16/6/13.
 */
public class BuildPredicate {
    private List<Predicate> predicates;

    public BuildPredicate() {
        this.predicates = new ArrayList<Predicate>();
    }

    /**
     * build the predicates of a line
     *
     * @param myExps all the accessible variables of a line
     * @return
     */
    public List<Predicate> buildSnapShot(List<MyExp> myExps) {
        List<Predicate> possibleP = new ArrayList<>();
        Iterator<MyExp> myExpIter = myExps.iterator();
        for (MyExp me : myExps) {
            if (me.getType().equals(PrimitiveType.BOOLEAN.toString())) {
                List<Predicate> pl = buildBooleanSnapshot(me);
                if (pl != null)
                    possibleP.addAll(pl);
            }
            while (myExpIter.hasNext()) {
                MyExp meI = myExpIter.next();
//                if (meI.equals(me)) {
//                    myExpIter.remove();//这里要使用Iterator的remove方法移除当前对象，如果使用List的remove方法，则同样会出现ConcurrentModificationException
//                }
                if (me != meI && meI.getType().equals(me.getType())) {
                    if (meI.getType().equals(PrimitiveType.INT.toString())) {
                        List<Predicate> pl = buildIntSnapshot(me, meI);
                        if (pl != null)
                            possibleP.addAll(pl);
                    }
                    if (meI.getType().equals(PrimitiveType.BOOLEAN.toString())) {
                        List<Predicate> pl = buildBooleanSnapshot(me, meI);
                        if (pl != null)
                            possibleP.addAll(pl);
                    }
                }
            }
        }

        return possibleP;
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
    private List<Predicate> buildIntSnapshot(MyExp left, MyExp right) {
        if (left == null || right == null) return null;
        List<Predicate> possibleP = new ArrayList<>();
        addSS(possibleP, new Predicate(left, InfixExpression.Operator.LESS, right));
        addSS(possibleP, new Predicate(left, InfixExpression.Operator.GREATER, right));
        addSS(possibleP, new Predicate(left, InfixExpression.Operator.LESS_EQUALS, right));
        addSS(possibleP, new Predicate(left, InfixExpression.Operator.GREATER_EQUALS, right));
        addSS(possibleP, new Predicate(left, InfixExpression.Operator.EQUALS, right));
        addSS(possibleP, new Predicate(left, InfixExpression.Operator.NOT_EQUALS, right));
        return possibleP;
    }


    private List<Predicate> buildBooleanSnapshot(MyExp left, MyExp right) {
        if (left == null || right == null) return null;
        List<Predicate> possibleP = new ArrayList<>();
        addSS(possibleP, new Predicate(left, InfixExpression.Operator.CONDITIONAL_AND, right));
        addSS(possibleP, new Predicate(left, InfixExpression.Operator.CONDITIONAL_OR, right));
        addSS(possibleP, new Predicate(left, InfixExpression.Operator.EQUALS, right));
        addSS(possibleP, new Predicate(left, InfixExpression.Operator.NOT_EQUALS, right));
        addSS(possibleP, new Predicate(left));
        addSS(possibleP, new Predicate(right));
        return possibleP;
    }

    private List<Predicate> buildBooleanSnapshot(MyExp left) {
        if (left == null) return null;
        List<Predicate> possibleSS = new ArrayList<>();
        addSS(possibleSS, new Predicate(left));
        return possibleSS;
    }


    /**
     * not useful. reference should be an object and has an boolean method invocation --".isEmpty()"
     *
     * @param left
     * @return
     */
    @Deprecated
    private List<Predicate> buildReferenceSnapshot(MyExp left) {
        if (left == null) return null;
        List<Predicate> possibleSS = new ArrayList<>();

        return possibleSS;
    }

    private void addSS(List<Predicate> sslist, Predicate ss) {
        if (sslist == null) return;
        int idx = predicates.indexOf(ss);
        if (idx > -1) {
            sslist.add(predicates.get(idx));
        } else {
            sslist.add(ss);
            predicates.add(ss);

        }

    }


}
