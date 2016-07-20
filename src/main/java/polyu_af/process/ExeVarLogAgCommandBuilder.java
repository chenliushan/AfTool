package polyu_af.process;

import polyu_af.models.TargetConfig;

/**
 * Created by liushanchen on 16/5/31.
 */
public class ExeVarLogAgCommandBuilder extends ExeCommandBuilder {
    public ExeVarLogAgCommandBuilder(TargetConfig tc) {
        super(tc);
        addVarLogAgent( );
        addCp( );
        addAfToolCp( );
    }
}
