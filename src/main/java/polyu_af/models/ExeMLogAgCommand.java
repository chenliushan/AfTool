package polyu_af.models;

/**
 * Created by liushanchen on 16/5/31.
 */
public class ExeMLogAgCommand extends AbsExeCommand {
    public ExeMLogAgCommand(TargetConfig tc) {
        super(tc);
        addMLogAgent( );
        addCp( );

    }
}
