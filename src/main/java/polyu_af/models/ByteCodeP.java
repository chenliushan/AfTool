package polyu_af.models;

import javassist.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import polyu_af.process.ExeTarget;
import polyu_af.process.ExeTargetRuntime;
import polyu_af.utils.MyJavaAgentLoader;

import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Created by liushanchen on 16/5/23.
 * This program
 * 1.compile the target .java file
 * 2.overwrites sample/Test1.class  for adding print statements
 * to print the known field variables and local variables.
 * -get method by method name and parameters
 * -can find the method in a inner class
 * -if the local variable is not initialized just log a string that the variable is not initialized
 */
public class ByteCodeP {
    private static Logger logger = LogManager.getLogger();

    private TargetProgram tp = null;
    private ClassPool poolParent = null;

    private TargetFile tf = null;
    private boolean isNestedMethod = false;

    private LogOutput lo = null;

    public ByteCodeP(TargetProgram targetProgram) {
        this.tp = targetProgram;
        this.tf = tp.getCurrentTarget();
        logger.info("getCurrentTarget:"+tf);
        this.poolParent = getClassPool();
        this.lo = new LogOutput();
    }

    /**
     * use process(String target) instead.
     */
    public void process(List<MethodAccessVars> vars) {
        if (tp != null && tp.getCurrentTarget() != null) {
            MyJavaAgentLoader.loadAgent();
            compileTarget();
            modifyTClass(vars);
            ExeTarget exeTarget = new ExeTargetRuntime(tp);
            exeTarget.process();
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
                fileManager.getJavaFileObjects(tf.getAbsoluteDir());
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
            cc = poolParent.get(tf.getQualifyFileName());
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
        String[] importP = lo.getImportPackages();
        for (int i = 0; i < importP.length; i++) {
            poolParent.importPackage(importP[i]);

        }
        try {
            CtField field = CtField.make(lo.getDeclaration(), cc);
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
        if (tf.getFileName().equals(methodAccessVar.getMethodName())) {
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
                        mainMethod.insertAt(accessVars.getLocation(), lo.logValStatement(var.getValue().getAstNodeVar()));
                    } catch (CannotCompileException e) {
                        mainMethod.insertAt(accessVars.getLocation(), lo.logNInitStatement(var.getValue().getAstNodeVar()));
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
                        mainMethod.insertAt(accessVars.getLocation(), lo.logConStatement(var.getValue().getAstNodeVar(), tf.getFileName()));
                    } catch (CannotCompileException e) {
                        try {
                            mainMethod.insertAt(accessVars.getLocation(), lo.logValStatement(var.getValue().getAstNodeVar()));
                        } catch (CannotCompileException e1) {
                            mainMethod.insertAt(accessVars.getLocation(), lo.logNInitStatement(var.getValue().getAstNodeVar()));
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


}

