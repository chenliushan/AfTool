package polyu_af.models;

import polyu_af.new_model.TargetFile;
import polyu_af.utils.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by liushanchen on 16/3/17.
 */
public class TargetProgram {

    private TargetConfig tc = null;
    private List<TargetFileOld> targetFileOlds = null;
    private List<TargetFile> targetFiles = null;
    private List<TargetFileOld> targetTestsClasses = null;

    public TargetProgram(TargetConfig tc) {
        this.tc = tc;
        obtainFiles();
        obtainSourceFiles();
        obtainTestFiles();
    }

    public void obtainSourceFiles() {
        String sourcePath = tc.getSourcePath();
        this.targetFileOlds = obtainOldTargetFiles(sourcePath);
    }
    public void obtainTestFiles() {
        String sourcePath = tc.getTestClassPath();
        this.targetTestsClasses = obtainOldTargetFiles(sourcePath);
    }

    public void obtainFiles() {
        String sourcePath = tc.getSourcePath();
        this.targetFiles = obtainTargetFiles(sourcePath);
    }

    private List<TargetFileOld> obtainOldTargetFiles(String sourcePath) {
        ArrayList<File> files = FileUtils.getListFiles(sourcePath);
        List<TargetFileOld> targetFileOlds = new ArrayList<TargetFileOld>();

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
            targetFileOlds.add(new TargetFileOld(ap, s));
        }
        return targetFileOlds;
    }
    private List<TargetFile> obtainTargetFiles(String sourcePath) {
        ArrayList<File> files = FileUtils.getListFiles(sourcePath);
        List<TargetFile> targetFileOlds = new ArrayList<TargetFile>();

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
            targetFileOlds.add(new TargetFile(ap, s));
        }
        return targetFileOlds;
    }


    public TargetFileOld getTarget(String qualifyName) {
        for(TargetFileOld tf: targetFileOlds){
            if(tf.getQualifyFileName().equals(qualifyName)){
                return tf;
            }
        }
        return null;
    }
    public TargetFile getTargetFile(String qualifyName) {
        for(TargetFile tf: targetFiles){
            if(tf.getQualifyFileName().equals(qualifyName)){
                return tf;
            }
        }
        return null;
    }

    public void setTargetFileOlds(List<TargetFileOld> targetFileOlds) {
        this.targetFileOlds = targetFileOlds;
    }

    public List<TargetFileOld> getTargetFileOlds() {
        return targetFileOlds;
    }

    public List<TargetFileOld> getTargetTestsClasses() {
        return targetTestsClasses;
    }

    public List<TargetFile> getTargetFiles() {
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
        for (int i = 0; i < tc.getClasspathEntries().length; i++) {
            classpath.append(":");
            classpath.append(tc.getClasspathEntries()[i]);
        }
        options.add(classpath.toString());
//        options.add("-processor");
//        options.add("com.google.java.contract.core.apt.AnnotationProcessor");
        options.add("-g");
        options.add("-d");
        options.add(tc.getOutputPath());
        return options;
    }

    public TargetConfig getTc() {
        return tc;
    }

    public void setTc(TargetConfig tc) {
        this.tc = tc;
    }


    @Override
    public String toString() {
        return "TargetProgram{" +
                ", tc=" + tc +
                ", targetFileOlds=" + targetFileOlds +
                '}';
    }
}
