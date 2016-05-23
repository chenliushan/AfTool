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
public class ExeTargetRuntime extends ExeTarget{
    private  Logger logger = LogManager.getLogger();

    public ExeTargetRuntime(TargetProgram tp ) {
        super(tp);
    }

    /**
     * Run the modified target .class file
     * in a new thread
     * the log configuration file should be added in the new thread's classpath
     */
    public void process() {
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
        StringBuilder command = new StringBuilder("java -cp .");
        addCp(command);
        addLogLib(command);
        if(tp.getCurrentTarget()!=null){
            getTargetTestE(command);
        }else{
            getTargetMainEntry(command);
        }
        logger.info("command:" + command.toString());
        return command.toString();
    }

    private void addCp(StringBuilder command) {
        String[] cp = tp.getClasspathEntries();
        command.append(":");
        command.append(tp.getOutputPath());
        for (int i = 0; i < cp.length; i++) {
            command.append(":");
            command.append(cp[i]);
            if (cp[i].contains("cofoja")) {
                command.insert(4, " -javaagent:" + cp[i]);
            }
        }
    }

    private void getTargetMainEntry(StringBuilder command) {
        if (tp.getProgramEntry() != null) {
            command.append(" ");
            command.append(tp.getProgramEntry());
        } else {
            logger.info("the program entry is null");
        }
        if (tp.getRunningArg() != null) {
            command.append(" ");
            command.append(tp.getRunningArg());
        }
    }

    private void getTargetTestE(StringBuilder command) {
        command.append(":lib/junit-4.11.jar:lib/hamcrest-core-1.3.jar:");
        command.append(tp.getTestClassPath());//append the test source classpath
        command.append(" org.junit.runner.JUnitCore ");
        command.append(tp.getCurrentTarget().getQualifyFileName()+"Test");//append the [test class name] including the package name

    }

    /**
     * add the log4j2 configuration file in command classpath
     * this method should be called after append classpath
     *
     * @param command
     */
    private void addLogLib(StringBuilder command) {
        command.append(":");
        command.append(System.getProperty("user.dir"));
        command.append("/lib/log4j-api-2.5.jar");
        command.append(":");
        command.append(System.getProperty("user.dir"));
        command.append("/lib/log4j-core-2.5.jar");
        command.append(":");
        command.append(System.getProperty("user.dir"));
        command.append("/logs");
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