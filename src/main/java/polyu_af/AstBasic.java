package polyu_af;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jdt.core.dom.*;

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
        CompilationUnit node = createNode(source.toCharArray());
        node.accept(visitorTest()); //Use visitor to go through the AST
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

        ASTVisitor visitor = new ASTVisitor() {
            /*
            when the visitor visit a node, the calling order is:
            preVisit2()
            preVisit()
            visit()
            endVisit()
            postVisit()
             */


            @Override
            public boolean preVisit2(ASTNode node) {
                /*
                The same as the preVisit method, but
                Returns:true if visit(node) should be called, and false otherwise.
                 */

                logger.info("preVisit2: " + node.getStartPosition());
                return super.preVisit2(node);
            }

            @Override
            public void preVisit(ASTNode node) {
                /*
                 Visits the given node to perform some arbitrary operation.
                 This method is invoked prior to the appropriate type-specific visit method.
                 The default implementation of this method does nothing. Subclasses may reimplement this method as needed.
                 */
                super.preVisit(node);
            }

            @Override
            public void postVisit(ASTNode node) {
                /*
                Visits the given node to perform some arbitrary operation.
                This method is invoked after the appropriate type-specific endVisit method.
                The default implementation of this method does nothing. Subclasses may reimplement this method as needed.
                 */

                logger.info("post_getNodeType: " + node.getNodeType());
                logger.info("post_getAST: " + node.getAST());
                logger.info("post_getFlags: " + node.getFlags());
                logger.info("post_getLocationInParent: " + node.getLocationInParent());
                logger.info("post_getStartPosition: " + node.getStartPosition());
                Map m = node.properties();
                logger.info("properties_size: "+m.size());
                for (Object key : m.keySet()) {
                    String k = key.toString();
                    String value = m.get(k).toString();
                    logger.info("key: " + k + "; value: " + value);
                }
//                logger.info("post_getParent: "+node.getParent());
                super.postVisit(node);
            }

//
//            @Override
//            public boolean visit(ImportDeclaration node) {
//                /*
//                show import declarations in order
//                 */
////                logger.info("ImportDeclaration: "+node.getName());
//                return true;
//            }
//
//            @Override
//            public boolean visit(MethodDeclaration node) {
//                 /*
//                show method info
//                 */
//                logger.info("MethodDeclaration: "+node.getName());
//                logger.info("getReturnType2: "+node.getReturnType2());
//                logger.info("parameters: "+node.parameters());
//                logger.info("getBody.statements.size: "+node.getBody().statements().size());
//                /*
//                can also get all the method's statements and do some analysis
//                but the visitor itself will still go iterate the chile of the method node
//                no need to do that here.
//                 */
//
////                List<Object> mStatement=node.getBody().statements();
////                Iterator<Object> iterator=mStatement.iterator();
////                while (iterator.hasNext()){
////                    statementAnalyze((Statement) iterator.next());
////                }
//                return true;
//            }
//
//            @Override
//            public boolean visit(ExpressionStatement node) {
//
//                return true;
//            }
//
//            @Override
//            public boolean visit(MethodInvocation mi) {
//                if (mi.getExpression() instanceof MethodInvocation) {
////                    logger.info("mi_old: "+mi.getExpression());
////                    mi.setExpression(null);
////                    logger.info("mi_new: "+mi.getExpression());
//                }
//                return true;
//            }
        };
        logger.info("visitor" + visitor);
        return visitor;
    }

//    private static void statementAnalyze(Statement stmt) { //just for reference
//        if (stmt instanceof ExpressionStatement) {
//            ExpressionStatement expressStmt = (ExpressionStatement) stmt;
//            Expression express = expressStmt.getExpression();
//            if (express instanceof Assignment) {
//                Assignment assign = (Assignment) express;
//                logger.info("LHS: " + assign.getLeftHandSide());
//                logger.info("OP: " + assign.getOperator());
//                logger.info("RHS: " + assign.getRightHandSide());
//            } else if (express instanceof MethodInvocation) {
//                MethodInvocation mi = (MethodInvocation) express;
//                logger.info("invocation name: " + mi.getName());
//                logger.info("invocation exp: " + mi.getExpression());
//                logger.info("invocation arg: " + mi.arguments());
//            }
//        } else if (stmt instanceof IfStatement) {
//            IfStatement ifstmt = (IfStatement) stmt;
//            Expression express = ifstmt.getExpression();
//            if (express instanceof MethodInvocation) {
//                MethodInvocation mi = (MethodInvocation) express;
//                logger.info("if-invocation name: " + mi.getName());
//                logger.info("if-invocation exp: " + mi.getExpression());
//                logger.info("if-invocation arg: " + mi.arguments());
//            } else if (express instanceof InfixExpression) {
//                InfixExpression wex = (InfixExpression) ifstmt.getExpression();
//                logger.info("if-LHS: " + wex.getLeftOperand());
//                logger.info("if-OP: " + wex.getOperator());
//                logger.info("if-RHS: " + wex.getRightOperand());
//            }
//        } else if (stmt instanceof VariableDeclarationStatement) {
//            VariableDeclarationStatement var = (VariableDeclarationStatement) stmt;
//            logger.info("Type of variable: " + var.getType());
//            logger.info("Name of variable: " + var.getType());
//        } else if (stmt instanceof ReturnStatement) {
//            ReturnStatement rtstmt = (ReturnStatement) stmt;
//            logger.info("return: " + rtstmt.getExpression());
//        }
//
//    }

}
