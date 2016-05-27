package polyu_af.models;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created by liushanchen on 16/5/26.
 */
public class ExeCommand {
    private static Logger logger = LogManager.getLogger();

    private TargetConfig tc = null;

    public ExeCommand(TargetConfig tc) {
        this.tc = tc;
    }

    /**
     * this method returns the exe command that
     * loading target program with the MLogAgent
     * and then run the target program with the entry of main method
     *
     * @return command string
     */
    public String getMLogAgCommand() {
        StringBuilder command = initMLogAgCommand();
        commandTgMainEntry(command);
        return command.toString();
    }

    /**
     * and then run the target program with the entry of Target Test Class
     *
     * @return command string
     */
    public String getMLogAgCommand(String tgQualifyFileName) {
        StringBuilder command = initMLogAgCommand();
        commandTgTestClass(command, tgQualifyFileName);
        return command.toString();
    }

    /**
     * and then run the target program with the entry of Specific test method of a test class
     *
     * @return command string
     */
    public String getMLogAgCommand(String tgQualifyFileName, String methodName) {
        StringBuilder command = initMLogAgCommand();
        commandTfTestMethod(command, tgQualifyFileName, methodName);
        return command.toString();
    }

    /**
     * this method returns the exe command that
     * loading target program with the MLogAgent
     * and then run the target program with the entry of main method
     *
     * @return command string
     */
    public String getVarLogAgCommand() {
        StringBuilder command = initVarLogAgCommand();
        commandTgMainEntry(command);
        return command.toString();
    }

    /**
     * and then run the target program with the entry of Target Test Class
     *
     * @return command string
     */
    public String getVarLogAgCommand(String tgQualifyFileName) {
        StringBuilder command = initVarLogAgCommand();
        commandTgTestClass(command, tgQualifyFileName);
        return command.toString();
    }

    /**
     * and then run the target program with the entry of Specific test method of a test class
     *
     * @return command string
     */
    public String getVarLogAgCommand(String tgQualifyFileName, String methodName) {
        StringBuilder command = initVarLogAgCommand();
        commandTfTestMethod(command, tgQualifyFileName, methodName);
        return command.toString();
    }


    private StringBuilder initMLogAgCommand() {
        StringBuilder command = new StringBuilder("java -cp .");
        addMLogAgent(command);
        addCp(command);
        addJavassistCp(command);
        return command;
    }

    private StringBuilder initVarLogAgCommand() {
        StringBuilder command = new StringBuilder("java -cp .");
        addVarLogAgent(command);
        addCp(command);
        addJavassistCp(command);
        addAfToolCp(command);
        addGsonCp(command);
        return command;
    }

    private void addCp(StringBuilder command) {
        String[] cp = tc.getClasspathEntries();
        command.append(":");
        command.append(tc.getOutputPath());
        for (int i = 0; i < cp.length; i++) {
            command.append(":");
            command.append(cp[i]);
            if (cp[i].contains("cofoja")) {
                command.insert(4, " -javaagent:" + cp[i]);
            }
        }
        addLogLib(command);
    }
    private void addJavassistCp(StringBuilder command) {
        command.append(":lib/javassist.jar");
    }
    private void addAfToolCp(StringBuilder command) {
        command.append(":build/classes/main");
    }
    private void addGsonCp(StringBuilder command) {
        command.append(":lib/gson-2.5.jar");
    }

    private void addMLogAgent(StringBuilder command) {
        StringBuilder sb = new StringBuilder(" -javaagent:lib/MLogAgent.jar=");
//        sb.append(tc.getSourcePath());
        sb.append("polyu_af");
        sb.append(" ");
        command.insert(4, sb);
    }

    private void addVarLogAgent(StringBuilder command) {
        StringBuilder sb = new StringBuilder(" -javaagent:lib/VarLogAgent.jar=");
//        sb.append(tc.getSourcePath());
        sb.append("polyu_af");
        sb.append(" ");
        command.insert(4, sb);
    }

    private void commandTgMainEntry(StringBuilder command) {
        if (tc.getProgramEntry() != null) {
            command.append(" ");
            command.append(tc.getProgramEntry());
        } else {
            logger.info("the program entry is null");
        }
        if (tc.getRunningArg() != null) {
            command.append(" ");
            command.append(tc.getRunningArg());
        }
    }

    private void commandTgTestClass(StringBuilder command, String tgQualifyFileName) {
        command.append(":");
        command.append(tc.getTestClassPath());//append the test source classpath
        command.append(":lib/junit-4.11.jar:lib/MyJunit.jar:lib/hamcrest-core-1.3.jar:");
        command.append(" polyu_af.MyJunitCore ");
        command.append(tgQualifyFileName + "Test");//append the [test class name] including the package name

    }

    private void commandTfTestMethod(StringBuilder command, String tgQualifyFileName, String methodName) {
        command.append(":");
        command.append(tc.getTestClassPath());//append the test source classpath
        command.append(":lib/junit-4.11.jar:lib/MyJunit.jar:lib/hamcrest-core-1.3.jar:");
        command.append(" polyu_af.MyJunitCore ");
        command.append(tgQualifyFileName + "Test#" + methodName);//append the [test class name] including the package name

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
        //add log4j.xml into classpath
        command.append(System.getProperty("user.dir"));
        command.append("/src/main/resources");
    }
}
