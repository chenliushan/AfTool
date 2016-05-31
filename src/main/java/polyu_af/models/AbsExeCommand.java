package polyu_af.models;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

/**
 * Created by liushanchen on 16/5/26.
 */
public abstract class AbsExeCommand {
    private static Logger logger = LogManager.getLogger();

    private TargetConfig tc = null;
    protected StringBuilder command;

    public AbsExeCommand(TargetConfig tc) {
        this.tc = tc;
        this.command = new StringBuilder("java -cp .");
    }

    protected void addCp() {
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
        addJavassistCp();
        addLogLib();
        addGsonCp();
    }

    protected void addJavassistCp() {
        command.append(":lib/javassist.jar");
    }

    protected void addAfToolCp() {
        command.append(":build/classes/main");
    }

    protected void addGsonCp() {
        command.append(":lib/gson-2.5.jar");
    }

    /**
     * add the log4j2 configuration file in command classpath
     * this method should be called after append classpath
     */
    protected void addLogLib() {
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

    private String getRootPackages() {
        String[] rp = tc.getRootPackage();
        StringBuilder rpsb = new StringBuilder("");
        for (int i = 0; i < rp.length; i++) {
            rpsb.append(rp[i]);
            rpsb.append(",");
        }
        if (rpsb.toString().endsWith(",")) {
            rpsb.deleteCharAt(rpsb.length() - 1);
        }
        return rpsb.toString();
    }

    protected void addMLogAgent() {
        StringBuilder sb = new StringBuilder(" -javaagent:lib/MLogAgent.jar=");
        sb.append(getRootPackages());
        sb.append(" ");
        command.insert(4, sb);
    }

    protected void addVarLogAgent() {
        StringBuilder sb = new StringBuilder(" -javaagent:lib/VarLogAgent.jar=");
        sb.append(getRootPackages());
        sb.append(" ");
        command.insert(4, sb);
    }

    public String mainEntry() {
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
        return command.toString();
    }

    private void addMJTestCp() {
        command.append(":");
        command.append(tc.getTestClassPath());//append the test source classpath
        command.append(":lib/junit-4.11.jar:lib/MyJunit.jar:lib/hamcrest-core-1.3.jar:");
        command.append(" polyu_af.MyJunitCore ");
    }

    public String testClass(String tgQualifyFileName) {
        addMJTestCp();
        command.append(tgQualifyFileName + "Test");//append the [test class name] including the package name
        return command.toString();
    }

    public String testClass(List<TargetFile> targetTests) {
        addMJTestCp();
        StringBuilder tfNames = new StringBuilder();
        for (TargetFile tt : targetTests) {
            tfNames.append(" ");
            tfNames.append(tt.getQualifyFileName());
        }
        command.append(tfNames);
        return command.toString();
    }

    public String testMethod(String tgQualifyFileName, String methodName) {
        addMJTestCp();
        command.append(tgQualifyFileName + "Test#" + methodName);//append the [test class name] including the package name
        return command.toString();
    }


}
