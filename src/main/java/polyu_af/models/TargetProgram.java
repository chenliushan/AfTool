package polyu_af.models;

import polyu_af.utils.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by liushanchen on 16/3/17.
 */
public class TargetProgram {

    private TargetConfig tc = null;
    private List<TargetFile> targetSources = null;
    private List<TargetFile> targetTestsClasses = null;
    private int currentT = 0;

    public TargetProgram(TargetConfig tc) {
        this.tc = tc;
        obtainSourceFiles();
        obtainTestFiles();
    }

    public void obtainSourceFiles() {
        String sourcePath = tc.getSourcePath();
        this.targetSources = obtainTargetFiles(sourcePath);
    }
    public void obtainTestFiles() {
        String sourcePath = tc.getTestClassPath();
        this.targetTestsClasses = obtainTargetFiles(sourcePath);
    }

    private List<TargetFile> obtainTargetFiles(String sourcePath) {
        ArrayList<File> files = FileUtils.getListFiles(sourcePath);
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
        return targetFiles;
    }

    public TargetFile getCurrentTarget() {
        return targetSources.get(currentT);
    }
    public TargetFile getTarget(String qualifyName) {
        for(TargetFile tf:targetSources){
            if(tf.getQualifyFileName().equals(qualifyName)){
                return tf;
            }
        }
        return null;
    }

    public TargetFile nextTarget() {
        currentT++;
        if (currentT < targetSources.size()) {
            return getCurrentTarget();
        } else {
            return null;
        }
    }

    public void moveIndex2Next() {
        currentT++;
        if (currentT >= targetSources.size()) {
            currentT = targetSources.size() - 1;
        }
    }

    public List<TargetFile> getTargetSources() {
        return targetSources;
    }

    public List<TargetFile> getTargetTestsClasses() {
        return targetTestsClasses;
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
                "currentT=" + currentT +
                ", tc=" + tc +
                ", targetSources=" + targetSources +
                '}';
    }
}
