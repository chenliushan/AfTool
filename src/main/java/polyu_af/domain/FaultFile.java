package polyu_af.domain;

import java.util.List;

/**
 * Created by liushanchen on 16/3/17.
 */

/*
the faulted file name and the faults information
 */
public class FaultFile {

    private  String sourceName=null;//specify the package and the class name
    private List<FaultUnit> faults=null;

    public List<FaultUnit> getFaults() {
        return faults;
    }

    public void setFaults(List<FaultUnit> faults) {
        this.faults = faults;
    }

    public String getSourceName() {
        return sourceName;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    @Override
    public String toString() {
        return "FaultFile{" +
                "faults=" + faults +
                ", sourceName='" + sourceName + '\'' +
                '}';
    }
}
