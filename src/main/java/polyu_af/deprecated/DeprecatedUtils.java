package polyu_af.deprecated;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.*;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.jdt.core.dom.rewrite.ITrackedNodePosition;
import org.eclipse.jdt.core.dom.rewrite.ListRewrite;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.text.edits.TextEdit;
import polyu_af.utils.AstUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by liushanchen on 16/4/12.
 */
public class DeprecatedUtils {
    private static Logger logger = LogManager.getLogger(AstUtils.class.getName());

    public static Map<String, ASTNode> astForm;
    /**
     * findScope of all declarations inside one ASTnode
     */
    public static ASTVisitor findDeclaration = new ASTVisitor() {

        @Override
        public boolean visit(VariableDeclarationFragment node) {
            VariableScope scope = findScope((CompilationUnit) node.getRoot(), node);
            String variableType = node.getName().resolveTypeBinding().getName();
            logger.info("VariableDeclarationFragment: " + variableType + " " + node.getName() + ":::" + scope.toString());
            storeAst(variableType + " " + node.getName() + ":::" + scope.toString(), node);
            return super.visit(node);
        }

        /**
         * get variable declaration in the method parameters
         * @param node
         * @return
         */
        @Override
        public boolean visit(SingleVariableDeclaration node) {
            VariableScope scope = findScope((CompilationUnit) node.getRoot(), node);
            logger.info("SingleVariableDeclaration: " + node.toString() + ":::" + scope.toString());
            storeAst(node.toString() + ":::" + scope.toString(), node);

            return super.visit(node);
        }
    };


    /**
     * 确定变量的scope
     * 1.	找到变量node直接属于的block/type/method node
     * 2.	变量node的起始位置到block的结束位置为作用域（不完善，在内部类中不能使用费静态的field 变量。）
     *
     * @param root
     * @param node
     * @return
     */
    private static VariableScope findScope(CompilationUnit root, ASTNode node) {
        int start = -1, end = -1;
        ASTNode parent = node.getParent();
        VariableScope scope = null;
        if (parent instanceof TypeDeclaration) {
            scope = new VariableScope(root.getLineNumber(node.getStartPosition()), root.getLineNumber(parent.getStartPosition() + parent.getLength()));
        } else if (parent instanceof Block) {
            scope = new VariableScope(root.getLineNumber(node.getStartPosition()), root.getLineNumber(parent.getStartPosition() + parent.getLength()));
        } else {
            return findScope(root, parent);
        }
        return scope;
    }


    private static void storeAst(String key, ASTNode node) {
        if (astForm == null) {
            astForm = new HashMap<String, ASTNode>();
        } else {
            astForm.put(key, node);
        }
    }



    /**
     * parse Expressions ListRewrite
     * fail when do "rewriter.rewriteAST(); "
     *
     * @param source
     * @param exp
     * @param root
     */
    public static void parseExpressionsListRewrite(String source, String exp, CompilationUnit root) {
        AST rootAst = root.getAST();
        root.recordModifications();
        ASTRewrite rewriter = ASTRewrite.create(rootAst);
        TypeDeclaration td = (TypeDeclaration) root.types().get(0);
        ITrackedNodePosition tdLocation = rewriter.track(td);
        ListRewrite listRewrite = rewriter.getListRewrite(root, CompilationUnit.TYPES_PROPERTY);

        ASTParser parser = ASTParser.newParser(AST.JLS8);
        parser.setKind(ASTParser.K_EXPRESSION);
        parser.setResolveBindings(true);
        parser.setSource(exp.toCharArray());
        ASTNode expAst = parser.createAST(null);
        InfixExpression expExp = (InfixExpression) ASTNode.copySubtree(rootAst, expAst);
        InfixExpression rootExp = rootAst.newInfixExpression();
//        logger.info("l: "+expExp.getLeftOperand());
        logger.info("getRoot: " + rootExp.getRoot());
        rootExp.setLeftOperand((Expression) ASTNode.copySubtree(rootExp.getAST(), expExp.getLeftOperand()));
        rootExp.setOperator(expExp.getOperator());
        rootExp.setRightOperand((Expression) ASTNode.copySubtree(rootExp.getAST(), expExp.getRightOperand()));


        listRewrite.insertFirst(rootExp, null);
        logger.info("getRoot: " + rootExp.getRoot());
        try {
            TextEdit edits = rewriter.rewriteAST();
            /**
             * IllegalArgumentException
             * if the node is null, or if the node is not part of this rewriter's AST,
             * or if the inserted node is not a new node (or placeholder),
             * or if the described modification is otherwise invalid (not a member of this node's original list)
             */
            Document document = new Document(source);
            edits.apply(document);

//      unit.getBuffer().setContents(document.get());
        } catch (JavaModelException e) {
            e.printStackTrace();
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

}
