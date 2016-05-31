package polyu_af.models;

/**
 * Created by liushanchen on 16/5/31.
 */
public class ExeVarLogAgCommand extends AbsExeCommand {
    public ExeVarLogAgCommand(TargetConfig tc) {
        super(tc);
        addVarLogAgent( );
        addCp( );
        addAfToolCp( );
    }
}
