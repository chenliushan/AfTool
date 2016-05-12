package polyu_af.models;

import polyu_af.utils.ReadFileUtils;

import java.util.Arrays;
import java.util.List;

/**
 * Created by liushanchen on 16/3/17.
 */
public class TargetProgram {

    private String projectDir = null;
    private String[] classpathEntries = null;
    private String sourcePath = null;
    private String outputPath = null;
    private String testClassPath = null;
    private String programEntry=null;
    private String[] runningArg=null;
    private String[] encodings = new String[]{"UTF-8"};
    private List<TargetClass> targetClassList = null;

    public String getProjectDir() {
        return projectDir;
    }

    public void setProjectDir(String projectDir) {
        this.projectDir = projectDir;
    }

    public String[] getClasspathEntries() {
        return classpathEntries;
    }

    public void setClasspathEntries(String[] classpathEntries) {
        this.classpathEntries = classpathEntries;
    }

    public String[] getEncodings() {
        return encodings;
    }

    public void setEncodings(String[] encodings) {
        this.encodings = encodings;
    }


    public List<TargetClass> getTargetClassList() {
        return targetClassList;
    }

    public void setTargetClassList(List<TargetClass> targetClassList) {
        this.targetClassList = targetClassList;
    }

    public String getOutputPath() {
        return outputPath;
    }

    public void setOutputPath(String outputPath) {
        this.outputPath = outputPath;
    }

    public String getSourcePath() {
        return sourcePath;
    }

    public void setSourcePath(String sourcePath) {
        this.sourcePath = sourcePath;
    }

    public String getSource(String sourceName) {
        if (sourcePath != null && sourceName != null) {
            String path = sourcePath;
            return ReadFileUtils.readFile(ReadFileUtils.joinDir(path,sourceName));
        }
        return null;

    }

    public String getProgramEntry() {
        return programEntry;
    }

    public void setProgramEntry(String programEntry) {
        this.programEntry = programEntry;
    }

    public String[] getRunningArg() {
        return runningArg;
    }

    public void setRunningArg(String[] runningArg) {
        this.runningArg = runningArg;
    }

    public String getTestClassPath() {
        return testClassPath;
    }

    public void setTestClassPath(String testClassPath) {
        this.testClassPath = testClassPath;
    }

    @Override
    public String toString() {
        return "TargetProgram{" +
                "classpathEntries=" + Arrays.toString(classpathEntries) +
                ", projectDir='" + projectDir + '\'' +
                ", sourcePath='" + sourcePath + '\'' +
                ", outputPath='" + outputPath + '\'' +
                ", testClassPath='" + testClassPath + '\'' +
                ", programEntry='" + programEntry + '\'' +
                ", runningArg=" + Arrays.toString(runningArg) +
                ", encodings=" + Arrays.toString(encodings) +
                ", targetClassList=" + targetClassList +
                '}';
    }
}
