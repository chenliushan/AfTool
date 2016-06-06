package polyu_af.models;

/**
 * Created by liushanchen on 16/5/16.
 */
public class MyExpString implements MyExp {
    private String type = null;
    private String expVar = null;

    public MyExpString(String type, String expVar) {
        this.type = type;
        this.expVar = expVar;
    }

    public String getNodeString() {
        return type + " " + expVar;
    }

    public String getExpVar() {
        return expVar;
    }


    public String getType() {
        return type;
    }


    @Override
    public String toString() {
        return "MyExpString{" +
                "expVar='" + expVar + '\'' +
                ", type=" + type +
                "}\n";
    }

    public static class MePara {
        public static final String TYPE = "type";
        public static final String EXP_VAR = "expVar";
    }
}