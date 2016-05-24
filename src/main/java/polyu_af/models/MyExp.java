package polyu_af.models;

import org.eclipse.jdt.core.dom.*;
import polyu_af.exception.NotAcceptExpNodeTypeException;

/**
 * Created by liushanchen on 16/5/16.
 */
public class MyExp {
    private ITypeBinding type = null;
    private String expVar=null;

    public MyExp(ITypeBinding type, String expVar) {
        this.type = type;
        this.expVar = expVar;
    }

    public String getNodeString() {
        return getTypeName()+" "+expVar;
    }

    public String getExpVar() {
        return expVar;
    }


    public ITypeBinding getType() {
        return type;
    }
    public boolean isPrimitive() {
        return type.isPrimitive();
    }

    public String getTypeName() {
        ITypeBinding type = getType();
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
                ", type=" + getTypeName() +
                "}\n";
    }
}
