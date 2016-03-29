package polyu_af.utils;

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
import polyu_af.models.FaultUnit;

/**
 * Created by liushanchen on 16/3/17.
 */
public class AstUtils {
    private static Logger logger = LogManager.getLogger(AstUtils.class.getName());
    /**
     * see the resolve declaration type of the simpleName node
     */
    public static ASTVisitor resolveTypeVisitor = new ASTVisitor() {
        @Override
        public boolean visit(SimpleName node) {
            logger.info("getIdentifier: " + node.getIdentifier());
            logger.info("getNodeType: " + ASTNode.nodeClassForType(node.getNodeType()));
            IBinding binding = node.resolveBinding();
            if (binding != null) {
                logger.info("getName: " + binding.getName());
                logger.info("getKey: " + binding.getKey());
                logger.info("getKind: " + binding.getKind());
            }
            if (node.resolveTypeBinding() != null) {
                logger.info("getQualifiedName: " + node.resolveTypeBinding().getQualifiedName());
            }
            logger.info("==========================");
            return super.visit(node);
        }

        @Override
        public boolean visit(SingleMemberAnnotation node) {
            IAnnotationBinding annotationBinding = node.resolveAnnotationBinding();
            if (annotationBinding != null) {
                logger.info("annotationBinding: " + annotationBinding);
                logger.info("getAllMemberValuePairs: ");
                IMemberValuePairBinding[] allValue = annotationBinding.getAllMemberValuePairs();
                for (IMemberValuePairBinding item : allValue) {
                    Object[] os = (Object[]) item.getValue();
                    for (Object o : os) {
                        logger.info("item: " + item.getName() + "; value: " + o);
                    }
                }
                logger.info("++++++++++++++++++++++++++");
            }
            return super.visit(node);
        }
    };
    /**
     * print the node's ASTtype
     */
    public static ASTVisitor seeTypeVisitor = new ASTVisitor() {
        @Override
        public void postVisit(ASTNode node) {
            logger.info("node: " + node.toString());
            logger.info("getNodeType: " + ASTNode.nodeClassForType(node.getNodeType()));
            logger.info("============== ");
            super.postVisit(node);
        }
    };



    public static CompilationUnit createResolvedAST(String source, String[] classpathEntries, String[] sourcepathEntries, String[] encodings, String UnitName) {
        ASTParser parser = ASTParser.newParser(AST.JLS8);
        parser.setKind(ASTParser.K_COMPILATION_UNIT);
        parser.setResolveBindings(true);
        /*
        the sourcepathEntries can only have one path
         */
        parser.setEnvironment(classpathEntries, sourcepathEntries, encodings, true);
        parser.setUnitName(UnitName);
        parser.setSource(source.toCharArray());
        CompilationUnit node = (CompilationUnit) parser.createAST(null);
        return node;
    }


    public static Expression createExpAST(String exp) {
        ASTParser parser = ASTParser.newParser(AST.JLS8);
        parser.setKind(ASTParser.K_EXPRESSION);
        parser.setResolveBindings(true);
        parser.setSource(exp.toCharArray());
        Expression expAst = (Expression) parser.createAST(null);
        return expAst;
    }


    /*
    Find the target node in the root that the FaultUnit Point to.
    The root and the faultUnit should comes from the same FaultFile.(not checked yet.)
     */
    public static ASTNode findNodeInRoot(ASTNode root, FaultUnit fu) {
        if (root != null && fu != null && (fu.getStartPosition() > -1 || fu.getLine() > -1)) {
            NodeFinder nodeFinder = null;
            if (fu.getStartPosition() == -1) {
                CompilationUnit cuRoot = (CompilationUnit) root;
                int position = cuRoot.getPosition(fu.getLine(), fu.getColumn());
                nodeFinder = new NodeFinder(root, position, fu.getLength());
            } else {
                nodeFinder = new NodeFinder(root, fu.getStartPosition(), fu.getLength());
            }
            return nodeFinder.getCoveredNode();
        }
        return null;
    }

    /**
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

    /**
     * Modify the ast and apply the changes to the document
     * @param root
     * @param exp
     * @param source
     * the root and the source should come from the same .java file
     */
    public static void parseExpRecordModifications(final CompilationUnit root, final String exp, String source) {
        ASTVisitor modify = new ASTVisitor() {
            @Override
            public boolean visit(ExpressionStatement node) {
                Expression expAst = createExpAST(exp);
                Expression cp = (Expression) ASTNode.copySubtree(root.getAST(), expAst);
                node.setExpression(cp);
                return super.visit(node);
            }
        };

        root.recordModifications();
        root.accept(modify);
        Document document = new Document(source);
        logger.info("document: " + document.get());
        TextEdit edits = root.rewrite(document, null);
        try {
            edits.apply(document);
            logger.info("document: " + document.get());
            logger.info("root: " + root);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
        root.accept(resolveTypeVisitor);

    }
}


