package polyu_af.models;

import org.eclipse.jdt.core.dom.*;
import polyu_af.exception.NotAcceptExpNodeTypeException;

/**
 * Created by liushanchen on 16/5/16.
 */
public class MyExp {
    private String type = null;
    private String expVar=null;

    public MyExp(ITypeBinding type, String expVar) {
        this.type = getTypeName(type);
        this.expVar = expVar;
    }

    public String getNodeString() {
        return type+" "+expVar;
    }

    public String getExpVar() {
        return expVar;
    }


    public String getType() {
        return type;
    }

    public static String getTypeName(ITypeBinding type) {
        if(type==null){
            return null;
        }
        if (type.isPrimitive()) {
            return type.getQualifiedName();
        } else {
            return type.getBinaryName();
        }
    }

    @Override
    public String toString() {
        return "MyExp{" +
                "expVar='" + expVar + '\'' +
                ", type=" + type +
                "}\n";
    }
    public static class MePara{
        public static final String TYPE="type";
        public static final String EXP_VAR="expVar";
    }
}
