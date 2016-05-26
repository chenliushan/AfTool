package polyu_af.visitors;

import org.eclipse.jdt.core.dom.*;
import polyu_af.exception.NotAcceptExpNodeTypeException;
import polyu_af.models.LineAccessVars;
import polyu_af.models.MyExpAst;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * Created by liushanchen on 16/5/12.
 */
public class LAccessExpVisitor extends LAccessVarVisitor {
    private Stack<List<MyExpAst>> currentAccessExp = new Stack<List<MyExpAst>>();
    private List<MyExpAst> allExp = new ArrayList<MyExpAst>();


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
            MyExpAst myExpressionAst = new MyExpAst(exp);
            allExp.add(myExpressionAst);
            currentAccessExp.peek().add(myExpressionAst);
        } catch (NotAcceptExpNodeTypeException e) {
            e.printStackTrace();
        }
    }

    private void addAnnExpIntoStack(SingleMemberAnnotation ann) {
        try {
            MyExpAst myExpressionAst = new MyExpAst(ann);
            allExp.add(myExpressionAst);
            currentAccessExp.peek().add(myExpressionAst);
        } catch (NotAcceptExpNodeTypeException e) {
            e.printStackTrace();
        }
    }

    private void newLayerOutsideMethod() {
        List<MyExpAst> formalParameters = new ArrayList<MyExpAst>();
        currentAccessExp.push(formalParameters);
    }

    private void newLayerInMethod() {
        List<MyExpAst> formalParameters = new ArrayList<MyExpAst>();
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
