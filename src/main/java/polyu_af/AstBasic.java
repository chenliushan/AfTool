package polyu_af;

import com.google.gson.Gson;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jdt.core.*;
import org.eclipse.jdt.core.dom.*;
import polyu_af.domain.InputPL;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

/**
 * Created by liushanchen on 16/2/24.
 */
public class AstBasic {
    private static Logger logger = LogManager.getLogger(AstBasic.class.getName());


    public static void main(String arg[]) {

        String source = readFile(System.getProperty("user.dir") + "/src/main/java/polyu_af/AstBasic.java");
        CompilationUnit root = createNode(source.toCharArray());
        /*
        Read the inputs and find the node
        and use visitor to go through the node's AST
         */
//        root.accept(visitorTest());
//       for(Object item:root.types()){
//           logger.info("getName"+item.getClass().getName());
//       }
//       findTheNodePL(root).accept(visitorTest());

//        List<Object> types=root.types();
//        for(Object item:types){
//            logger.info("item: "+item.toString());
//        }
        ASTNode expNode=findTheNodePL(root);
        if(expNode!=null){
            expNode.accept(visitorTest());
        }else {
            logger.warn("The expression node is NULL!");
        }
    }



    private static ASTNode findTheNodePL(ASTNode root) {
        /*
        Find the node acrroding to its startPosition and length
        the input sample is:{“ startPosition”：“（int）”，“length”：“(int)”}
        or {"line":" 163", "column": "40","length": "24"}
        or {"line":" 47", "length": "100"}
        or {"line":" 47"}
        or {"startPosition":" 25"}
        the last two input will get the whole line
        */
        String source = readFile(System.getProperty("user.dir") + "/input/AfToolInput");
        if (source.trim() != "" && source != null) {
            logger.info("source:" + source);
            Gson gson = new Gson();
            InputPL input = gson.fromJson(source, InputPL.class);
            logger.info("InputPL:" + input.toString());
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

    private static String readFile(String fileName) {
        String fileContent = "";
        File file = new File(fileName);
        if (file.exists()) {
            if (file.isFile()) {
                try {
                    BufferedReader input = new BufferedReader(new FileReader(file));
                    StringBuffer buffer = new StringBuffer();
                    String text;
                    while ((text = input.readLine()) != null)
                        buffer.append(text + "\n");
                    fileContent = buffer.toString();
                } catch (IOException ioException) {
                    logger.error("readFile: " + ioException.getStackTrace().toString());
                }
            }
        }
        return fileContent;
    }

    private static CompilationUnit createNode(char[] source) {
        ASTParser parser = ASTParser.newParser(AST.JLS8);
        parser.setKind(ASTParser.K_COMPILATION_UNIT);
        parser.setSource(source);
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
                logger.info("node: " + node.toString());
                logger.info("getNodeType: " + ASTNode.nodeClassForType(node.getNodeType()));
                logger.info("============== " );

                /*
                try ASTNode
                findDeclaration(IBinding binding, ASTNode root)
                findVariableDeclaration(IVariableBinding binding, ASTNode root)
                getContainingList(ASTNode node)

                or
                TypeAnalyzer extends ASTVisitor
                */

//                CompilationUnit nodeUnit= (CompilationUnit)node ;
//                List<Object> types=nodeUnit.types();
//                for(Object item:types){
//                    logger.info("item: "+item.toString());
//                }

                super.postVisit(node);
            }

//            @Override
//            public boolean visit(SimpleName node) {
////                CompilationUnit nodeUnit=(CompilationUnit)node;
//                logger.info("getIdentifier: " + node.getIdentifier());
//
//                logger.info("isDeclaration: " + node.isDeclaration());
//                logger.info("toString: " + node.toString());
//                return super.visit(node);
//            }
        };
        return visitor;
    }



}
