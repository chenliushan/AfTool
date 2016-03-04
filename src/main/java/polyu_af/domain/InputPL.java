package polyu_af.domain;

/**
 * Created by liushanchen on 4/3/16.
 */
public class InputPL {
    int startPosition;
    int length=1000;

    /*
    if cannot get the precise length, just use a big integer,
    the nodeFinder.getCoveredNode can also find the node.
     */
    public int getStartPosition() {
        return startPosition;
    }

    public void setStartPosition(int startPosition) {
        this.startPosition = startPosition;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    @Override
    public String toString() {
        return "InputPL{" +
                "startPosition=" + startPosition +
                ", length=" + length +
                '}';
    }
}
