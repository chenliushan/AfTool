package polyu_af.process;

import polyu_af.models.TargetConfig;

/**
 * Created by liushanchen on 16/5/31.
 */
public class ExeMLogAgCommandBuilder extends ExeCommandBuilder {
    public ExeMLogAgCommandBuilder(TargetConfig tc) {
        super(tc);
        addMLogAgent( );
        addCp( );

    }
}
