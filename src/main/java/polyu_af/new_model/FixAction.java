package polyu_af.new_model;

import polyu_af.models.Predicate;
import polyu_af.models.Snapshot;

import java.util.List;

/**
 * Created by liushanchen on 16/7/29.
 */
public class FixAction {
    SnapshotV3 snapshotV3;
    List<String> fixs;

    public FixAction(SnapshotV3 snapshotV3) {
        this.snapshotV3 = snapshotV3;
    }

    public SnapshotV3 getSnapshotV3() {
        return snapshotV3;
    }

    public List<String> getFixs() {
        return fixs;
    }

    public void setFixs(List<String> fixs) {
        this.fixs = fixs;
    }

    @Override
    public String toString() {
        return "FixAction{" +
                "snapshotV3=" + snapshotV3 +
                ", fixs=" + fixs +
                '}';
    }
}
