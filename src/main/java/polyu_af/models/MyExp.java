package polyu_af.models;

/**
 * Created by liushanchen on 16/6/6.
 */
public interface MyExp {

    public String getExpVar();
    public String getNodeString();
    public String getType();
    public void setType(String type);
    public void setExpVar(String expVar);

    public static class MyExpAstPara{
        public static final String TYPE="type";
        public static final String EXPVAR="expVar";

    }
}
