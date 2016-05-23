package polyu_af.process;

import org.eclipse.jdt.core.dom.*;
import polyu_af.GlobalProcess;
import polyu_af.exception.NotAcceptExpNodeTypeException;
import polyu_af.models.LineAccessVars;
import polyu_af.models.MyExp;
import polyu_af.utils.AstUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * Created by liushanchen on 16/5/12.
 */
public class LAccessExpVisitor extends LAccessVarVisitor {
    private Stack<List<MyExp>> currentAccessExp = new Stack<List<MyExp>>();
    private List<MyExp> allExp = new ArrayList<MyExp>();


    public LAccessExpVisitor(CompilationUnit root) {
        super(root);
    }


    @Override
    public boolean visit(InfixExpression node) {
        addExpIntoStack(node);
        return super.visit(node);
    }

//    @Override
//    public void endVisit(Assignment node) {
//        addExpIntoStack(node);
//        super.endVisit(node);
//    }

    @Override
    public void endVisit(MethodInvocation node) {
        addExpIntoStack(node);
        super.endVisit(node);
    }

    @Override
    public boolean visit(IfStatement node) {
        newLayerInMethod();
        return super.visit(node);
    }


    @Override
    public boolean visit(MethodDeclaration node) {
        newLayerOutsideMethod();

        return super.visit(node);
    }

    @Override
    public boolean visit(SingleMemberAnnotation node) {
//        String smaValue=node.getValue().resolveConstantExpressionValue().toString();
//        Expression expAst=AstUtils.createExpAST(smaValue);
//        addExpIntoStack(expAst);
        addAnnExpIntoStack(node);
        return super.visit(node);
    }

    @Override
    public void endVisit(MethodDeclaration node) {
        removeLayer();
        super.endVisit(node);
    }

    @Override
    public boolean visit(ForStatement node) {
        newLayerInMethod();
        return super.visit(node);
    }

    @Override
    public void endVisit(ForStatement node) {
        removeLayer();
        super.endVisit(node);
    }

    @Override
    public boolean visit(Block node) {
        newLayerInMethod();
        return super.visit(node);
    }

    @Override
    public void endVisit(Block node) {
        removeLayer();
        super.endVisit(node);
    }

    @Override
    public boolean visit(WhileStatement node) {
        newLayerInMethod();
        return super.visit(node);
    }

    @Override
    public boolean visit(SwitchStatement node) {
        newLayerInMethod();
        return super.visit(node);
    }

    @Override
    public void endVisit(SwitchStatement node) {
        removeLayer();
        super.endVisit(node);
    }


    @Override
    public boolean visit(BreakStatement node) {
        removeLayer();
        newLayerInMethod();
        return super.visit(node);
    }

    @Override
    public void endVisit(TypeDeclaration node) {
        super.endVisit(node);
        addExpInLineAccessVars();
    }

    private void addExpInLineAccessVars() {
        for(LineAccessVars lav:accessVars4LineList){
            lav.addExpInLine(allExp);
        }
    }

    private void addExpIntoStack(Expression exp) {
        try {
            MyExp myExpression = new MyExp(exp);
            allExp.add(myExpression);
            currentAccessExp.peek().add(myExpression);
        } catch (NotAcceptExpNodeTypeException e) {
            e.printStackTrace();
        }
    }

    private void addAnnExpIntoStack(SingleMemberAnnotation ann) {
        try {
            MyExp myExpression = new MyExp(ann);
            allExp.add(myExpression);
            currentAccessExp.peek().add(myExpression);
        } catch (NotAcceptExpNodeTypeException e) {
            e.printStackTrace();
        }
    }

    private void newLayerOutsideMethod() {
        List<MyExp> formalParameters = new ArrayList<MyExp>();
        currentAccessExp.push(formalParameters);
    }

    private void newLayerInMethod() {
        List<MyExp> formalParameters = new ArrayList<MyExp>();
        formalParameters.addAll(currentAccessExp.peek());
        currentAccessExp.push(formalParameters);
    }

    private void removeLayer() {
        currentAccessExp.pop();
    }

//    protected void outPutAccessibleVars(int position) {
//        super.outPutAccessibleVars(position);
//        accessVars4LineList.get(accessVars4LineList.size() - 1).addExp(currentAccessExp.peek());
//    }


}