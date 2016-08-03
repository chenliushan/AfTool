package polyu_af.new_model;

import java.util.List;
import java.util.Set;

/**
 * Created by liushanchen on 16/7/29.
 */
public class FixAction {
    SnapshotV3 snapshotV3;
    Set<String> fixs;

    public FixAction(SnapshotV3 snapshotV3) {
        this.snapshotV3 = snapshotV3;
    }

    public SnapshotV3 getSnapshotV3() {
        return snapshotV3;
    }

    public Set<String> getFixs() {
        return fixs;
    }

    public void setFixs(Set<String> fixs) {
        this.fixs = fixs;
    }

    @Override
    public String toString() {
        return "FixAction{" +
                "snapshot=" + snapshotV3 +
                ", fixs=" + fixs +
                '}';
    }
}
