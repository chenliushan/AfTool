package polyu_af.models;

import org.eclipse.jdt.core.dom.ASTNode;
import polyu_af.exception.NotAcceptExpNodeTypeException;

/**
 * Created by liushanchen on 16/6/12.
 */
public class MyExpInv extends MyExpAst {

    private String type = null;
    private String expVar = null;

    public MyExpInv(ASTNode astNode,String type, String expVar) throws NotAcceptExpNodeTypeException {
        super(astNode);
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
        return "MyExpInv{" +
                "expVar='" + expVar + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
