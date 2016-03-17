package polyu_af.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jdt.core.dom.*;
import polyu_af.domain.FaultUnit;

/**
 * Created by liushanchen on 16/3/17.
 */
public class AstUtils {
    private static Logger logger = LogManager.getLogger(AstUtils.class.getName());
    public static ASTVisitor resolveType = new ASTVisitor() {
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


    public static CompilationUnit createResolvedAST(char[] source, String[] classpathEntries, String[] sourcepathEntries, String[] encodings) {
        ASTParser parser = ASTParser.newParser(AST.JLS8);
        parser.setKind(ASTParser.K_COMPILATION_UNIT);
        parser.setResolveBindings(true);
        parser.setEnvironment(classpathEntries, sourcepathEntries, encodings, true);
        parser.setUnitName("AfTool");
        parser.setSource(source);
        CompilationUnit node = (CompilationUnit) parser.createAST(null);
        return node;
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




}
