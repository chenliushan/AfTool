package polyu_af.new_model;

import polyu_af.utils.FileUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liushanchen on 16/5/23.
 */
public class TargetFile {
    private String absoluteDir = null;
    private String packageName = null;//specify the package and the class name
    private String fileName = null;//specify the package and the class name
    private List<TargetMethod> methods = null;


    public TargetFile(String absoluteDir, String dirSource) {
        this.absoluteDir = absoluteDir;
        dividePackageAndFile(new StringBuilder(dirSource));
        methods = new ArrayList<>();
    }

    public TargetFile(String absoluteDir, String pn, String fn) {
        this.absoluteDir = absoluteDir;
        this.packageName = pn;
        this.fileName = fn;
        methods = new ArrayList<>();
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

    public String getDirAndFileName() {
        return getPackageDir() + fileName;
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
            if (pn.startsWith("/")) pn = pn.substring(1);
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
        } else if (fn.endsWith(".class")) {
            this.fileName = fn.substring(0, fn.length() - 6);
        }
        setPackageName(pn);
    }

    public String getSource() {
        if (absoluteDir != null) {
            return FileUtils.getSource(absoluteDir);
        }
        return null;

    }

    public List<TargetMethod> getMethods() {
        return methods;
    }


    public void setMethods(List<TargetMethod> methods) {
        for(TargetMethod method:methods){
            this.addMethods(method);
        }
    }

    public void addMethods(TargetMethod method) {
        if(!methods.contains(method)){
            methods.add(method);
            if (method != null && method.getTargetFile() != this) {
                method.setTargetFile(this);
            }
        }

    }

    public String getAbsoluteDir() {
        return absoluteDir;
    }

    @Override
    public String toString() {
        return "TargetFile{" +
                "absoluteDir='" + absoluteDir + '\'' +
                ", packageName='" + packageName + '\'' +
                ", fileName='" + fileName + '\'' +
                ", methods=" + methods +
                "}\n";
    }

    public static class TfPara {
        public static final String ABSOLUTE_DIR = "absoluteDir";
        public static final String PACKAGE_NAME = "packageName";
        public static final String FILE_NAME = "fileName";
        public static final String FAULTS = "faults";
        public static final String METHOD_ACCESS_VARS = "methods";
    }
}
