package polyu_af.models;

import polyu_af.utils.FileUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by liushanchen on 16/5/23.
 */
public class TargetConfig {
    private String projectDir = null;
    private String[] rootPackage = null;
    private String[] classpathEntries = null;
    private String sourcePath = null;
    private String outputPath = null;
    private String testClassPath = null;
    private String programEntry = null;
    private String[] runningArg = null;
    private String[] encodings = new String[]{"UTF-8"};
    public String getProjectDir() {
        return projectDir;
    }
    /**
     * getCompileOptions
     *
     * @return
     */
    public List<String> getCompileOptions() {
        List<String> options = new ArrayList<String>();
        options.add("-classpath");
        StringBuilder classpath = new StringBuilder(".");
        for (int i = 0; i < classpathEntries.length; i++) {
            classpath.append(":");
            classpath.append(classpathEntries[i]);
        }
        options.add(classpath.toString());
//        options.add("-processor");
//        options.add("com.google.java.contract.core.apt.AnnotationProcessor");
        options.add("-g");
        options.add("-d");
        options.add(outputPath);
        return options;
    }

    public String[] getRootPackage() {
        return rootPackage;
    }

    public void setRootPackage(String[] rootPackage) {
        this.rootPackage = rootPackage;
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
            return FileUtils.getSource(FileUtils.joinDir(path, sourceName));
        }
        return null;

    }

    public String getProgramEntry() {
        return programEntry;
    }

    public String[] getRunningArg() {
        return runningArg;
    }


    public String getTestClassPath() {
        return testClassPath;
    }

    public void setTestClassPath(String testClassPath) {
        this.testClassPath = testClassPath;
    }

    @Override
    public String toString() {
        return "TargetConfig{" +
                "classpathEntries=" + Arrays.toString(classpathEntries) +
                ", projectDir='" + projectDir + '\'' +
                ", rootPackage='" + rootPackage + '\'' +
                ", sourcePath='" + sourcePath + '\'' +
                ", outputPath='" + outputPath + '\'' +
                ", testClassPath='" + testClassPath + '\'' +
                ", programEntry='" + programEntry + '\'' +
                ", runningArg=" + Arrays.toString(runningArg) +
                ", encodings=" + Arrays.toString(encodings) +
                '}';
    }
}
