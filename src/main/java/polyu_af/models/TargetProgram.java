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
    private String sourcepath = null;
    private String outputPath = null;
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

    public String getSourcepath() {
        return sourcepath;
    }

    public void setSourcepath(String sourcepath) {
        this.sourcepath = sourcepath;
    }

    public String getSource(String sourceName) {
        if (sourcepath != null && sourceName != null) {
            String path = sourcepath;
            return ReadFileUtils.readFile(ReadFileUtils.joinDir(path,sourceName));
        }
        return null;

    }

    @Override
    public String toString() {
        return "TargetProgram{" +
                "classpathEntries=" + Arrays.toString(classpathEntries) +
                ", projectDir='" + projectDir + '\'' +
                ", sourcepath='" + sourcepath + '\'' +
                ", outputPath='" + outputPath + '\'' +
                ", encodings=" + Arrays.toString(encodings) +
                ", targetClassList=" + targetClassList +
                '}';
    }
}
