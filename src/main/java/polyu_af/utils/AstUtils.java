package polyu_af.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.*;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.text.edits.TextEdit;
import polyu_af.models.FaultUnit;

import java.util.Map;

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
                logger.info("binding.getName: " + binding.getName());
                logger.info("binding.getKey: " + binding.getKey());
                logger.info("binding.getKind: " + binding.getKind());
            }
            ITypeBinding typeBinding = node.resolveTypeBinding();
            if (typeBinding != null) {
                logger.info("typeBinding.getQualifiedName: " + typeBinding.getQualifiedName());
//                logger.info("typeBinding.getTypeDeclaration: " + typeBinding.getTypeDeclaration());
//                logger.info("typeBinding.getDeclaringClass: " + typeBinding.getDeclaringClass());
//                logger.info("typeBinding.getDeclaringMethod: " + typeBinding.getDeclaringMethod());
//                logger.info("typeBinding.getComponentType: " + typeBinding.getComponentType());
//                logger.info("typeBinding.isGenericType: " + typeBinding.isGenericType());
            }
            logger.info("==========================");
            return super.visit(node);
        }

        @Override
        public boolean visit(TypeDeclaration node) {
            super.endVisit(node);
            IBinding binding = node.resolveBinding();
            if (binding != null) {
                logger.info("binding.getName: " + binding.getName());
                logger.info("binding.getKey: " + binding.getKey());
                logger.info("binding.getKind: " + binding.getKind());
                logger.info("binding.getJavaElement: " + binding.getJavaElement());
//                logger.info("binding.toString: " + binding.toString());
            }
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
    public static ASTVisitor astTypeVisitor = new ASTVisitor() {
        @Override
        public void postVisit(ASTNode node) {
            logger.info("node: " + node.toString());
            logger.info("getNodeType: " + ASTNode.nodeClassForType(node.getNodeType()));
            logger.info("============== ");
            super.postVisit(node);
        }
    };


    /**
     * @param source
     * @param classpathEntries
     * @param sourcepath       can only have one path
     * @param encodings
     * @param UnitName
     * @return
     */
    public static CompilationUnit createResolvedAST(String source, String[] classpathEntries, String[] sourcepath, String[] encodings, String UnitName) {
//        Map options = JavaCore.getOptions();

        ASTParser parser = ASTParser.newParser(AST.JLS8);
        parser.setKind(ASTParser.K_COMPILATION_UNIT);
        parser.setResolveBindings(true);
//        parser.setCompilerOptions(options);
        parser.setEnvironment(classpathEntries, sourcepath, encodings, true);
        parser.setUnitName(UnitName);
        parser.setSource(source.toCharArray());
        CompilationUnit node = (CompilationUnit) parser.createAST(null);
        return node;
    }

    public static Expression createResolvedExpAST(String source, String[] classpathEntries, String[] sourcepath, String[] encodings, String UnitName) {
        ASTParser parser = ASTParser.newParser(AST.JLS8);
        parser.setKind(ASTParser.K_EXPRESSION);
        parser.setResolveBindings(true);

        parser.setEnvironment(classpathEntries, sourcepath, encodings, true);
        parser.setUnitName(UnitName);
        parser.setSource(source.toCharArray());
        Expression node = (Expression) parser.createAST(null);
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

    /**
     * Find the target node in the root that the FaultUnit Point to.
     * The root and the faultUnit should comes from the same TargetClass.(not checked yet.)
     *
     * @param root
     * @param fu
     * @return
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
     * Modify the ast and apply the changes to the document
     *
     * @param root
     * @param exp
     * @param source the root and the source should come from the same .java file
     */
    public static String parseExpRecordModifications(final CompilationUnit root, final String exp, String source) {
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
//        root.accept(resolveTypeVisitor);
        return document.get();
    }

}


