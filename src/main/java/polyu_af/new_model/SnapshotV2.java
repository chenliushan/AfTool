package polyu_af.new_model;

import polyu_af.models.Predicate;

/**
 * Created by liushanchen on 16/6/13.
 */
public class SnapshotV2 {
    Predicate predicate;
    boolean value;

    public SnapshotV2() {

    }

    public SnapshotV2( Predicate predicate, Boolean value) {
        this.predicate = predicate;
        this.value = value;
    }


    public Predicate getPredicate() {
        return predicate;
    }

    public void setPredicate(Predicate predicate) {
        this.predicate = predicate;
    }

    public Boolean getValue() {
        return value;
    }

    public void setValue(Boolean value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "<" +
                " p:" + predicate +
                " v:" + value +
                ">\n";
    }
}
