package polyu_af.models;

/**
 * Created by liushanchen on 16/5/23.
 */
public class LogOutput {

    public String[] getImportPackages() {
        return new String[]{"org.apache.logging.log4j.LogManager", "org.apache.logging.log4j.Logger"};
    }

    public String getDeclaration() {
        return "public static Logger logger =org.apache.logging.log4j.LogManager#getLogger(\"AfTool\");\"";
    }

    public String getLineDivider() {
        return "logger.info(\"---------\");";
    }

    public String getMethodName(String methodName) {
        return "logger.info(\"" + methodName + "\");";
    }

    public String logValStatement(String varName) {
        return "logger.info(\"" + varName + ":\"+(" + varName + "));";
    }

    public String logNInitStatement(String varName) {
        return "logger.info(\"" + varName + ":\"+" + "\": may not initialized.\");";
    }

    public String logConStatement(String varName, String targetClass) {
        return "logger.info(\"" + targetClass + "." + varName + ":\"+(" + targetClass + "." + varName + "));";
    }
}
