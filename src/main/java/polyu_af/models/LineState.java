package polyu_af.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liushanchen on 16/6/1.
 */
public class LineState {
    int lineNum;
    List<ExpValue> expValueList;

    public LineState(int lineNum) {
        this.lineNum = lineNum;
        this.expValueList = new ArrayList<ExpValue>();
    }

    public int getLineNum() {
        return lineNum;
    }

    public void setLineNum(int lineNum) {
        this.lineNum = lineNum;
    }

    public List<ExpValue> getExpValueList() {
        return expValueList;
    }

    public void setExpValueList(List<ExpValue> expValueList) {
        this.expValueList = expValueList;
    }
    public void addExpValueList(ExpValue expValue) {
        expValueList.add(expValue);
    }

    @Override
    public String toString() {
        return "\n\nLineState{" +
                "expValueList=" + expValueList +
                ", lineNum=" + lineNum +
                '}';
    }
}
