package polyu_af.models;

import polyu_af.exception.NotFoundException;
import polyu_af.utils.ReadFileUtils;

import java.io.File;
import java.util.ArrayList;
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
    private String programEntry = null;
    private String[] runningArg = null;
    private String[] encodings = new String[]{"UTF-8"};
    private List<TargetClass> targetClassList = null;
    private List<TargetFile> targetFiles = null;
    private int currentT = 0;

    public void obtainProgramFiles()  {
        ArrayList<File> files = ReadFileUtils.getListFiles(sourcePath);
        List<TargetFile> targetFiles = new ArrayList<TargetFile>();

        for (File f : files) {
            String ap = f.getAbsolutePath();
            String s = null;
            if (ap.equals(sourcePath)) {
                continue;
            } else if (ap.startsWith(sourcePath)) {
                s = ap.substring(sourcePath.length());
            } else if (ap.contains(sourcePath)) {
                s = ap.substring(sourcePath.length() + ap.indexOf(sourcePath));
            }
            targetFiles.add(new TargetFile(ap, s));
        }
//        if(targetFiles==null) {
//            throw new NotFoundException(sourcePath);
//        }
        this.targetFiles = targetFiles;
    }

    public TargetFile getCurrentTarget()  {
        if(targetFiles==null)
            obtainProgramFiles();
        return targetFiles.get(currentT);
    }

    public TargetFile nextTarget() {
        currentT++;
        if(currentT<targetFiles.size()){
            return getCurrentTarget();
        }else{
            return null;
        }
    }
    public void moveIndex2Next() {
        currentT++;
        if(currentT>=targetFiles.size()){
            currentT=targetFiles.size()-1;
        }
    }

    public List<TargetFile> getTargetFiles() {
        if (targetFiles == null) {
            obtainProgramFiles();
        }
        return targetFiles;
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

    public String getProjectDir() {
        return projectDir;
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


    public List<TargetClass> getTargetClassList() {
        return targetClassList;
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
            return ReadFileUtils.getSource(ReadFileUtils.joinDir(path, sourceName));
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
                ", targetClassList=" + targetFiles +
                '}';
    }
}
