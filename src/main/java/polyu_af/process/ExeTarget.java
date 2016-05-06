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
            getProcessOutput(process);
            int exitValue = process.waitFor();
            logger.info("exitValue:" + exitValue);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private String getCommand() {
        StringBuilder command = new StringBuilder("java -cp .");
        String[] cp = tp.getClasspathEntries();
        for (int i = 0; i < cp.length; i++) {
            command.append(":" + cp[i]);
        }
        command.append(":" + tp.getOutputPath());
        //add the log4j2 configuration file in path
        command.append(":" + tp.getProjectDir() + "/src/main/resources");
        command.append(" " + tp.getProgramEntry());
        if (tp.getRunningArg() != null) {
            command.append(" " + tp.getRunningArg());
        }
        logger.info("command:" + command.toString());
        return command.toString();
    }

    private void getProcessOutput(Process process) {
        InputStream is = process.getInputStream();
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
