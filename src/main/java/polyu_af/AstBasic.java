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
import java.util.Map;

/**
 * Created by liushanchen on 16/2/24.
 */
public class AstBasic {
    private static Logger logger = LogManager.getLogger(AstBasic.class.getName());


    public static void main(String arg[]) {

        String source = readFile(System.getProperty("user.dir") + "/src/main/java/polyu_af/AstBasic.java");
        CompilationUnit root = createNode(source.toCharArray());
        /*
        Use visitor to go through the AST
         */
        root.accept(visitorTest());
        /*
        Read the inputs and find the node
         */
        findTheNodePL(root);

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
                ASTNode nodeScope = nodeFinder.getCoveringNode();
                ASTNode targetNode = nodeFinder.getCoveredNode();
                logger.info("nodeFinder.getCoveringNode(): " + nodeScope);
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
//                CompilationUnit root = (CompilationUnit) node.getRoot();
//                int line = root.getLineNumber(node.getStartPosition());
//                int column = root.getColumnNumber(node.getStartPosition());
//                logger.info("{\"line\":\" " + line + "\", \"column\": \"" + column + "\",\"startPosition\":\" " + node.getStartPosition() + "\",\"length\": \"" + node.getLength() + "\"}");
                super.postVisit(node);
            }
        };
        return visitor;
    }
    private static IField getFieldToPostProcess(String par, IType type) {
        String[] candidates = par.trim().split(" ");
        for (String candidate : candidates) {
            IField field = type.getField(candidate);
            if (field != null && field.exists()) {
                return field;
            }
        }
        return null;
    }


}
