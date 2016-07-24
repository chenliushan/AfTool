package polyu_af.new_model;

import polyu_af.models.FixLine;

/**
 * Created by liushanchen on 16/7/24.
 */
public class TargetLine extends FixLine {

    private transient TargetMethod targetMethod;
    public TargetLine(int location) {
        super(location);
    }

    public TargetMethod getTargetMethod() {
        return targetMethod;
    }

    public void setTargetMethod(TargetMethod targetMethod) {
        if(targetMethod!=null){
            this.targetMethod = targetMethod;
            if(!targetMethod.targetLines.contains(this)){
                targetMethod.addLine(this);
            }
        }

    }

    @Override
    public String toString() {
        return "TargetLine{" +
                "location="+location+
                "targetMethod=" + targetMethod.getLongName() +
                '}';
    }
}
