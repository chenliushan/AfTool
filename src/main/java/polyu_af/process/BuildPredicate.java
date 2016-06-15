package polyu_af.process;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
    private List<Predicate> predicates;//临时的用来存储所有的predicate防止过多创建新对象。因为不同行可能有较多相同的Snapshot
    private static Logger logger = LogManager.getLogger(BuildSnapshot.class.getName());

    public BuildPredicate() {
        this.predicates = new ArrayList<Predicate>();
    }

    /**
     * build the predicates of a line
     *
     * @param myExps all the accessible variables of a line
     * @return 返回某行的所有可能predicate
     */
    public List<Predicate> buildPredicate(List<MyExp> myExps) {
        List<Predicate> possibleP = new ArrayList<Predicate>();
        Iterator<MyExp> myExpIter = myExps.iterator();
        while (myExpIter.hasNext()) {
            MyExp meI = myExpIter.next();
            if (meI.getType().equals(PrimitiveType.BOOLEAN.toString())) {
                List<Predicate> pl = buildBooleanPredicate(meI);
                if (pl != null) {
                    possibleP.addAll(pl);
                }
            }
            for (MyExp me : myExps) {
                if (me != meI && meI.getType().equals(me.getType())) {
                    if (meI.getType().equals(PrimitiveType.INT.toString())
                            || meI.getType().equals(PrimitiveType.CHAR.toString())
                            || meI.getType().equals(PrimitiveType.SHORT.toString())
                            || meI.getType().equals(PrimitiveType.LONG.toString())
                            || meI.getType().equals(PrimitiveType.FLOAT.toString())
                            || meI.getType().equals(PrimitiveType.DOUBLE.toString())) {
                        List<Predicate> pl = buildIntPredicate(me, meI);
                        if (pl != null)
                            possibleP.addAll(pl);
                    } else if (me.getType().equals(PrimitiveType.BOOLEAN.toString())) {

                        List<Predicate> pl = buildBooleanPredicate(me, meI);
                        if (pl != null)
                            possibleP.addAll(pl);
                    } else if (me.getType().equals(PrimitiveType.BYTE.toString())
                            || me.getType().equals(PrimitiveType.VOID.toString())) {
                        logger.info("byte or void:");
                    }else{

                    }
                }
            }
        }

        return possibleP;
    }

    /**
     * @param left
     * @param right
     * @return
     */
    private List<Predicate> buildIntPredicate(MyExp left, MyExp right) {
        if (left == null || right == null) return null;
        List<Predicate> possibleP = new ArrayList<>();
        addSS(possibleP, new Predicate(left, Predicate.Operator.LESS, right));
        addSS(possibleP, new Predicate(left, Predicate.Operator.GREATER, right));
        addSS(possibleP, new Predicate(left, Predicate.Operator.LESS_EQUALS, right));
        addSS(possibleP, new Predicate(left, Predicate.Operator.GREATER_EQUALS, right));
        addSS(possibleP, new Predicate(left, Predicate.Operator.EQUALS, right));
        addSS(possibleP, new Predicate(left, Predicate.Operator.NOT_EQUALS, right));
        return possibleP;
    }

    private List<Predicate> buildBooleanPredicate(MyExp left, MyExp right) {
        if (left == null || right == null) return null;
        List<Predicate> possibleP = new ArrayList<>();
        addSS(possibleP, new Predicate(left, Predicate.Operator.CONDITIONAL_AND, right));
        addSS(possibleP, new Predicate(left, Predicate.Operator.CONDITIONAL_OR, right));
        addSS(possibleP, new Predicate(left, Predicate.Operator.EQUALS, right));
        addSS(possibleP, new Predicate(left, Predicate.Operator.NOT_EQUALS, right));
        return possibleP;
    }

    private List<Predicate> buildBooleanPredicate(MyExp exp) {
        if (exp == null) return null;
        List<Predicate> possibleP = new ArrayList<Predicate>();
        addSS(possibleP, new Predicate(exp));
        addSS(possibleP, new Predicate(Predicate.Operator.NOT,exp));
        return possibleP;
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
