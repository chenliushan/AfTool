package polyu_af.utils;

import com.sun.tools.attach.VirtualMachine;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.management.ManagementFactory;

public class MyJavaAgentLoader {
    private static Logger logger = LogManager.getLogger();
    private static final String jarFilePath = "lib/"
            + "Javaagent.jar";

    public static void loadAgent() {
        logger.info("dynamic loading javaagent.");
        String nameOfRunningVM = ManagementFactory.getRuntimeMXBean().getName();
        int p = nameOfRunningVM.indexOf('@');
        String pid = nameOfRunningVM.substring(0, p);
        try {
            VirtualMachine vm = VirtualMachine.attach(pid);
            vm.loadAgent(jarFilePath, "");
            logger.info("detach");
            vm.detach();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}