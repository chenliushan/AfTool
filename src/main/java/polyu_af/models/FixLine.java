package polyu_af.models;

/**
 * Created by liushanchen on 16/6/14.
 */
public abstract class FixLine {
    int location;

    public FixLine(int location) {
        this.location = location;
    }

    public int getLocation() {
        return location;
    }
}
