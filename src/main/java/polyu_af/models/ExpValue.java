package polyu_af.models;

/**
 * Created by liushanchen on 16/6/1.
 */
public class ExpValue {
    MyExpAst exp;
    String valueString;
    Object value;

    public ExpValue(MyExpAst exp) {
        this.exp = exp;
    }

    public MyExpAst getExp() {
        return exp;
    }

    public Object getValue() {
        return value;
    }

    public String getValueString() {
        return valueString;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public void setValueString(String valueString) {
        this.valueString = valueString;
    }

    @Override
    public String toString() {
        return "\nExpValue{" +
                "exp=" + exp +
                ", valueString='" + valueString + '\'' +
                ", value=" + value +
                '}';
    }
}
