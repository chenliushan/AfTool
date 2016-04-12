package polyu_af.process;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jdt.core.dom.*;
import polyu_af.models.MyExpression;

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
 *
 */
public class AccessibleVariables extends ASTVisitor {

    private static Logger logger = LogManager.getLogger(AccessibleVariables.class.getName());

    private CompilationUnit root = null;

    public AccessibleVariables(CompilationUnit root) {
        this.root = root;
    }

    private Map<Integer, List<MyExpression>> accessibleVariables = new HashMap<Integer, List<MyExpression>>();
    /**
     * each stack element stores the accessible fields in the current scope.
     */
    private Stack<List<MyExpression>> currentField = new Stack<List<MyExpression>>();
    /**
     * each stack element stores the accessible formal parameters and local variables
     * in the current scope.
     */
    private Stack<List<MyExpression>> currentAccessible = new Stack<List<MyExpression>>();
    /**
     * map between each position and its corresponding actual parameter (or argument).
     * private Map<Integer, Expression> pos2arg = new HashMap<Integer, Expression>();
     * public final Map<Integer, Expression> getPos2ArgMap() {
     *return pos2arg;
     *}
     */
    /**
     * track the current enclosing type.
     * private Stack<List<MyExpression>> currentForA = new Stack<List<MyExpression>>();
     */

    /**
     * map between each position and its corresponding type declaration.
     * private Map<Integer, TypeDeclaration> pos2typeDecl = new HashMap<Integer, TypeDeclaration>();
     * public final Map<Integer, TypeDeclaration> getPos2TypeDecl() {
     * return pos2typeDecl;
     * }
     */

    private Stack<TypeDeclaration> typeDecl = new Stack<TypeDeclaration>();


    public final Map<Integer, List<MyExpression>> getAccessibleVariables() {
        return accessibleVariables;
    }


    @Override
    public final boolean visit(final TypeDeclaration node) {
        typeDecl.push(node);
        currentField.push(new ArrayList<MyExpression>());
        return super.visit(node);
    }

    @Override
    public final void endVisit(final TypeDeclaration node) {
        typeDecl.pop();
        currentField.pop();
        outPut(node.getStartPosition() + node.getLength());
        super.endVisit(node);
    }

    @Override
    public final boolean visit(final FieldDeclaration node) {
        for (Object o : node.fragments()) {
            VariableDeclarationFragment vdf = (VariableDeclarationFragment) o;
            MyExpression myExpression = new MyExpression(vdf, vdf.resolveBinding().getType().getName(), vdf.getName().getIdentifier());
            currentField.peek().add(myExpression);
        }
        return super.visit(node);
    }

    @Override
    public final boolean visit(final MethodDeclaration node) {
        List<MyExpression> formalParameters = new ArrayList<MyExpression>();
        for (Object o : node.parameters()) {
            SingleVariableDeclaration svd = (SingleVariableDeclaration) o;
            MyExpression myExpression = new MyExpression(svd, svd.getType().toString(), svd.getName().toString());
            formalParameters.add(myExpression);
        }
        currentAccessible.push(formalParameters);
        outPut(node.getStartPosition());
        return super.visit(node);
    }

    @Override
    public boolean visit(ForStatement node) {
        List<MyExpression> formalParameters = new ArrayList<MyExpression>();
        formalParameters.addAll(currentAccessible.peek());
        for (Object o : node.initializers()) {
            VariableDeclarationExpression svd = (VariableDeclarationExpression) o;
            for (Object o2 : svd.fragments()) {
                VariableDeclarationFragment f = (VariableDeclarationFragment) o2;
                MyExpression myExpression = new MyExpression(svd, f.resolveBinding().getType().getName().toString(), f.getName().toString());
                formalParameters.add(myExpression);
            }
        }
        currentAccessible.push(formalParameters);
        outPut(node.getStartPosition());
        return super.visit(node);
    }

    @Override
    public void endVisit(ForStatement node) {
        currentAccessible.pop();
        outPut(node.getStartPosition() + node.getLength());
        super.endVisit(node);
    }

    @Override
    public boolean visit(Block node) {
        List<MyExpression> formalParameters = new ArrayList<MyExpression>();
        formalParameters.addAll(currentAccessible.peek());
        currentAccessible.push(formalParameters);
        outPut(node.getStartPosition());
        return super.visit(node);
    }

    @Override
    public void endVisit(Block node) {
        currentAccessible.pop();
        outPut(node.getStartPosition() + node.getLength());
        super.endVisit(node);
    }


    @Override
    public boolean visit(SwitchStatement node) {
        List<MyExpression> formalParameters = new ArrayList<MyExpression>();
        formalParameters.addAll(currentAccessible.peek());
        currentAccessible.push(formalParameters);
        outPut(node.getStartPosition());
        return super.visit(node);
    }

    @Override
    public void endVisit(SwitchStatement node) {
        currentAccessible.pop();
        outPut(node.getStartPosition() + node.getLength());
        super.endVisit(node);
    }

    @Override
    public boolean visit(BreakStatement node) {
        currentAccessible.pop();
        List<MyExpression> formalParameters = new ArrayList<MyExpression>();
        formalParameters.addAll(currentAccessible.peek());
        currentAccessible.push(formalParameters);
        outPut(node.getStartPosition());
        return super.visit(node);
    }

    @Override
    public final void endVisit(final MethodDeclaration node) {
        currentAccessible.pop();
        outPut(node.getStartPosition() + node.getLength());
        super.endVisit(node);
    }

    @Override
    public final boolean visit(final Initializer node) {
        currentAccessible.push(new ArrayList<MyExpression>());
        outPut(node.getStartPosition());
        return super.visit(node);
    }

    @Override
    public final void endVisit(final Initializer node) {
        currentAccessible.pop();
        outPut(node.getStartPosition() + node.getLength());
        super.endVisit(node);
    }


    @Override
    public final boolean visit(final VariableDeclarationStatement node) {

        for (Object o : node.fragments()) {
            VariableDeclarationFragment vdf = (VariableDeclarationFragment) o;
            MyExpression myExpression = new MyExpression(vdf, vdf.resolveBinding().getType().getName(), vdf.getName().toString());
            currentAccessible.peek().add(myExpression);
            outPut(node.getStartPosition());
        }
        return super.visit(node);
    }

//
//    @Override
//    public final boolean visit(final MethodInvocation node) {
//
//        for (Object o : node.arguments()) {
//            int position = root.getLineNumber(((Expression) o).getStartPosition());
//            Expression argExpr = (Expression) o;
//            //pos2arg.put(position, argExpr);
//            pos2typeDecl.put(position, typeDecl.peek());
//            if (currentAccessible.isEmpty()) {
//                accessibleVariables.put(position, new ArrayList<MyExpression>());
//            } else {
//                accessibleVariables.put(position, new ArrayList<MyExpression>(currentAccessible.peek()));
//            }
//            if (!currentField.isEmpty()) {
//                accessibleVariables.get(position).addAll(currentField.peek());
//            }
//        }
//
//        return super.visit(node);
//    }

    private void outPut(int position) {
        /**
         * 因为key相同了所以会覆盖
         */
        position = root.getLineNumber(position);
//        if(accessibleVariables.containsKey(position)){
//            logger.info("key crash xxxxxxxxxxxxxxxxxx "+position);
//        }
//        pos2typeDecl.put(position, typeDecl.peek());
        if (currentAccessible.isEmpty()) {
            accessibleVariables.put(position, new ArrayList<MyExpression>());
        } else {
            accessibleVariables.put(position, new ArrayList<MyExpression>(currentAccessible.peek()));
        }
        if (!currentField.isEmpty()) {
            accessibleVariables.get(position).addAll(currentField.peek());
        }
    }
}
