package polyu_af.new_model;

import polyu_af.models.Predicate;

/**
 * Created by liushanchen on 16/6/13.
 */
public class SnapshotV3 {
    TargetLine location;
    Predicate predicate;
    boolean value;

    public SnapshotV3() {

    }

    public SnapshotV3(TargetLine location, Predicate predicate, Boolean value) {
        this.location = location;
        this.predicate = predicate;
        this.value = value;
    }

    public TargetLine getLocation() {
        return location;
    }

    public void setLocation(TargetLine location) {
        this.location = location;
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
                " l:" + location.getLocation() +
                " p:" + predicate.toString() +
                " v:" + value +
                ">\n";
    }

    @Override
    public boolean equals(Object obj) {
        if (super.equals(obj)) {
            return true;
        } else {
            if (obj != null && obj instanceof SnapshotV3) {
                SnapshotV3 ssV3 = (SnapshotV3) obj;
                try {
                    if (ssV3.getLocation().equals(this.location)
                            && ssV3.getPredicate().equals(this.predicate)
                            && ssV3.getValue().equals(this.value)) {
                        return true;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }
}
