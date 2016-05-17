package polyu_af.models;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jdt.core.dom.*;
import polyu_af.utils.AstUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by liushanchen on 16/5/4.
 */
public class LineAccessVars {
    private static Logger logger = LogManager.getLogger();

    private int location;
    private Map<String, MyExp> vars;


    public LineAccessVars(int location) {
        this.location = location;
        vars = new HashMap<String, MyExp>();
    }

    public LineAccessVars(int location, List<MyExp> vars) {
        this.location = location;
    }

    public void addVar(MyExp var) {
        this.vars.put(var.getAstNodeVar(), var);
    }

    public void addVar(Map<String, MyExp> vars) {
        this.vars.putAll(vars);
    }

    public int getLocation() {
        return location;
    }

    public Map<String, MyExp> getVars() {
        return vars;
    }

    @Override
    public String toString() {
        return "\n\nLineAccessVars{" +
                "location=" + location +
                ", \n--vars:" + vars +
                '}';
    }

    public boolean addExpInLine(List<MyExp> myExpList) {
        for (MyExp me : myExpList) {
            ASTNode astNode = me.getAstNode();
            int nType = me.getAstNode().getNodeType();
            switch (nType) {
                case ASTNode.VARIABLE_DECLARATION_FRAGMENT:
                    VariableDeclarationFragment vdf = (VariableDeclarationFragment) astNode;
                    break;
                case ASTNode.SINGLE_VARIABLE_DECLARATION:
                    SingleVariableDeclaration svd = (SingleVariableDeclaration) astNode;
                    break;
                case ASTNode.SINGLE_MEMBER_ANNOTATION:
                    SingleMemberAnnotation sma = (SingleMemberAnnotation) astNode;
                    String smaValue = sma.getValue().resolveConstantExpressionValue().toString();
                    Expression expAst = AstUtils.createExpAST(smaValue);
                    if(expAst!=null &&checkExpInType(expAst)){
                        vars.put(me.getAstNodeVar(), me);
                    }
                    break;
                case ASTNode.INFIX_EXPRESSION:
                    InfixExpression ife = (InfixExpression) astNode;
                    if (checkExpInType(ife.getLeftOperand()) && checkExpInType(ife.getRightOperand())) {
                        vars.put(me.getAstNodeVar(), me);
                    }
                    break;
                case ASTNode.METHOD_INVOCATION:
                    MethodInvocation mi = (MethodInvocation) astNode;
//                    logger.info("METHOD_INVOCATION:" + mi );
//                    logger.info("METHOD_INVOCATION:" + mi.getExpression() );

//                    if (checkExpInType(mi.getExpression())) {
//                        logger.info("METHOD_INVOCATION:" + mi + "----valid");
//                        vars.put(me.getAstNodeVar(), me);
//                    }

                    break;
                default:
                    logger.info("not match type:" + astNode.toString());
                    break;
            }

        }

        return false;
    }

    private boolean checkExpInString(String e) {
        for (Map.Entry<String, MyExp> var : vars.entrySet()) {
            if (var.getKey().equals(e.toString())) {
                return true;
            }
        }
        return false;
    }

    private boolean checkExpInType(Expression e) {
        int nType = e.getNodeType();
        if (nType == ASTNode.NUMBER_LITERAL || nType == ASTNode.STRING_LITERAL || nType == ASTNode.NULL_LITERAL) {
            return true;
        }
        if (e.resolveTypeBinding() != null) {
            for (Map.Entry<String, MyExp> var : vars.entrySet()) {
                if (var.getValue().getType() != null) {
                    if (var.getValue().getType().getBinaryName().equals(e.resolveTypeBinding().getBinaryName())
                            && var.getKey().equals(e.toString())) {
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

}
