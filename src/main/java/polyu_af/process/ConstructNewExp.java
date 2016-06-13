package polyu_af.process;

import polyu_af.models.ExpValue;
import polyu_af.models.LineState;
import polyu_af.models.MyExp;
import polyu_af.models.MyExpString;

import java.util.List;

/**
 * Created by liushanchen on 16/6/10.
 */
public class ConstructNewExp {
    private LineState lineState;

    public ConstructNewExp(LineState lineState) {
        this.lineState = lineState;
    }

    public List<MyExpString> process(){

       List<ExpValue> esList= lineState.getExpValueList();

        return null;
    }
    public List<MyExpString> typeInt(MyExpString mes1,MyExpString mes2){
        return null;
    }
    public List<MyExpString> typeBoolean(MyExpString mes1,MyExpString mes2){
        return null;
    }
    public List<MyExpString> typeString(MyExpString mes1,MyExpString mes2){
        return null;
    }

}
