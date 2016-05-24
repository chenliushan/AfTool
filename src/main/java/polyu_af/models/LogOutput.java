package polyu_af.models;

/**
 * Created by liushanchen on 16/5/23.
 */
public class LogOutput {

    private String loggerName="afToolLogger";

    public LogOutput() {
    }
    public LogOutput(String loggerName) {
        this.loggerName = loggerName;
    }

    public String[] getImportPackages() {
        return new String[]{"org.apache.logging.log4j.LogManager", "org.apache.logging.log4j.Logger"};
    }

    public String getDeclaration() {
        return "public static Logger "+loggerName+" =org.apache.logging.log4j.LogManager#getLogger(\"AfTool\");\"";
    }

    public String getLineDivider() {
        return loggerName+".info(\"---------\");";
    }

    public String getMethodName(String methodName) {
        return loggerName+".info(\"" + methodName + "\");";
    }

    public String logValStatement(String varName) {
        return loggerName+".info(\"" + varName + ":\"+(" + varName + "));";
    }

    public String logNInitStatement(String varName) {
        return loggerName+".info(\"" + varName + ":\"+" + "\": may not initialized.\");";
    }

    public String logConStatement(String varName, String targetClass) {
        return loggerName+".info(\"" + targetClass + "." + varName + ":\"+(" + targetClass + "." + varName + "));";
    }
}
