package polyu_af.process;

import polyu_af.models.TargetProgram;

/**
 * Created by liushanchen on 16/5/19.
 */
public abstract class ExeTarget {
    protected TargetProgram tp = null;


    public ExeTarget(TargetProgram tp) {
        this.tp = tp;
    }

    public abstract void process();


}
