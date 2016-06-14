package polyu_af.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liushanchen on 16/6/14.
 */
public class TcMethod extends FixMethod {
    List<TcLine> tcLineList;

    public TcMethod(String methodName) {
        super(methodName);
        this.tcLineList = new ArrayList<TcLine>();
    }

    public TcMethod(String methodName, List<String> paramTypes) {
        super(methodName, paramTypes);
        this.tcLineList = new ArrayList<TcLine>();

    }

    public List<TcLine> getTcLineList() {
        return tcLineList;
    }

    public void setTcLineList(List<TcLine> tcLineList) {
        this.tcLineList = tcLineList;
    }

    public void addTcLineList(TcLine tcLine) {
        this.tcLineList.add(tcLine);
    }

    @Override
    public String toString() {
        return "TcMethod{" +
                "tcLineList=" + tcLineList +
                '}';
    }
}
