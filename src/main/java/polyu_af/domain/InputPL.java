package polyu_af.domain;

/**
 * Created by liushanchen on 4/3/16.
 */
public class InputPL {
    int line=-1;
    int column=0;
    int startPosition=-1;
    int length=1000;

    /*
    if cannot get the precise length, just use a big integer,
    the nodeFinder.getCoveredNode can also find the node.
     */

    public int getColumn() {
        return column;
    }

    public void setColumn(int column) {
        this.column = column;
    }

    public int getLine() {
        return line;
    }

    public void setLine(int line) {
        this.line = line;
    }

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
                "column=" + column +
                ", line=" + line +
                ", startPosition=" + startPosition +
                ", length=" + length +
                '}';
    }
}
