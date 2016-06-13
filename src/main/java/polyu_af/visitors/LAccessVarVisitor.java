package polyu_af.visitors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jdt.core.dom.*;
import polyu_af.exception.NotAcceptExpNodeTypeException;
import polyu_af.models.LineVars;
import polyu_af.models.MyExpAst;

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
public class LAccessVarVisitor extends ASTVisitor {

    private static Logger logger = LogManager.getLogger();

    protected CompilationUnit root = null;

    public LAccessVarVisitor(CompilationUnit root) {
        this.root = root;
    }

    protected List<LineVars> accessVars4LineList = new ArrayList<LineVars>();
    /**
     * each stack element stores the accessible fields in the current scope.
     */
    private Stack<List<MyExpAst>> currentField = new Stack<List<MyExpAst>>();
    private Stack<List<MyExpAst>> currentStaticField = new Stack<List<MyExpAst>>();
    private boolean isStaticBlock = false;
    /**
     * each stack element stores the accessible formal parameters and local variables
     * in the current scope.
     */
    private Stack<List<MyExpAst>> currentAccessible = new Stack<List<MyExpAst>>();
    private Stack<TypeDeclaration> typeDecl = new Stack<TypeDeclaration>();
    public List<LineVars> getAccessibleVars() {
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
            currentField.push(new ArrayList<MyExpAst>());
        } else {
            if (currentField.isEmpty()) {
                currentField.push(new ArrayList<MyExpAst>());
            } else {
                currentField.push(new ArrayList<MyExpAst>(currentField.peek()));
            }
        }
        if (currentStaticField.isEmpty()) {
            currentStaticField.push(new ArrayList<MyExpAst>());
        } else {
            currentStaticField.push(new ArrayList<MyExpAst>(currentStaticField.peek()));
        }
        return super.visit(node);
    }

    @Override
    public void endVisit(final TypeDeclaration node) {
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
        for (Object o : node.fragments()) {
            VariableDeclarationFragment vdf = (VariableDeclarationFragment) o;
            MyExpAst myExpressionAst = null;
            try {
                myExpressionAst = new MyExpAst(vdf);
                if (Modifier.isStatic(node.getModifiers())) {
                    currentStaticField.peek().add( myExpressionAst);
                } else {
                    currentField.peek().add( myExpressionAst);
                }
            } catch (NotAcceptExpNodeTypeException e) {
                e.printStackTrace();
            }

        }
        return super.visit(node);
    }

    @Override
    public boolean visit(final MethodDeclaration node) {
        if (Modifier.isStatic(node.getModifiers())) {
            isStaticBlock = true;
        }
        List<MyExpAst> formalParameters = new ArrayList<MyExpAst>();
        for (Object o : node.parameters()) {
            SingleVariableDeclaration svd = (SingleVariableDeclaration) o;
            MyExpAst myExpressionAst = null;
            try {
                myExpressionAst = new MyExpAst(svd);
                formalParameters.add( myExpressionAst);
            } catch (NotAcceptExpNodeTypeException e) {
                e.printStackTrace();
            }
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
        List<MyExpAst> formalParameters = new ArrayList<MyExpAst>();
        formalParameters.addAll(currentAccessible.peek());
        for (Object o : node.initializers()) {
            VariableDeclarationExpression svd = (VariableDeclarationExpression) o;
            for (Object o2 : svd.fragments()) {
                VariableDeclarationFragment f = (VariableDeclarationFragment) o2;
                MyExpAst myExpressionAst = null;
                try {
                    myExpressionAst = new MyExpAst(f);
                    formalParameters.add( myExpressionAst);
                } catch (NotAcceptExpNodeTypeException e) {
                    e.printStackTrace();
                }
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
        for (Object o : node.fragments()) {
            VariableDeclarationFragment vdf = (VariableDeclarationFragment) o;
            MyExpAst myExpressionAst = null;
            try {
                myExpressionAst = new MyExpAst(vdf);
                currentAccessible.peek().add( myExpressionAst);
            } catch (NotAcceptExpNodeTypeException e) {
                e.printStackTrace();
            }
        }
        return super.visit(node);
    }


    protected void outPutAccessibleVars(int position) {
        LineVars vars = new LineVars(root.getLineNumber(position));

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

    private void newLayerInMethod() {
        ArrayList<MyExpAst> formalParameters = new ArrayList<MyExpAst>();
        formalParameters.addAll(currentAccessible.peek());
        currentAccessible.push(formalParameters);
    }

    private void removeLayer() {
        currentAccessible.pop();
    }


}
