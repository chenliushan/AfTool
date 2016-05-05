package polyu_af.process;

import javassist.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import polyu_af.models.AccessVar4Method;
import polyu_af.models.AccessibleVars;
import polyu_af.models.MyExpression;
import polyu_af.models.TargetProgram;
import polyu_af.utils.ReadFileUtils;

import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
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

    private TargetProgram tp = null;
    private String target = null;
    private String targetFile = null;
    private String targetClass = null;
    private ClassPool poolParent = null;

    public FixRuntime(TargetProgram targetProgram) {
        this.tp = targetProgram;
        this.poolParent = getClassPool();
    }

    /**
     * @param accessVar4MethodList
     * @param target
     */
    public void process(List<AccessVar4Method> accessVar4MethodList, String target) {
        setTarget(target);
        process(accessVar4MethodList);
    }

    /**
     * use process(String target) instead.
     */
    private void process(List<AccessVar4Method> accessVar4MethodList) {
        if (target != null && target.length() > 0) {
            compileTarget();
            modifyTClass(accessVar4MethodList);
//            runTarget();
            runIt();
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
            pool.appendClassPath(tp.getOutputPath());
        } catch (NotFoundException e) {
            e.printStackTrace();
        }

        return pool;
    }

    /**
     * Compile the target file
     */
    private void compileTarget() {
        JavaCompiler javaCompiler = ToolProvider.getSystemJavaCompiler();
        StandardJavaFileManager fileManager = javaCompiler.getStandardFileManager(null, null, null);
        Iterable<? extends JavaFileObject> fileObjects =
                fileManager.getJavaFileObjects(ReadFileUtils.joinDir(tp.getSourcePath(), targetFile));
        List<String> options = new ArrayList<String>();
        options.add("-g");
        options.add("-d");
        options.add(tp.getOutputPath());
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
        try {
            for (AccessVar4Method methodAccessVar : accessVar4MethodList) {

                CtMethod mainMethod = cc.getDeclaredMethod(methodAccessVar.getMethodName());
                for (AccessibleVars accessVars : methodAccessVar.getVarsList()) {
                    mainMethod.insertAt(accessVars.getLocation(), "logger.info(\"---------\");");
                    for (MyExpression var : accessVars.getVars()) {
                        mainMethod.insertAt(accessVars.getLocation(), "logger.info(\"" + var.getText() + ":\"+" + var.getText() + ");");
                    }

                }
            }
            cc.writeFile(tp.getOutputPath());    // update the class file
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
     */
    private void runTarget() {
        importTargetCp();
        Loader cl = new Loader(poolParent);
        try {
            logger.info("==================Target Program Log==================");
            cl.run(tp.getProgramEntry(), tp.getRunningArg());
            logger.info("===============End of Target Program Log==============");
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    private void importTargetCp() {
        String[] projectClassPath = tp.getClasspathEntries();
        for (int i = 0; i < projectClassPath.length; i++) {
            try {
                poolParent.insertClassPath(projectClassPath[i]);
            } catch (NotFoundException e) {
                e.printStackTrace();
            }
        }
    }
//
//    private List<URL> getURLs(String path) {
//        File[] files = new File(path).listFiles();
//        // Convert File to a URL
//        List<URL> urls = new ArrayList<URL>();
//        for (File file : files) {
//            try {
//                logger.info("file:" + file.toURL());
//            } catch (MalformedURLException e) {
//                e.printStackTrace();
//            }
//
//            if (file.isDirectory()) {
//                urls.addAll(getURLs(file.getAbsolutePath()));
//            } else {
//                try {
//                    urls.add(file.toURL()); // fil// e:/classes/demo
//                } catch (MalformedURLException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//        return urls;
//    }
    private URL getURL(String path) {
        File files = new File(path);
        try {
            return files.toURL();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void runIt() {
        URL[] urls = new URL[]{getURL(tp.getOutputPath())};
        ClassLoader loader = new URLClassLoader(urls);
        try {
            Class thisClass = loader.loadClass(tp.getProgramEntry());
            Object iClass = thisClass.newInstance();
            Method thisMethod = thisClass.getDeclaredMethod("main", String[].class);
            thisMethod.invoke(iClass, (Object) tp.getRunningArg());
        } catch (Exception e) {
            e.printStackTrace();

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
                target = target.replace("/", ".");
            }
            this.targetClass = target;
        }
    }
}
