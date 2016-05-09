package polyu_af.process;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import polyu_af.models.TargetProgram;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by liushanchen on 16/5/6.
 */
public class ExeTarget {
    private static Logger logger = LogManager.getLogger();

    private TargetProgram tp = null;

    public ExeTarget(TargetProgram tp) {
        this.tp = tp;
    }

    /**
     * Run the modified target .class file
     * in a new thread
     * the log configuration file should be added in the new thread's classpath
     */
    public void runTarInNThread() {
        Runtime runtime = Runtime.getRuntime();
        try {
            Process process = runtime.exec(getCommand());
            getProcessOutput(process.getInputStream());
            getProcessOutput(process.getErrorStream());
            int exitValue = process.waitFor();
            logger.info("exitValue:" + exitValue);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private String getCommand() {
        StringBuilder command = new StringBuilder("java -javaagent:"+tp.getProjectDir()+"/lib/cofoja-1.3-20160207.jar -cp .");
        String[] cp = tp.getClasspathEntries();
        for (int i = 0; i < cp.length; i++) {
            command.append(":" + cp[i]);
        }
        command.append(":" + tp.getOutputPath());
        //add the log4j2 configuration file in path
        command.append(":"+ System.getProperty("user.dir")+ "/lib/log4j-api-2.5.jar");
        command.append(":"+ System.getProperty("user.dir")+ "/lib/log4j-core-2.5.jar");
        command.append(":"+ System.getProperty("user.dir")+ "/logs");
        command.append(" " + tp.getProgramEntry());
        if (tp.getRunningArg() != null) {
            command.append(" " + tp.getRunningArg());
        }
        logger.info("command:" + command.toString());
        return command.toString();
    }

    private void getProcessOutput(InputStream is) {
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr);
        String line = null;
        try {
            while ((line = br.readLine()) != null)
                logger.info(">>>" + line);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
