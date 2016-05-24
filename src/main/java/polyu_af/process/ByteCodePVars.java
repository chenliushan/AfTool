package polyu_af.process;

import javassist.CannotCompileException;
import javassist.CtBehavior;
import javassist.CtClass;
import javassist.NotFoundException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import polyu_af.models.*;

import java.io.IOException;
import java.util.List;

/**
 * Created by liushanchen on 16/5/23.
 * This program
 * 1.compile the target .java file
 * 2.overwrites current target file by adding log statements
 * to print the known field variables and local variables in runtime.
 * Note: the javassist's compiler is not support inner class
 * -get method by method name and parameters
 * -if the local variable is not initialized just log a string that the variable is not initialized
 */
public class ByteCodePVars extends ByteCodeP {
    private Logger logger = LogManager.getLogger();
    private boolean isNestedMethod = false;
    private TargetFile tf = null;
    private CtClass cc = null;

    public ByteCodePVars(TargetProgram targetProgram) {
        super(targetProgram.getTc());
    }

    /**
     * use process(String target) instead.
     */
    public void process(TargetFile targetFile) {
        this.tf = targetFile;
        if (tc != null && tf != null) {
            compileTarget(tf.getAbsoluteDir());
            cc = findTargetClass();
            if (cc != null) {
                importLogPack(cc);
                //For every method in the list
                forMethods(tf.getMethodAccessVars());
                rewrite(cc);
            }
        }
    }

    /**
     * find target class
     *
     * @return CtClass
     */
    private CtClass findTargetClass() {
        CtClass cc = null;
        try {
            cc = poolParent.get(tf.getQualifyFileName());
        } catch (NotFoundException e) {
            e.printStackTrace();
        }
        return cc;

    }

    /**
     * rewrite the bytecode file
     */
    private void rewrite(CtClass ctClass) {
        try {
            ctClass.writeFile(tc.getOutputPath());
        } catch (CannotCompileException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void forMethods(List<MethodAccessVars> accessVar4MethodList) {
        for (MethodAccessVars methodAccessVar : accessVar4MethodList) {
            isNestedMethod = false;
            //find the method in the target bytecode file
            CtBehavior mainMethod = findTMethod(methodAccessVar);
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
    }

    /**
     * find the method in the target
     *
     * @param methodAccessVar
     * @return
     */
    private CtBehavior findTMethod(MethodAccessVars methodAccessVar) {
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
                            declareLogger(nccs[i]);
                            logNestedCVarValue(methodAccessVar.getVarsList(), mainMethod);
                            nccs[i].writeFile(tc.getOutputPath());
//                            rewrite(nccs[i]);
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
                mainMethod.insertAt(accessVars.getLocation(), lo.getLineDivider());

                for (MyExp var : accessVars.getVarsList()) {
                    try {
                        mainMethod.insertAt(accessVars.getLocation(), lo.logValStatement(var.getExpVar()));
                    } catch (CannotCompileException e) {
                        mainMethod.insertAt(accessVars.getLocation(), lo.logNInitStatement(var.getExpVar()));
                        logger.error("CannotCompileException: location:" + accessVars.getLocation() + "var:" + var);
                    }

                }
            } catch (CannotCompileException e) {
                logger.error("location:" + accessVars.getLocation() + "vars:" + accessVars.getVarsList());
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
                mainMethod.insertAt(accessVars.getLocation(), lo.getLineDivider());
                //for every var that is accessible in the line
                for (MyExp var : accessVars.getVarsList()) {

                    try {
                        mainMethod.insertAt(accessVars.getLocation(), lo.logConStatement(var.getExpVar(), tf.getQualifyFileName()));
                    } catch (CannotCompileException e) {
                        try {
                            mainMethod.insertAt(accessVars.getLocation(), lo.logValStatement(var.getExpVar()));
                        } catch (CannotCompileException e1) {
                            mainMethod.insertAt(accessVars.getLocation(), lo.logNInitStatement(var.getExpVar()));
                            logger.error(" Nested CannotCompileException: location:"
                                    + accessVars.getLocation() + "var:"
                                    + tf.getQualifyFileName() + "." + var.getExpVar());
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

