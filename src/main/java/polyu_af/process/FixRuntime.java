package polyu_af.process;

import javassist.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import polyu_af.GlobalProcess;
import polyu_af.models.AccessVar4Method;
import polyu_af.models.AccessibleVars;
import polyu_af.models.MyExpression;
import polyu_af.utils.ReadFileUtils;

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

*/
public class FixRuntime {
    private static Logger logger = LogManager.getLogger();


    private String[] projectClassPath = null;
    private String outputPath = null;
    private String sourcePath = null;

    private String target = null;
    private String targetFile = null;
    private String targetClass = null;
    private ClassPool poolParent = null;

    public FixRuntime(String outputPath, String[] projectClassPath, String sourcePath) {
        this.outputPath = outputPath;
        this.projectClassPath = projectClassPath;
        this.sourcePath = sourcePath;
        this.poolParent = getClassPool();
    }

    /**
     * @param accessVar4MethodList
     * @param target
     * @param args
     */
    public void process(List<AccessVar4Method> accessVar4MethodList, String target, String[] args) {
        setTarget(target);
        process(accessVar4MethodList, args);
    }

    /**
     * use process(String target) instead.
     *
     * @param args the arguments to run the target
     */
    private void process(List<AccessVar4Method> accessVar4MethodList, String[] args) {
        if (target != null && target.length() > 0) {
            compileTarget();
            modifyTClass(accessVar4MethodList);
            runTarget(args);
        }
    }

    /**
     * get the targetClassPool that contain the target classpath
     *
     * @return targetClassPool
     */
    private ClassPool getClassPool() {
        ClassPool pool = ClassPool.getDefault();
        try {
            pool.appendClassPath(outputPath);
        } catch (NotFoundException e) {
            e.printStackTrace();
        }
//        System.out.println("getClassPool:" + poolParent);
        return pool;
    }

    /**
     * Compile the target file
     */
    private void compileTarget() {
        JavaCompiler javaCompiler = ToolProvider.getSystemJavaCompiler();
        StandardJavaFileManager fileManager = javaCompiler.getStandardFileManager(null, null, null);
        Iterable<? extends JavaFileObject> fileObjects =
                fileManager.getJavaFileObjects(ReadFileUtils.joinDir(sourcePath, targetFile));
        List<String> options = new ArrayList<String>();
        options.add("-g");
        options.add("-d");
        options.add(outputPath);
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
     */
    private void modifyTClass(List<AccessVar4Method> accessVar4MethodList) {
        CtClass cc = null;
        try {
            cc = poolParent.get(targetClass);
            poolParent.importPackage("org.apache.logging.log4j.LogManager");
            poolParent.importPackage("org.apache.logging.log4j.Logger");
            CtField field = CtField.make(" private static Logger logger = LogManager.getLogger();", cc);
            cc.addField(field);
        } catch (NotFoundException e) {
            e.printStackTrace();
            return;
        } catch (CannotCompileException e) {
            e.printStackTrace();
        }
        for (AccessVar4Method methodAccessVar : accessVar4MethodList) {
            try {
                CtMethod mainMethod = cc.getDeclaredMethod(methodAccessVar.getMethodName());
                for (AccessibleVars accessVars : methodAccessVar.getVarsList()) {
                    mainMethod.insertAt(accessVars.getLocation(), "logger.info(\"---------\");");
                    for (MyExpression var : accessVars.getVars()) {
//                        mainMethod.insertAt(accessVars.getLocation(), "System.out.println(\" " + var.getText() + ":\"+" + var.getText() + ");");
                        mainMethod.insertAt(accessVars.getLocation(), "logger.info(\"" + var.getText() + ":\"+" + var.getText() + ");");
                    }

                }
            } catch (NotFoundException e) {
                e.printStackTrace();
            } catch (CannotCompileException e) {
                e.printStackTrace();
            }
        }


    }

    /**
     * Run the modified target .class file
     *
     * @param args
     */
    private void runTarget(String[] args) {
        importTargetCp();
        Loader cl = new Loader(poolParent);
        try {
            cl.run(targetClass, args);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    private void importTargetCp() {
        for (int i = 0; i < projectClassPath.length; i++) {
            try {
                poolParent.insertClassPath(projectClassPath[i]);
            } catch (NotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public void setTarget(String target) {
        if (target.indexOf(".") > 0) {
            if (target.endsWith(".java")) {
                setTarget(target.substring(0, target.length() - 5));
            } else if (target.endsWith(".class")) {
                setTarget(target.substring(0, target.length() - 6));
            }
        } else {
            this.target = target;
            this.targetFile = target + ".java";
            if (target.contains("/")) {
               target= target.replace("/", ".");
            }
            this.targetClass = target ;
            logger.info("targetClass:" + targetClass + "; targetFile:" + targetFile + "; target:" + target);
        }
    }
}
