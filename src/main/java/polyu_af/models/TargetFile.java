package polyu_af.models;

import polyu_af.utils.ReadFileUtils;

import java.util.List;

/**
 * Created by liushanchen on 16/5/23.
 */
public class TargetFile {
    private String absoluteDir=null;
    private String packageName = null;//specify the package and the class name
    private String fileName = null;//specify the package and the class name
    private List<FaultUnit> faults = null;



    public TargetFile(String absoluteDir,String dirSource) {
        this.absoluteDir=absoluteDir;
        dividePackageAndFile(new StringBuilder(dirSource));
    }


    public List<FaultUnit> getFaults() {
        return faults;
    }

    public void setFaults(List<FaultUnit> faults) {
        this.faults = faults;
    }

    public String getFileName() {
        return fileName;
    }

    public String getJavaFileName() {
        return fileName + ".java";
    }

    public String getClassFileName() {
        return fileName + ".class";
    }

    public String getQualifyFileName() {
        return packageName + "." + fileName;
    }

    private void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getPackageName() {
        return packageName;
    }

    public String getPackageDir() {
        String tmp = packageName;
        tmp.replaceAll(".", "/");
        return tmp + "/";
    }

    private void setPackageName(String pn) {
        if (pn.contains("/")) {
            if (pn.startsWith("/"))pn=pn.substring(1);
            pn.replaceAll("//", ".");
        }
        this.packageName = pn;
    }

    private void dividePackageAndFile(StringBuilder sourceDir) {
        int dIndex = sourceDir.lastIndexOf("/");
        String fn = sourceDir.substring(dIndex + 1);
        String pn = sourceDir.substring(0, dIndex);
        if (fn.endsWith(".java")) {
            this.fileName = fn.substring(0, fn.length() - 5);
        }
        setPackageName(pn);
    }
    public String getSource(String sourceName) {
        if (absoluteDir!=null) {
            return ReadFileUtils.getSource(absoluteDir);
        }
        return null;

    }

    public String getAbsoluteDir() {
        return absoluteDir;
    }

    @Override
    public String toString() {
        return "TargetFile{" +
                "absoluteDir:'" + absoluteDir + '\'' +
                ", packageName:'" + packageName + '\'' +
                ", fileName:'" + fileName + '\'' +
                ", faults:" + faults +
                '}';
    }
}
