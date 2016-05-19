package polyu_af.process;

import polyu_af.models.TargetProgram;

/**
 * Created by liushanchen on 16/5/19.
 */
public abstract class ExeTarget {
    protected TargetProgram tp = null;
    protected String target = null;
    protected String targetFile = null;
    protected String targetClass = null;

    public ExeTarget(TargetProgram tp, String targetClass) {
        setTarget(targetClass);
        this.tp = tp;
    }

    public abstract void process();

    private void setTarget(String target) {
        if (target.indexOf(".") > 0) {
            if (target.endsWith(".java")) {
                setTarget(target.substring(0, target.length() - 5));
            } else if (target.endsWith(".class")) {
                setTarget(target.substring(0, target.length() - 6));
            }
        } else {
            this.target = target;
            this.targetFile = target + ".java";
            if (target.contains("/")) {
                target = target.replace("/", ".");
            }
            this.targetClass = target;

        }
    }
}
