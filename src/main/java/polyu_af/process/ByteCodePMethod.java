package polyu_af.process;

import javassist.CannotCompileException;
import javassist.CtBehavior;
import javassist.CtClass;
import javassist.NotFoundException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import polyu_af.models.TargetFile;
import polyu_af.models.TargetProgram;

import java.io.IOException;
import java.util.Arrays;

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
public class ByteCodePMethod extends ByteCodeP {
    private Logger logger = LogManager.getLogger();
    private TargetFile tf = null;
    private CtClass cc = null;

    public ByteCodePMethod(TargetProgram targetProgram) {
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
                findMethods(cc);
                findNestedClass();
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



    /**
     * find the method in the target
     *
     * @return
     */
    private void findMethods(CtClass ctClass) {
        for(CtBehavior cb:Arrays.asList(ctClass.getDeclaredMethods())){
            try {
                cb.insertBefore(lo.getMethodName(cb.getGenericSignature()));
            } catch (CannotCompileException e) {
                e.printStackTrace();
                logger.error("CannotCompileException:"+cb.getName()+":"+e.getReason());
            }
        }
        for(CtBehavior cb:Arrays.asList(ctClass.getDeclaredConstructors())){
            try {
                cb.insertAfter(lo.getMethodName(cb.getSignature()));
            } catch (CannotCompileException e) {
                e.printStackTrace();
            }
        }
    }
    private void findNestedClass() {
        try {
            CtClass[] nccs = cc.getNestedClasses();
            for (int i = 0; i < nccs.length; i++) {
                declareLogger(nccs[i]);
                findMethods(nccs[i]);
                rewrite(nccs[i]);
            }
        } catch (NotFoundException e) {
            e.printStackTrace();
        }
    }

}

