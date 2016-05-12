package polyu_af.process;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jdt.core.dom.*;
import polyu_af.models.AccessVars4Line;
import polyu_af.models.MyExpression;
import polyu_af.utils.AstUtils;

import java.util.*;

/**
 * Created by liushanchen on 16/4/1.
 * <p>
 * 1.	借助stack结构记录当前访问到的accessible 变量；两个stack，一个记录currentField variables； 另一个记录currentAccessible variables 既local variables。
 * a)	currentField stack：
 * i.	push – visit TypeDeclaration
 * ii.	peek.add – visit FieldDeclaration
 * iii.	Pop – endVisit TypeDeclaration
 * b)	currentAcessible stack:
 * i.	push – visit MethodDeclaration\for\block\switchStatement
 * ii.	peek.add – parameters, variable statement
 * iii.	pop – endvisit MethodDeclaration \for\block\ switchStatement
 * 1.	block(if, while, try ):
 * a)	visitBlock – currentAccessible.push(currentAccessible.peek().copy)
 * b)	endVisitBlock – currentAccessible.pop()
 * 2.	for:
 * a)	visitBlock – currentAccessible.push(currentAccessible.peek().copy+initial)
 * b)	endVisitBlock – currentAccessible.pop()
 * 3.	switch case:
 * a)	visit SwitchStatement – push(copy)
 * b)	endvisit switchStatement – pop
 * c)	visit break – pop & push(copy)
 * c)	result Map:
 * i.	put – currentField.peek() & currentAccessible.peek().
 * </p>
 */

/**
 * use getAccessibleVariables to get the access variable for every line of the class
 * one line's accessible variables is equivalent with the biggest line smaller than it.
 */
public class AccessVarVisitor extends ASTVisitor {

    private static Logger logger = LogManager.getLogger();

    protected CompilationUnit root = null;

    public AccessVarVisitor(CompilationUnit root) {
        this.root = root;
    }

    protected List<AccessVars4Line> accessVars4LineList = new ArrayList<AccessVars4Line>();
    /**
     * each stack element stores the accessible fields in the current scope.
     */
    private Stack<List<MyExpression>> currentField = new Stack<List<MyExpression>>();
    private Stack<List<MyExpression>> currentStaticField = new Stack<List<MyExpression>>();
    private boolean isStaticBlock = false;
    /**
     * each stack element stores the accessible formal parameters and local variables
     * in the current scope.
     */
    private Stack<List<MyExpression>> currentAccessible = new Stack<List<MyExpression>>();

    private Stack<TypeDeclaration> typeDecl = new Stack<TypeDeclaration>();


    public List<AccessVars4Line> getAccessibleVars() {
        return accessVars4LineList;
    }


    @Override
    public void endVisit(ExpressionStatement node) {
        super.endVisit(node);
        outPutAccessibleVars(node.getStartPosition() + node.getLength());
    }

    @Override
    public void endVisit(ReturnStatement node) {
        super.endVisit(node);
        outPutAccessibleVars(node.getStartPosition() + node.getLength());
    }

    /**
     * 进入一个 类 或者 内部类
     *
     * @param node
     * @return
     */
    @Override
    public final boolean visit(final TypeDeclaration node) {
        typeDecl.push(node);
        if (Modifier.isStatic(node.getModifiers())) {
            currentField.push(new ArrayList<MyExpression>());
        } else {
            if (currentField.isEmpty()) {
                currentField.push(new ArrayList<MyExpression>());
            } else {
                currentField.push(new ArrayList<MyExpression>(currentField.peek()));
            }
        }
        if (currentStaticField.isEmpty()) {
            currentStaticField.push(new ArrayList<MyExpression>());
        } else {
            currentStaticField.push(new ArrayList<MyExpression>(currentStaticField.peek()));
        }
        return super.visit(node);
    }

    @Override
    public final void endVisit(final TypeDeclaration node) {
        typeDecl.pop();
        currentStaticField.pop();
        currentField.pop();

        super.endVisit(node);
    }

    /**
     * 进入一个 field 变量
     *
     * @param node
     * @return
     */
    @Override
    public final boolean visit(final FieldDeclaration node) {
        String type = AstUtils.getExpType(node.getType());
        for (Object o : node.fragments()) {
            VariableDeclarationFragment vdf = (VariableDeclarationFragment) o;
            MyExpression myExpression = new MyExpression(vdf, vdf.getName().getIdentifier(), type);

            if (Modifier.isStatic(node.getModifiers())) {
                currentStaticField.peek().add(myExpression);
            } else {
                currentField.peek().add(myExpression);
            }
        }
        return super.visit(node);
    }

    @Override
    public boolean visit(final MethodDeclaration node) {
        if (Modifier.isStatic(node.getModifiers())) {
            isStaticBlock = true;
        }
        List<MyExpression> formalParameters = new ArrayList<MyExpression>();
        for (Object o : node.parameters()) {
            SingleVariableDeclaration svd = (SingleVariableDeclaration) o;
            String type=AstUtils.getExpType(svd.getType());
            MyExpression myExpression = new MyExpression(svd, svd.getName().toString(), type);
            formalParameters.add(myExpression);
        }
        currentAccessible.push(formalParameters);
        return super.visit(node);
    }

    @Override
    public void endVisit(final MethodDeclaration node) {
        isStaticBlock = false;
        removeLayer();
        super.endVisit(node);
    }

    @Override
    public boolean visit(ForStatement node) {
        List<MyExpression> formalParameters = new ArrayList<MyExpression>();
        formalParameters.addAll(currentAccessible.peek());
        for (Object o : node.initializers()) {
            VariableDeclarationExpression svd = (VariableDeclarationExpression) o;
            String type = AstUtils.getExpType(svd.getType());
            for (Object o2 : svd.fragments()) {
                VariableDeclarationFragment f = (VariableDeclarationFragment) o2;
                MyExpression myExpression = new MyExpression(svd, f.getName().toString(), type);
                formalParameters.add(myExpression);
            }
        }
        currentAccessible.push(formalParameters);
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
    public boolean visit(final VariableDeclarationStatement node) {
        String type = AstUtils.getExpType(node.getType());
        for (Object o : node.fragments()) {
            VariableDeclarationFragment vdf = (VariableDeclarationFragment) o;
            MyExpression myExpression = new MyExpression(vdf, vdf.getName().toString(), type);
            currentAccessible.peek().add(myExpression);
        }
        return super.visit(node);
    }


    protected void outPutAccessibleVars(int position) {
        AccessVars4Line vars = new AccessVars4Line(root.getLineNumber(position));

        if (!currentAccessible.isEmpty()) {
            vars.addVar(currentAccessible.peek());
        }
        if (!isStaticBlock) {
            if (!currentField.isEmpty()) {
                vars.addVar(currentField.peek());
            }
        }
        if (!currentStaticField.isEmpty()) {
            vars.addVar(currentStaticField.peek());
        }
        if (!accessVars4LineList.contains(vars)) {
            accessVars4LineList.add(vars);
        }
    }
    private void newLayerInMethod(){
        List<MyExpression> formalParameters = new ArrayList<MyExpression>();
        formalParameters.addAll(currentAccessible.peek());
        currentAccessible.push(formalParameters);
    }
    private void removeLayer(){
        currentAccessible.pop();
    }


}
