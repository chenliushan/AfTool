package polyu_af.new_model;

import polyu_af.models.FixMethod;
import polyu_af.models.TargetFileOld;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liushanchen on 16/6/14.
 */
public class TargetMethod extends FixMethod {
    List<TargetLine> targetLines;
    transient TargetFile targetFile;


    public TargetMethod(String methodName) {
        super(methodName);
        this.targetLines = new ArrayList<TargetLine>();
    }

    public TargetMethod(String methodName, List<String> paramTypes) {
        super(methodName, paramTypes);
        this.targetLines = new ArrayList<TargetLine>();

    }

    public TargetFile getTargetFile() {
        return targetFile;
    }

    public void setTargetFile(TargetFile targetFile) {
        if(targetFile!=null){
            this.targetFile = targetFile;
            if(!targetFile.getMethods().contains(this)){
                targetFile.addMethods(this);

            }
        }
    }

    public List<TargetLine> getTargetLines() {
        return targetLines;
    }

    public void setTargetLines(List<TargetLine> targetLines) {
        this.targetLines = targetLines;
    }

    public void addLine(TargetLine targetLine) {
        if(targetLine!=null){
            if (!targetLines.contains(targetLine)) {
                this.targetLines.add(targetLine);
            }
            if (targetLine.getTargetMethod() != this) {
                targetLine.setTargetMethod(this);
            }
        }

    }

    @Override
    public String toString() {
        return "TargetMethod{" +
                "name=" + getLongName() +
                ", targetFile=" + targetFile .getFileName()+
                '}';
    }
}
