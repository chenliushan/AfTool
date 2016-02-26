package polyu.comp.af;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.dom.*;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.text.edits.TextEdit;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Created by liushanchen on 16/2/24.
 */
public class AstBasic {

    private static void astSample() {//Not used, just for reference
        /*
        创建编译单元(类)
         */
        ASTParser parser = ASTParser.newParser(AST.JLS8);
        parser.setSource("".toCharArray());
        CompilationUnit unit = (CompilationUnit) parser.createAST(new NullProgressMonitor());
        unit.recordModifications();//启动对节点修改的监控。调用这个方法很重要，因为这样可以在以后通过检索节点的修改来访问源代码。
        AST ast = unit.getAST();
        /*
        创建 Package
         */
        PackageDeclaration packageDeclaration = ast.newPackageDeclaration();
        unit.setPackage(packageDeclaration);
        packageDeclaration.setName(ast.newSimpleName("astexplorer"));
        /*
        创建 VariableDeclaration
         */
        VariableDeclarationFragment vdf = ast.newVariableDeclarationFragment();
        vdf.setName(ast.newSimpleName("minimumSize"));
        FieldDeclaration fd = ast.newFieldDeclaration(vdf);
        fd.setModifiers(Modifier.PRIVATE);
        fd.setType(ast.newSimpleType(ast.newSimpleName("Point")));
        /*
        创建方法参数
         */
        SingleVariableDeclaration variableDeclaration = ast.newSingleVariableDeclaration();
        variableDeclaration.setModifiers(Modifier.NONE);
        variableDeclaration.setType(ast.newSimpleType(ast.newSimpleName("Point")));
        variableDeclaration.setName(ast.newSimpleName("size"));
//        methodConstructor.parameters().add(variableDeclaration);
        /*
        创建 Javadoc 节点
         */
        Javadoc jc = ast.newJavadoc();
        TagElement tag = ast.newTagElement();
        TextElement te = ast.newTextElement();
        tag.fragments().add(te);
        te.setText("Sample SWT Composite class created using the ASTParser");
        jc.tags().add(tag);
        tag = ast.newTagElement();
        tag.setTagName(TagElement.TAG_AUTHOR);
        tag.fragments().add(ast.newSimpleName("Manoel Marques"));
        jc.tags().add(tag);
//        classType.setJavadoc(jc);
        /*
        创建语句
         */
        vdf.setName(ast.newSimpleName("gridLayout"));
        VariableDeclarationStatement vds = ast.newVariableDeclarationStatement(vdf);
        vds.setType(ast.newSimpleType(ast.newSimpleName("GridLayout")));
        ClassInstanceCreation cc = ast.newClassInstanceCreation();
        cc.setName(ast.newSimpleName("GridLayout"));
        vdf.setInitializer(cc);
//        constructorBlock.statements().add(vds);
        /*
         创建同一语句的另一种方法
         */
        Assignment a = ast.newAssignment();
        a.setOperator(Assignment.Operator.ASSIGN);
        VariableDeclarationExpression vde = ast.newVariableDeclarationExpression(vdf);
        vde.setType(ast.newSimpleType(ast.newSimpleName("GridLayout")));
        a.setLeftHandSide(vde);

        cc.setName(ast.newSimpleName("GridLayout"));
        a.setRightHandSide(cc);
//        constructorBlock.statements().add(ast.newExpressionStatement(a));

    }


    private void addNodeTest(char[] source) {//Not used, just for reference
        ASTParser parser = ASTParser.newParser(AST.JLS8);
        parser.setSource(source);
        CompilationUnit targetRoot = (CompilationUnit) parser.createAST(null);
        targetRoot.recordModifications();
        parser.setSource("class T{}”".toCharArray());
        CompilationUnit srcRoot = (CompilationUnit) parser.createAST(null);
        //这是非法操作，两者的AST源不一样
        targetRoot.types().add(srcRoot.types().get(0));
        //这是合法操作
        targetRoot.types().add(ASTNode.copySubtree(targetRoot.getAST(), (ASTNode) srcRoot.types().get(0)));
        targetRoot.types().add(targetRoot.getAST().newTypeDeclaration());
    }

    public static void main(String arg[]) {

        String source = readFile(System.getProperty("user.dir") + "/src/main/java/polyu/comp/af/AstBasic.java");
        CompilationUnit node = createNode(source.toCharArray());
        List types = node.types();
        TypeDeclaration typeDec = (TypeDeclaration) types.get(0);
        //show class name
        System.out.println("className:" + typeDec.getName());
        //show fields
        FieldDeclaration fieldDec[] = typeDec.getFields();
        System.out.println("Fields:");
        for (FieldDeclaration field : fieldDec) {
            System.out.println("Field fragment:" + field.fragments());
            System.out.println("Field type:" + field.getType());
        }
        //analyze
        analyzeMethod(typeDec);

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
                    System.err.println("File Error!");
                }
            }
        }
//        System.out.println("fileContent:" + fileContent);
        return fileContent;
    }

    private static CompilationUnit createNode(char[] source) {
        ASTParser parser = ASTParser.newParser(AST.JLS8);
        parser.setKind(ASTParser.K_COMPILATION_UNIT);
        parser.setSource(source);
        CompilationUnit node = (CompilationUnit) parser.createAST(null);
        return node;

    }

    private static Block createBlock(char[] source) {
        ASTParser parser = ASTParser.newParser(AST.JLS8);
        parser.setKind(ASTParser.K_STATEMENTS);
        parser.setSource(source);
        Block block = (Block) parser.createAST(null);
        return block;

    }

    /*
       show import declarations in order
        */
    private static void showImport(CompilationUnit node) {
        List importList = node.imports();
        System.out.println("import:");
        for (Object obj : importList) {
            ImportDeclaration importDec = (ImportDeclaration) obj;
            System.out.println(importDec.getName());
        }

    }

    /*
           analyze methods
            */
    private static void analyzeMethod(TypeDeclaration typeDec) {
        MethodDeclaration methodDec[] = typeDec.getMethods();
        System.out.println("Method:");
        for (MethodDeclaration method : methodDec) {
            //get method name
            SimpleName methodName = method.getName();
            System.out.println("method name:" + methodName);

            //get method parameters
            List param = method.parameters();
            System.out.println("method parameters:" + param);

            //get method return type
            Type returnType = method.getReturnType2();
            System.out.println("method return type:" + returnType);

            //get method body
            MethodBody(method);
        }


    }

    private static void MethodBody(MethodDeclaration method) {
        //get method body
        Block body = method.getBody();
        List statements = body.statements();   //get the statements of the method body
        Iterator iter = statements.iterator();
        while (iter.hasNext()) {
            //get each statement
            Statement stmt = (Statement) iter.next();
            System.out.println("stmt : " + stmt);
            statementAnalyze(stmt);

        }
        System.out.println("====================");
    }

    private static void statementAnalyze(Statement stmt) {
        if (stmt instanceof ExpressionStatement) {
            ExpressionStatement expressStmt = (ExpressionStatement) stmt;
            Expression express = expressStmt.getExpression();
            if (express instanceof Assignment) {
                Assignment assign = (Assignment) express;
                System.out.println("LHS:" + assign.getLeftHandSide() + "; ");
                System.out.println("Op:" + assign.getOperator() + "; ");
                System.out.println("RHS:" + assign.getRightHandSide());

            } else if (express instanceof MethodInvocation) {
                MethodInvocation mi = (MethodInvocation) express;
                System.out.println("invocation name:" + mi.getName());
                System.out.println("invocation exp:" + mi.getExpression());
                System.out.println("invocation arg:" + mi.arguments());
            }
            System.out.println();

        } else if (stmt instanceof IfStatement) {
            IfStatement ifstmt = (IfStatement) stmt;
            Expression express = ifstmt.getExpression();
            if(express instanceof MethodInvocation){
                MethodInvocation mi = (MethodInvocation) express;
                System.out.println("if-invocation name:" + mi.getName());
                System.out.println("if-invocation exp:" + mi.getExpression());
                System.out.println("if-invocation arg:" + mi.arguments());
            }else if(express instanceof InfixExpression){
                InfixExpression wex = (InfixExpression) ifstmt.getExpression();
                System.out.println("if-LHS:" + wex.getLeftOperand() + "; ");
                System.out.println("if-op:" + wex.getOperator() + "; ");
                System.out.println("if-RHS:" + wex.getRightOperand());
            }
            System.out.println();
        } else if (stmt instanceof VariableDeclarationStatement) {
            VariableDeclarationStatement var = (VariableDeclarationStatement) stmt;
            System.out.println("Type of variable:" + var.getType());
            System.out.println("Name of variable:" + var.fragments());
            System.out.println();

        } else if (stmt instanceof ReturnStatement) {
            ReturnStatement rtstmt = (ReturnStatement) stmt;
            System.out.println("return:" + rtstmt.getExpression());
            System.out.println();
        }

    }

}
