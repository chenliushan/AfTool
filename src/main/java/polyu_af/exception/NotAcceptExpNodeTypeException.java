package polyu_af.exception;

/**
 * Created by liushanchen on 16/5/5.
 */
public class NotAcceptExpNodeTypeException extends Exception {
    public NotAcceptExpNodeTypeException(String msg) {
        super("MyExpAst not accept node type:"+msg);
    }
}
