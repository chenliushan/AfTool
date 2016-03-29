package polyu_af.models;

/**
 * Created by liushanchen on 4/3/16.
 */
public class FaultUnit {
    /*
    describe the fault position
    the input sample is:{“ startPosition”：“（int）”，“length”：“(int)”}
    or {"line":" 163", "column": "40","length": "24"}
    or {"line":" 47", "length": "100"}
    or {"line":" 47"}
    or {"startPosition":" 25"}
    the last two input will get the whole line
     */
    private int line = -1;
    private int column = 0;
    private int startPosition = -1;
    /*
   if cannot get the precise length, just use a big integer,
   the nodeFinder.getCoveredNode can also find the node.
    */
    private int length = 500;//表示选中的范围,实际上是将选中范围放得很大,然后利用getCoveredNode返回第一个选中的节点,也就取得选中范围内的第一行.


    /*
    the expression
     */
    private String expression;
    private boolean expValue;

    public FaultUnit() {
    }

    public FaultUnit(int line,  String expression,boolean expValue) {
        this.line = line;
        this.expValue = expValue;
        this.expression = expression;
    }

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

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    public boolean isExpValue() {
        return expValue;
    }

    public void setExpValue(boolean expValue) {
        this.expValue = expValue;
    }

    @Override
    public String toString() {
        return "FaultUnit{" +
                "column=" + column +
                ", line=" + line +
                ", startPosition=" + startPosition +
                ", length=" + length +
                ", expression='" + expression + '\'' +
                ", expValue=" + expValue +
                '}';
    }
}
