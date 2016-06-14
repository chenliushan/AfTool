package polyu_af.models;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.InfixExpression;
import org.eclipse.jdt.core.dom.MethodInvocation;
import polyu_af.utils.AstUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liushanchen on 16/5/4.
 */
public class LineVars extends FixLine{
    public static boolean addInvokingMethod = false;
    private List<MyExp> varsList;
    private transient List<Predicate> predicates;


    public LineVars(int location) {
        super(location);
        this.varsList = new ArrayList<MyExp>();
    }

    public List<Predicate> getPredicates() {
        return predicates;
    }

    public void setPredicates(List<Predicate> predicates) {
        this.predicates = predicates;
    }

    public void addVar(MyExpAst var) {
        this.varsList.add(var);
        if (addInvokingMethod) {
            List<MyExpInv> myInvokingMethod = new MyExpInvokingMethod().getInvokingMethod(var);
            if (myInvokingMethod != null) {
                this.varsList.addAll(myInvokingMethod);
            }
        }
    }


    public void addVar(List<MyExpAst> vars) {
        for (MyExpAst mea : vars) {
            addVar(mea);
        }
    }

    public List<MyExp> getVarsList() {
        return varsList;
    }

    /**
     * if the exp is valid in line then add this exp in this line
     *
     * @param myExpAstList
     * @return
     */
    public boolean addExpInLine(List<MyExpAst> myExpAstList) {
        for (MyExpAst me : myExpAstList) {
            ASTNode astNode = me.getAstNode();
            int nType = me.getAstNode().getNodeType();
            switch (nType) {
                case ASTNode.SINGLE_MEMBER_ANNOTATION:
                    String smaValue = me.getExpVar();
                    Expression expAst = AstUtils.createExpAST(smaValue);
                    if (expAst != null && checkExpInType(expAst)) {
                        addVar(me);
                    }
                    break;
                case ASTNode.INFIX_EXPRESSION:
                    InfixExpression ife = (InfixExpression) astNode;

                    if (checkExpInType(ife.getLeftOperand()) && checkExpInType(ife.getRightOperand())) {
                        addVar(me);
                    }
                    break;
                case ASTNode.METHOD_INVOCATION:
                    MethodInvocation mi = (MethodInvocation) astNode;

                    break;
                default:
                    break;
            }

        }

        return false;
    }


    private boolean checkExpInString(String e) {
        for (MyExp me : varsList) {
            if (me.getExpVar().equals(e)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断vars在此行是否有效(存在相同类型与值的表达式)
     *
     * @param e
     * @return
     */
    private boolean checkExpInType(Expression e) {
        int nType = e.getNodeType();
        //is primitive type?
        if (nType == ASTNode.NUMBER_LITERAL || nType == ASTNode.STRING_LITERAL || nType == ASTNode.NULL_LITERAL) {
            return true;
        }
        //if there is exp in same type and value?
        if (e.resolveTypeBinding() != null) {
            for (MyExp me : varsList) {
                if (me.getType() != null && e.resolveTypeBinding() != null) {
                    if ((me.getType().equals(e.resolveTypeBinding().getBinaryName())
                            || me.getType().equals(e.resolveTypeBinding().getQualifiedName()))
                            && me.getExpVar().equals(e.toString())) {
//                        if (me.getType().getBinaryName().equals(e.resolveTypeBinding().getBinaryName())
//                                && me.getExpVar().equals(e.toString())) {
                        return true;
                    }
                } else {
                    return checkExpInString(e.toString());
                }
            }
        } else {
            return checkExpInString(e.toString());
        }
        return false;
    }

    @Override
    public String toString() {
        return "LineVars{" +
                "location=" + location +
                ", varsList=" + varsList +
                ", predicates=" + predicates +
                '}';
    }

    public static class LavPara {
        public static final String LOCATION = "location";
        public static final String VARS_LIST = "varsList";
    }

}
