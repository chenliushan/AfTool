package polyu_af.process;

import org.eclipse.jdt.core.dom.*;
import polyu_af.models.MyExpression;
import polyu_af.utils.AstUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * Created by liushanchen on 16/5/12.
 */
public class AccessExpVisitor extends AccessVarVisitor{
    private Stack<List<MyExpression>> currentAccessExp = new Stack<List<MyExpression>>();


    public AccessExpVisitor(CompilationUnit root) {
        super(root);
    }



    @Override
    public boolean visit(ExpressionStatement node) {
        Expression exp=node.getExpression();
        addExpIntoStack(exp);

        return super.visit(node);
    }

    @Override
    public boolean visit(IfStatement node) {
        Expression exp=node.getExpression();
        addExpIntoStack(exp);
        return super.visit(node);
    }



    @Override
    public boolean visit( MethodDeclaration node) {
        newLayerOutsideMethod();
        return super.visit(node);
    }

    @Override
    public void endVisit( MethodDeclaration node) {
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
    public boolean visit(SwitchStatement node) {
        newLayerInMethod();
        Expression exp=node.getExpression();
        addExpIntoStack(exp);
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

    private void addExpIntoStack( Expression exp){
        MyExpression myExpression = new MyExpression(exp, exp.toString(), AstUtils.getExpType(exp.resolveTypeBinding()));
        currentAccessExp.peek().add(myExpression);
    }
    private void newLayerOutsideMethod(){
        List<MyExpression> formalParameters = new ArrayList<MyExpression>();
        currentAccessExp.push(formalParameters);
    }

    private void newLayerInMethod(){
        List<MyExpression> formalParameters = new ArrayList<MyExpression>();
        formalParameters.addAll(currentAccessExp.peek());
        currentAccessExp.push(formalParameters);
    }
    private void removeLayer(){
        currentAccessExp.pop();
    }

    protected void outPutAccessibleVars(int position) {
        super.outPutAccessibleVars(position);
        accessVars4LineList.get(accessVars4LineList.size() - 1).addExp(currentAccessExp.peek());
    }


}
