package polyu_af.process;

import javassist.*;

import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/*
   This program
   1.compile the target .java file
   2.overwrites sample/Test1.class  for adding print statements
   to print the known field variables and local variables.
   3.run the modified Test1.class file


   To see the modified class definition, execute:

    % javac -classpath .:../../../libs/javassist.jar attempt/Test.java
    % java -classpath .:../../../libs/javassist.jar attempt/Test
    or
    % build gradle
    % java -cp build/libs/Test4Javassist-1.0.jar:libs/javassist.jar attempt.Test

*/
public class RuntimeValue {
    static String projectClassPath = "/Users/liushanchen/IdeaProjects/Test4Javassist";
    static String outputPath = "/build/classes/main/";
    static String sourcePath = "/src/main/java/";

    public static void main(String[] args) throws Exception {
        ClassPool poolParent=getClassPool();

        //Compile the target file
        String targetFile = "attempt/Test1.java";
        compileTarget(targetFile);

        //Modify the target file
        String targetClass = "attempt.Test1";
        modifyTargetClass(poolParent,targetClass);

        //Run the modified target .class file
        runTarget(poolParent,targetClass,args);

    }

    /**
     * get the targetClassPool that contain the target classpath
     * @return targetClassPool
     */
    private static ClassPool getClassPool(){
        ClassPool poolParent = ClassPool.getDefault();
        try {
            poolParent.appendClassPath(projectClassPath + outputPath);
        } catch (NotFoundException e) {
            e.printStackTrace();
        }
        System.out.println("getClassPool:" + poolParent);
        return poolParent;
    }

    /**
     * Compile the target file
     *
     * @param targetFile
     */
    private static void compileTarget(String targetFile) {
        JavaCompiler javaCompiler = ToolProvider.getSystemJavaCompiler();
        StandardJavaFileManager fileManager = javaCompiler.getStandardFileManager(null, null, null);
        Iterable<? extends JavaFileObject> fileObjects =
                fileManager.getJavaFileObjects(projectClassPath + sourcePath +targetFile);
        List<String> options = new ArrayList<String>();
        options.add("-g");
        options.add("-d");
        options.add(projectClassPath + outputPath);
        JavaCompiler.CompilationTask cTask = javaCompiler.getTask(null, null, null, options, null, fileObjects);
        cTask.call();
        try {
            fileManager.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    /**
     * Modify the target file
     * @param pool
     * @param TargetClass
     */
    private static void modifyTargetClass(ClassPool pool, String TargetClass) {
        try {
            CtClass cc = pool.get(TargetClass);
            CtMethod mainMethod = cc.getDeclaredMethod("main");
            mainMethod.insertBefore("System.out.println(\"Hello, I was inserted in the .class\");");
            mainMethod.insertBefore("System.out.println(\"testStringDeclare:\"+testString);");
            mainMethod.insertAt(30, "System.out.println(\"string a:\"+a);");
            mainMethod.insertAt(31, "System.out.println(\"CtClass:\"+cc);");
            System.out.println("main() is found and the statement is inserted.");
            cc.writeFile(projectClassPath + outputPath.substring(0, outputPath.length() - 1));    // update the class file
        } catch (NotFoundException e) {
            e.printStackTrace();
        } catch (CannotCompileException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Run the modified target .class file
     * @param pool
     * @param targetClass
     * @param args
     */
    private static void runTarget(ClassPool pool, String targetClass,String[] args){
        Loader cl = new Loader(pool);
        try {
            cl.run(targetClass, args);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

}
