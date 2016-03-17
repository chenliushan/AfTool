package polyu_af;

import com.google.gson.Gson;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.dom.*;
import polyu_af.domain.FaultUnit;
import polyu_af.utils.MyUtils;

/**
 * Created by liushanchen on 16/2/24.
 */
public class AstBasic {
    private static Logger logger = LogManager.getLogger(AstBasic.class.getName());
    public static void main(String arg[]) {
//        String source = readFile(System.getProperty("user.dir") + "/src/main/java/polyu_af/AstBasic.java");
//        String[] classpathEntries=new String[]{"build/classes/main",
//                "lib/org.eclipse.core.contenttype_3.4.200.v20140207-1251.jar",
//                "lib/org.eclipse.core.jobs_3.6.0.v20140424-0053.jar",
//                "lib/org.eclipse.core.resources_3.9.1.v20140825-1431.jar",
//                "lib/org.eclipse.core.runtime_3.10.0.v20140318-2214.jar",
//                "lib/org.eclipse.equinox.common_3.6.200.v20130402-1505.jar",
//                "lib/org.eclipse.equinox.preferences_3.5.200.v20140224-1527.jar",
//                "lib/org.eclipse.jdt_3.10.1.v20150204-1700.jar",
//                "lib/org.eclipse.jdt.core_3.10.2.v20150120-1634.jar",
//                "lib/org.eclipse.osgi_3.10.1.v20140909-1633.jar",
//                "lib/org.eclipse.text_3.5.300.v20130515-1451.jar"
//        };
//        String[] sourcepathEntries=new String[]{"src/main/java"};
//        String[] encodings=new String[]{"UTF-8"};g

        String source = MyUtils.readFile("/Users/liushanchen/IdeaProjects/AfTest" + "/src/main/java/polyu_af/MyList1.java");
        String[] classpathEntries = new String[]{"/Users/liushanchen/IdeaProjects/AfTest/build/classes/main",
                "/Users/liushanchen/IdeaProjects/AfTest/lib/cofoja.asm-1.2-20140817.jar"};
        String[] sourcepathEntries = new String[]{"/Users/liushanchen/IdeaProjects/AfTest/src/main/java"};
        String[] encodings = new String[]{"UTF-8"};

        CompilationUnit root = createNode(source.toCharArray(), classpathEntries, sourcepathEntries, encodings);

        /*
        Read the inputs and find the node
        and use visitor to go through the node's AST
         */
//        root.accept(visitorTest());

//        ASTNode expNode = findTheNodePL(root);
//        if (expNode != null) {
//            expNode.accept(visitorTest());
//        } else {
//            logger.warn("The expression node is NULL!");
//        }

    }


    private static ASTNode findTheNodePL(ASTNode root) {
        /*
        Find the node acrroding to its startPosition and length
        */
        String source = MyUtils.readFile(System.getProperty("user.dir") + "/input/AfToolInput");
        if (source.trim() != "" && source != null) {
            logger.info("source:" + source);
            Gson gson = new Gson();
            FaultUnit input = gson.fromJson(source, FaultUnit.class);
            logger.info("FaultUnit:" + input.toString());
            /*
            The NodeFinder class can be used to find a specific node inside a tree
            For a given selection range, finds the covered node and the covering node.
            */
            if (input != null && (input.getStartPosition() > -1 || input.getLine() > -1)) {
                NodeFinder nodeFinder = null;
                if (input.getStartPosition() == -1) {
                    /*
                    int	getPosition(int line, int column)
                    Given a line number and column number, returns the corresponding position in the original source string.
                    */
                    CompilationUnit cuRoot = (CompilationUnit) root;
                    int position = cuRoot.getPosition(input.getLine(), input.getColumn());
                    logger.info("position:  " + position);
                    nodeFinder = new NodeFinder(root, position, input.getLength());
                } else {
                    nodeFinder = new NodeFinder(root, input.getStartPosition(), input.getLength());
                }
                /*
                Returns the innermost node that fully contains the selection.
                A node also contains the zero-length selection on either end.
                If more than one node covers the selection,
                the returned node is the last covering node found in a preorder traversal of the AST.
                This implies that for a zero-length selection between two adjacent sibling nodes, the node on the right is returned.
                 */
//                ASTNode nodeScope = nodeFinder.getCoveringNode();
                /*
                If the AST contains nodes whose range is equal to the selection,
                returns the innermost of those nodes. Otherwise, returns the first node in a preorder traversal of the AST,
                where the complete node range is covered by the selection.
                */
                ASTNode targetNode = nodeFinder.getCoveredNode();
//                logger.info("nodeFinder.getCoveringNode(): " + nodeScope);
                logger.info("nodeFinder.getCoveredNode(): " + targetNode);
                return targetNode;
            }
        }
        return null;
    }



    private static CompilationUnit createNode(char[] source, String[] classpathEntries, String[] sourcepathEntries, String[] encodings) {
        ASTParser parser = ASTParser.newParser(AST.JLS8);
        parser.setKind(ASTParser.K_COMPILATION_UNIT);
        parser.setResolveBindings(true);
        parser.setEnvironment(classpathEntries, sourcepathEntries, encodings, true);
        parser.setUnitName("AfTool");
        parser.setSource(source);
        CompilationUnit node = (CompilationUnit) parser.createAST(null);
        return node;

    }

    private static CompilationUnit createNode(ICompilationUnit unit) {
        ASTParser parser = ASTParser.newParser(AST.JLS8);
        parser.setKind(ASTParser.K_COMPILATION_UNIT);
        parser.setResolveBindings(true);
        parser.setSource(unit);
        CompilationUnit node = (CompilationUnit) parser.createAST(null);
        return node;

    }

    private static ASTVisitor visitorTest() {

         /*
         Do the specific process when visit some nodes that are specific type
         by overriding the visit() function with specific node arg.
         */

        ASTVisitor visitor = new ASTVisitor() {
            /*
            when the visitor visit a node, the calling order is:
            preVisit2()
            preVisit()
            visit()
            endVisit()
            postVisit()
            */


            /*
            The same as the preVisit method, but
            Returns:true if visit(node) should be called, and false otherwise.
            */
            @Override
            public boolean preVisit2(ASTNode node) {

                return super.preVisit2(node);
            }

            /*
            Visits the given node to perform some arbitrary operation.
            This method is invoked prior to the appropriate type-specific visit method.
            The default implementation of this method does nothing. Subclasses may reimplement this method as needed.
             */
            @Override
            public void preVisit(ASTNode node) {

                super.preVisit(node);
            }

            /*
            Visits the given node to perform some arbitrary operation.
            This method is invoked after the appropriate type-specific endVisit method.
            The default implementation of this method does nothing. Subclasses may reimplement this method as needed.
             */
            @Override
            public void postVisit(ASTNode node) {
               /*
                node.getNodeType()
                Returns an integer value identifying the type of this concrete AST node.
                The values are small positive integers, suitable for use in switch statements.

                ASTNode.nodeClassForType()
                Returns the node class for the corresponding node type.
                 */
//                logger.info("node: " + node.toString());
//                logger.info("getNodeType: " + ASTNode.nodeClassForType(node.getNodeType()));
//                logger.info("============== ");


                super.postVisit(node);
            }

            @Override
            public boolean visit(SimpleName node) {
                logger.info("getIdentifier: " + node.getIdentifier());
                IBinding binding = node.resolveBinding();
                if (binding != null) {
//                    logger.info("binding: " + binding);
                    logger.info("getName: " + binding.getName());
                    logger.info("getKey: " + binding.getKey());
                    logger.info("getKind: " + binding.getKind());
//                    if (binding.getKind() == IBinding.TYPE) {
//                        logger.info("TYPE: "+binding);
//                    }
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
                    logger.info("annotationBinding: "+annotationBinding);
                    logger.info("getAllMemberValuePairs: ");
                    IMemberValuePairBinding[] allValue=annotationBinding.getAllMemberValuePairs();
                    for (IMemberValuePairBinding item:allValue){
                        Object[] os= (Object []) item.getValue();//get the content of [Ljava.lang.Object;@29d89d5d
                        for(Object o:os){
                            logger.info("item: "+item.getName()+"; value: "+o);
                        }
                    }
                    logger.info("++++++++++++++++++++++++++");
                }
                return super.visit(node);
            }
        };
        return visitor;
    }


}
