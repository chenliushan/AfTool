package polyu_af.new_model;

/**
 * Created by liushanchen on 16/8/3.
 */
public class Strategy {
    TargetLine targetLine;
    String fix;

    public Strategy(TargetLine targetLine, String fix) {
        this.targetLine = targetLine;
        this.fix = fix;
    }

    public TargetLine getTargetLine() {
        return targetLine;
    }

    public void setTargetLine(TargetLine targetLine) {
        this.targetLine = targetLine;
    }

    public String getFix() {
        return fix;
    }

    public void setFix(String fix) {
        this.fix = fix;
    }

    @Override
    public String toString() {
        return "Strategy{" +
                "targetLine=" + targetLine +
                ", fix='" + fix + '\'' +
                '}';
    }
}
