package polyu_af.process;

import polyu_af.models.TargetConfig;
import polyu_af.models.TargetFile;

/**
 * Created by liushanchen on 16/5/19.
 */
public abstract class ExeTarget {
    protected TargetConfig tc = null;


    public ExeTarget(TargetConfig targetConfig) {
        this.tc = targetConfig;
    }

    public abstract void process(TargetFile tf);


}
