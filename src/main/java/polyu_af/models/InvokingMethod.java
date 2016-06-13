package polyu_af.models;

import java.util.List;

/**
 * Created by liushanchen on 16/6/12.
 */
public interface InvokingMethod {
    public  List<MyExpInv> getInvokingMethod(MyExpAst myExpAst);
    public boolean isValidInvoking(String invokingName);
}
