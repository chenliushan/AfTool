package polyu_af.process;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by liushanchen on 16/5/6.
 */
public class ExeTargetRuntime {
    private static Logger logger = LogManager.getLogger();

    /**
     * Run the modified target .class file
     * in a new thread
     * the log configuration file should be added in the new thread's classpath
     */
    public static void process(String command) {
        Runtime runtime = Runtime.getRuntime();
        System.out.println("command:" + command);

        try {
            Process process = runtime.exec(command);
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

    private static void getProcessOutput(InputStream is) {
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
