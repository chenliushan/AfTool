package polyu_af.deprecated;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.dom.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by liushanchen on 16/3/2.
 */
public class AstBasicSample {

    private static Logger logger = LogManager.getLogger(AstBasicSample.class.getName());

    public static void main(String arg[]) {

        String source = readFile(System.getProperty("user.dir") + "/src/main/java/polyu_af/AstBasic.java");
        CompilationUnit node = createNode(source.toCharArray());


        List types = node.types();
        TypeDeclaration typeDec = (TypeDeclaration) types.get(0);
        //show class name
        System.out.println("className:" + typeDec.getName());
        //show fields
        FieldDeclaration fieldDec[] = typeDec.getFields();
        FieldDeclaration fieldDeclaration = (FieldDeclaration)typeDec.getParent();
        IType fieldType = (IType)fieldDeclaration.getType().resolveBinding().getJavaElement();
        System.out.println("Fields:");
        for (FieldDeclaration field : fieldDec) {
            System.out.println("Field fragment:" + field.fragments());
            System.out.println("Field type:" + field.getType());
        }
        getFieldToPostProcess(source,fieldType);
        //analyze
//        analyzeMethod(typeDec);


        /*
        another ways to find the sourceRange
         */
//        ASTParser parser = ASTParser.newParser(AST.JLS8);
//        parser.setKind(ASTParser.K_STATEMENTS);
//        parser.setSource(source.toCharArray());
//        ASTNode unitNode = parser.createAST(null);
//        root.types().get(0)
//        IField field=getFieldToPostProcess(source,(IType) root.types().get(0));
//        ISourceRange nameRange = null;
//        try {
//            nameRange = field.getNameRange();
//        } catch (JavaModelException e) {
//            e.printStackTrace();
//        }
//        if (SourceRange.isAvailable(nameRange)) {
//            if (root != null) {
//                ASTNode nameNode = NodeFinder.perform(root, nameRange);
//                logger.info("*******"+nameNode.toString());
//            }
//        }
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

    private static void showImport(CompilationUnit node) {
        /*
         show import declarations in order
        */
        List importList = node.imports();
        System.out.println("import:");
        for (Object obj : importList) {
            ImportDeclaration importDec = (ImportDeclaration) obj;
            System.out.println(importDec.getName());
        }

    }
    private static void analyzeMethod(TypeDeclaration typeDec) {
        /*
        analyze methods
        */
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
            if (express instanceof MethodInvocation) {
                MethodInvocation mi = (MethodInvocation) express;
                System.out.println("if-invocation name:" + mi.getName());
                System.out.println("if-invocation exp:" + mi.getExpression());
                System.out.println("if-invocation arg:" + mi.arguments());
            } else if (express instanceof InfixExpression) {
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

//                logger.info("preVisit2: " + node.getStartPosition());
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

                /*
                output the node info
                 */
//                CompilationUnit root = (CompilationUnit) node.getRoot();
//                int line = root.getLineNumber(node.getStartPosition());
//                int column = root.getColumnNumber(node.getStartPosition());
//                logger.info("{\"line\":\" " + line + "\", \"column\": \"" + column + "\",\"startPosition\":\" " + node.getStartPosition() + "\",\"length\": \"" + node.getLength() + "\"}");
//                logger.info("line: " + line + "; column: " + column + "; post_getStartPosition: " + node.getStartPosition() + "; post_length: " + node.getLength());//                logger.info("post_getNodeType: " + node.getNodeType());// post_getNodeType: 42
//                logger.info("post_getAST: " + node.getAST());//post_getAST: org.eclipse.jdt.core.dom.AST@38d8f54a
//                logger.info("post_getLocationInParent: " + node.getLocationInParent());//post_getLocationInParent: ChildListProperty[org.eclipse.jdt.core.dom.CompilationUnit,types]
//                logger.info("post_getParent: " + node.getParent());//show the parent code
                /*

                 */
//                Map m = node.properties();
//                logger.info("properties_size: "+m.size());
//                for (Object key : m.keySet()) {
//                    String k = key.toString();
//                    String value = m.get(k).toString();
//                    logger.info("key: " + k + "; value: " + value);
//                }
//                logger.info("post_getParent: " + node.getParent());

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
            @Override
            public boolean visit(VariableDeclarationFragment node) {

//                FieldDeclaration fieldDeclaration = (FieldDeclaration)node.getParent();
//                if (fieldDeclaration.getType().resolveBinding()!=null) {
//                    IType fieldType = (IType) fieldDeclaration.getType().resolveBinding().getJavaElement();
//                    logger.info("*******getFieldToPostProcess:  "+getFieldToPostProcess("visit",fieldType));
//                }

                return false;
            }
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

    /*
    将class静态变量用反射到map
     */
    private static Map<String, Object> dumpProperty2Map(Class obj) {
        Map<String, Object> map = new Hashtable<String, Object>();
        Field[] field1 = obj.getDeclaredFields();
        for (int i = 0; i < field1.length; i++) {
            field1[i].setAccessible(true);
            try {
                map.put(field1[i].getName(), field1[i].get(obj));
            } catch (IllegalArgumentException e1) {
                e1.printStackTrace();
            } catch (IllegalAccessException e2) {
                e2.getStackTrace();
            }
        }
        return map;
    }


    private void useASTRewrite(){
        // creation of a Document
//        ICompilationUnit cu = ... ; // content is "public class X {\n}"
//        String source = cu.getSource();
//        Document document= new Document(source);
//
//        // creation of DOM/AST from a ICompilationUnit
//        ASTParser parser = ASTParser.newParser(AST.JLS3);
//        parser.setSource(cu);
//        CompilationUnit astRoot = (CompilationUnit) parser.createAST(null);
//
//        // creation of ASTRewrite
//        ASTRewrite rewrite = ASTRewrite.create(astRoot.getAST());
//
//        // description of the change
//        SimpleName oldName = ((TypeDeclaration)astRoot.types().get(0)).getName();
//        SimpleName newName = astRoot.getAST().newSimpleName("Y");
//        rewrite.replace(oldName, newName, null);
//
//        // computation of the text edits
//        TextEdit edits = rewrite.rewriteAST(document, cu.getJavaProject().getOptions(true));
//
//        // computation of the new source code
//        edits.apply(document);
//        String newSource = document.get();
//
//        // update of the compilation unit
//        cu.getBuffer().setContents(newSource);

    }
    private void modifytheAST(){
        // creation of a Document
//        ICompilationUnit cu = ... ; // content is "public class X {\n}"
//        String source = cu.getSource();
//        Document document= new Document(source);
//
//        // creation of DOM/AST from a ICompilationUnit
//        ASTParser parser = ASTParser.newParser(AST.JLS3);
//        parser.setSource(cu);
//        CompilationUnit astRoot = (CompilationUnit) parser.createAST(null);
//
//        // start record of the modifications
//        astRoot.recordModifications();
//
//        // modify the AST
//        TypeDeclaration typeDeclaration = (TypeDeclaration)astRoot.types().get(0);
//        SimpleName newName = astRoot.getAST().newSimpleName("Y");
//        typeDeclaration.setName(newName);
//
//        // computation of the text edits
//        TextEdit edits = astRoot.rewrite(document, cu.getJavaProject().getOptions(true));
//
//        // computation of the new source code
//        edits.apply(document);
//        String newSource = document.get();
//
//        // update of the compilation unit
//        cu.getBuffer().setContents(newSource);
    }
}
