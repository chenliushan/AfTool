package polyu_af.utils;

/**
 * Created by liushanchen on 16/7/29.
 */
public class CommonUtils {
    public static String appendInvoking(String expVar, String invokingName) {
        StringBuilder sb = new StringBuilder("(");
        sb.append(expVar);
        sb.append(")");
        sb.append(".");
        sb.append(invokingName);
        sb.append("()");
        return sb.toString();
    }
}
