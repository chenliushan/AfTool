package polyu_af.models;

/**
 * Created by liushanchen on 16/6/13.
 */
public class Snapshot {
    int location;
    Predicate predicate;
    Boolean value;

    public Snapshot() {

    }

    public Snapshot(int location, Predicate predicate, Boolean value) {
        this.location = location;
        this.predicate = predicate;
        this.value = value;
    }

    public int getLocation() {
        return location;
    }

    public void setLocation(int location) {
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
                "l:" + location +
                "p:=" + predicate +
                "v:=" + value +
                ">\n";
    }
}
