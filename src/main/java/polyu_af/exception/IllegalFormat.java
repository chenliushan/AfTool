package polyu_af.exception;

/**
 * Created by liushanchen on 16/5/31.
 */
public class IllegalFormat extends Exception {
    public IllegalFormat(String msg) {
        super("illegal log format!:"+msg);
    }

}
