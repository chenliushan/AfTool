package polyu_af.process;

import javassist.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import polyu_af.models.LineAccessVars;
import polyu_af.models.MethodAccessVars;
import polyu_af.models.MyExp;
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
import java.util.Map;

/*
   This program
   1.compile the target .java file
   2.overwrites sample/Test1.class  for adding print statements
   to print the known field variables and local variables.
   -get method by method name and parameters
   -can find the method in a inner class
   -if the local variable is not initialized just log a string that the variable is not initialized
*/
public class ByteCodeMLogVar {
    private static Logger logger = LogManager.getLogger();

    private TargetProgram tp = null;
    private String target = null;
    private String targetFile = null;
    private String targetClass = null;
    private String constructorN = null;
    private ClassPool poolParent = null;

    private boolean isNestedMethod = false;

    public ByteCodeMLogVar(TargetProgram targetProgram) {
        this.tp = targetProgram;
        this.poolParent = getClassPool();
    }

    /**
     * @param vars
     * @param target
     */
    public void process(List<MethodAccessVars> vars, String target) {
        setTarget(target);
        process(vars);
    }

    /**
     * use process(String target) instead.
     */
    private void process(List<MethodAccessVars> vars) {
        if (target != null && target.length() > 0) {
            compileTarget();
            modifyTClass(vars);
            ExeTarget exeTarget = new ExeTarget(tp, targetClass);
            exeTarget.runTarInNThread();
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
            String[] cp = tp.getClasspathEntries();
            for (int i = 0; i < cp.length; i++) {
                pool.appendClassPath(cp[i]);
            }
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
        List<String> options = tp.getCompileOptions();
        JavaCompiler.CompilationTask cTask = javaCompiler.getTask(null, null, null, options, null, fileObjects);
        cTask.call();
        try {
            fileManager.close();
        } catch (IOException e) {
            logger.error("recompile -g error!!");
            e.printStackTrace();
        }
    }



    /**
     * Modify the target bytecode file
     *
     * @param accessVar4MethodList the accessible variables for every line group by method (from AST analysis)
     */
    private void modifyTClass(List<MethodAccessVars> accessVar4MethodList) {
        CtClass cc = null;
        try {
            cc = poolParent.get(targetClass);
        } catch (NotFoundException e) {
            e.printStackTrace();
            return;
        }
        importPack(cc);
        //For every method in the list
        for (MethodAccessVars methodAccessVar : accessVar4MethodList) {
            isNestedMethod = false;
            //find the method in the target bytecode file
            CtBehavior mainMethod = findTMethod(methodAccessVar, cc);
            //insert log to get the value of every accessible variable
            if (mainMethod == null) {
                logger.error("mainMethod==null  did not get the method name: " + methodAccessVar.getMethodName() + "!!!!!!");
                continue;
            } else {
                if (!isNestedMethod) {
                    logVarValue(methodAccessVar.getVarsList(), mainMethod);

                }
            }
        }
        // rewrite the bytecode file
        try {
            cc.writeFile(tp.getOutputPath());
        } catch (CannotCompileException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * import the Log package and declare the log variable
     *
     * @param cc the target bytecode file
     */
    private void importPack(CtClass cc) {
        poolParent.importPackage("org.apache.logging.log4j.LogManager");
        poolParent.importPackage("org.apache.logging.log4j.Logger");
        try {
            CtField field = CtField.make(" public static Logger logger =org.apache.logging.log4j.LogManager#getLogger();", cc);
            cc.addField(field);
        } catch (CannotCompileException e) {
            e.printStackTrace();
        }
    }

    /**
     * find the method in the target
     *
     * @param methodAccessVar
     * @param cc
     * @return
     */
    private CtBehavior findTMethod(MethodAccessVars methodAccessVar, CtClass cc) {
        CtBehavior mainMethod = null;
        List<CtClass> ps = methodAccessVar.getParams(poolParent);
        CtClass[] prams = ps.toArray(new CtClass[ps.size()]);
        if (constructorN.equals(methodAccessVar.getMethodName())) {
            try {
                mainMethod = cc.getDeclaredConstructor(prams);
            } catch (NotFoundException e) {
                e.printStackTrace();
            }
        } else {
            try {
                mainMethod = cc.getDeclaredMethod(methodAccessVar.getMethodName(), prams);
            } catch (NotFoundException e) {
                try {
                    CtClass[] nccs = cc.getNestedClasses();
                    //find the first method has that name and paras in nested classes
                    for (int i = 0; i < nccs.length; i++) {
                        try {
                            mainMethod = nccs[i].getDeclaredMethod(methodAccessVar.getMethodName(), prams);
                        } catch (NotFoundException e1) {
                            continue;
                        }
                        if (mainMethod != null) {
                            isNestedMethod = true;
                            importPack(nccs[i]);
                            logNestedCVarValue(methodAccessVar.getVarsList(), mainMethod);
                            nccs[i].writeFile(tp.getOutputPath());
                            break;
                        }
                    }
                } catch (NotFoundException e1) {
                    e1.printStackTrace();
                } catch (CannotCompileException e1) {
                    e1.printStackTrace();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }

            }
        }
        return mainMethod;
    }


    /**
     * insert log to get the value of every accessible variable
     *
     * @param varsList   cluster of vars of line
     * @param mainMethod the method get from bytecode file
     */
    private void logVarValue(List<LineAccessVars> varsList, CtBehavior mainMethod) {

        for (LineAccessVars accessVars : varsList) {
            try {
                mainMethod.insertAt(accessVars.getLocation(), "logger.info(\"---------\");");
                for (Map.Entry<String, MyExp> var : accessVars.getVars().entrySet()) {
                    try {
                        mainMethod.insertAt(accessVars.getLocation(), logValStatement(var.getValue().getAstNodeVar()));
                    } catch (CannotCompileException e) {
                        mainMethod.insertAt(accessVars.getLocation(), logNInitStatement(var.getValue().getAstNodeVar()));
                        logger.error("CannotCompileException: location:" + accessVars.getLocation() + "var:" + var);
                    }

                }
            } catch (CannotCompileException e) {
                logger.error("location:" + accessVars.getLocation() + "vars:" + accessVars.getVars());
                e.printStackTrace();
            }
        }
    }

    /**
     * insert log to get the value of every accessible variable
     *
     * @param varsList   cluster of vars of line
     * @param mainMethod the method get from bytecode file
     */
    private void logNestedCVarValue(List<LineAccessVars> varsList, CtBehavior mainMethod) {
        //for very 'line' in the method
        for (LineAccessVars accessVars : varsList) {
            try {
                mainMethod.insertAt(accessVars.getLocation(), "logger.info(\"---------\");");
                //for every var that is accessible in the line
                for (Map.Entry<String, MyExp> var : accessVars.getVars().entrySet()) {
                    try {
                        mainMethod.insertAt(accessVars.getLocation(), logConStatement(var.getValue().getAstNodeVar()));
                    } catch (CannotCompileException e) {
                        try {
                            mainMethod.insertAt(accessVars.getLocation(), logValStatement(var.getValue().getAstNodeVar()));
                        } catch (CannotCompileException e1) {
                            mainMethod.insertAt(accessVars.getLocation(), logNInitStatement(var.getValue().getAstNodeVar()));
                            logger.error("CannotCompileException: location:" + accessVars.getLocation() + "var:" + var);
                        }
                    }
                }
            } catch (CannotCompileException e) {
                logger.error("Nested location:" + accessVars.getLocation());
                e.printStackTrace();
            }
        }
    }

    private String logValStatement(String varName) {
        return "logger.info(\"" + varName + ":\"+(" + varName + "));";
    }

    private String logNInitStatement(String varName) {
        return "logger.info(\"" + varName + ":\"+" + "\": may not initialized.\");";
    }

    private String logConStatement(String varName) {
        return "logger.info(\"" + constructorN + "." + varName + ":\"+(" + targetClass + "." + varName + "));";
    }

    /**
     * Run the modified target .class file
     * with the javassit loader
     * (If the target app contains a javassist loader, there will be an error. )
     * in the current thread
     * the log will use current tool's log configuration
     */
    @Deprecated
    private void runTarLoadByJs() {
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

    /**
     * Run the modified target .class file
     * with the URLClassLoader
     * (the target app can run another nesting it without any error)
     * in the current thread
     * the log will use current tool's log configuration
     */
    @Deprecated
    private void runTarLoadByJn() {
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

    private URL getURL(String path) {
        File files = new File(path);
        try {
            return files.toURI().toURL();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }


    private void setTarget(String target) {
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
            int idx = targetClass.indexOf(".");
            if (idx > 0) {
                this.constructorN = targetClass.substring(idx + 1);
            } else {
                this.constructorN = targetClass;
            }
        }
    }
}
