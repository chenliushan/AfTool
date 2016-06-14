package polyu_af.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liushanchen on 16/6/14.
 */
public class TestCaseR {
    List<TcMethod> tcMethodList;

    public TestCaseR() {
        this.tcMethodList = new ArrayList<TcMethod>();
    }

    public List<TcMethod> getTcMethodList() {
        return tcMethodList;
    }

    public void setTcMethodList(List<TcMethod> tcMethodList) {
        this.tcMethodList = tcMethodList;
    }

    public void addTcMethodList(TcMethod tcMethod) {
        this.tcMethodList.add(tcMethod);
    }
}
