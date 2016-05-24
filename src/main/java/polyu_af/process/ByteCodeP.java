package polyu_af.process;

import javassist.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import polyu_af.models.LogOutput;
import polyu_af.models.TargetConfig;
import polyu_af.models.TargetFile;
import polyu_af.utils.MyJavaAgentLoader;

import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import java.io.IOException;
import java.util.List;

/**
 * Created by liushanchen on 16/5/23.
 *
 */
public abstract class ByteCodeP {
    private Logger logger = LogManager.getLogger();

    protected TargetConfig tc = null;
    protected ClassPool poolParent = null;
    protected LogOutput lo = null;

    public ByteCodeP(TargetConfig targetConfig) {
        this.tc = targetConfig;
        this.poolParent = getClassPool();
        this.lo = new LogOutput();
//        MyJavaAgentLoader.loadAgent();
    }

    public abstract void process(TargetFile tf);
    /**
     * get the targetClassPool that contain the target classpath
     *
     * @return targetClassPool
     */
    private ClassPool getClassPool() {
        ClassPool pool = ClassPool.getDefault();
        try {
            pool.appendClassPath(tc.getOutputPath());
            String[] cp = tc.getClasspathEntries();
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
    protected void compileTarget(String absoluteDir) {
        JavaCompiler javaCompiler = ToolProvider.getSystemJavaCompiler();
        StandardJavaFileManager fileManager = javaCompiler.getStandardFileManager(null, null, null);
        Iterable<? extends JavaFileObject> fileObjects =
                fileManager.getJavaFileObjects(absoluteDir);
        List<String> options = tc.getCompileOptions();
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
     * import the Log package and declare the log variable
     *
     * @param cc the target bytecode file
     */
    protected void importLogPack(CtClass cc) {
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
     protected void declareLogger(CtClass cc) {
        try {
            CtField field = CtField.make(lo.getDeclaration(), cc);
            cc.addField(field);
        } catch (CannotCompileException e) {
            e.printStackTrace();
        }
    }



}

