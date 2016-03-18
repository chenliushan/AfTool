package polyu_af.domain;

import polyu_af.utils.ReadFileUtils;

import java.util.Arrays;
import java.util.List;

/**
 * Created by liushanchen on 16/3/17.
 */
public class InputFile {

    private String projectDir = null;
    private String[] classpathEntries = null;
    private String[] sourcepathEntries = null;
    private String[] encodings = new String[]{"UTF-8"};
    private List<FaultFile> faultFileList = null;

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

    public String[] getSourcepathEntries() {
        return sourcepathEntries;
    }

    public void setSourcepathEntries(String[] sourcepathEntries) {
        this.sourcepathEntries = sourcepathEntries;
    }

    public List<FaultFile> getFaultFileList() {
        return faultFileList;
    }

    public void setFaultFileList(List<FaultFile> faultFileList) {
        this.faultFileList = faultFileList;
    }

    public char[] getSource(String sourceName) {
        if (sourcepathEntries != null && sourceName != null) {
            String path = sourcepathEntries[0];
            if (path.endsWith("/")||sourceName.startsWith("/")) {
                path += sourceName;
            } else {
                path = path + "/" + sourceName;
            }
            return ReadFileUtils.readFile(path).toCharArray();
        }
        return null;

    }

    @Override
    public String toString() {
        return "InputFile{" +
                "classpathEntries=" + Arrays.toString(classpathEntries) +
                ", projectDir='" + projectDir + '\'' +
                ", sourcepathEntries=" + Arrays.toString(sourcepathEntries) +
                ", encodings=" + Arrays.toString(encodings) +
                ", faultFileList=" + faultFileList +
                '}';
    }
}
