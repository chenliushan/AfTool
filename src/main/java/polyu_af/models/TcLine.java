package polyu_af.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liushanchen on 16/6/14.
 */
public class TcLine extends FixLine{
    List<ExpValue> expValueList;
    List<Snapshot> snapshotList;

    public TcLine(int location) {
        super(location);
        this.expValueList = new ArrayList<ExpValue>();
        this.snapshotList = new ArrayList<Snapshot>();
    }

    public List<ExpValue> getExpValueList() {
        return expValueList;
    }

    public void setExpValueList(List<ExpValue> expValueList) {
        this.expValueList = expValueList;
    }

    public void addExpValueList(ExpValue expValue) {
        this.expValueList.add(expValue);
    }

    public List<Snapshot> getSnapshotList() {
        return snapshotList;
    }

    public void setSnapshotList(List<Snapshot> snapshotList) {
        this.snapshotList = snapshotList;
    }

    public void addSnapshotList(Snapshot snapshot) {
        this.snapshotList.add(snapshot);
    }

    @Override
    public String toString() {
        return "TcLine{" +
                "expValueList=" + expValueList +
                ", location=" + location +
                ", snapshotList=" + snapshotList +
                '}';
    }
}
