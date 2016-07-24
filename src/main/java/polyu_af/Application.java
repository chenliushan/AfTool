package polyu_af;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created by liushanchen on 16/3/17.
 */
public class Application {
    private static Logger logger = LogManager.getLogger(Application.class.getName());

    public static void main(String arg[]) {
        long startTime = System.currentTimeMillis();

        /*************************************************************/

        V2Process process = new V2Process();
        process.run();

        /*************************************************************/

        Runtime runtime = Runtime.getRuntime();
        logger.info("freeMemory:" + runtime.freeMemory() + "; totalMemory:" + runtime.totalMemory());
        long usedMemory = runtime.totalMemory() - runtime.freeMemory();
        logger.info("usedMemory:" + usedMemory);

        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        logger.info("totalTime:" + totalTime);

    }

}
