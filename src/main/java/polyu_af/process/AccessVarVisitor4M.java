package polyu_af.process;

import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import polyu_af.models.AccessVar4Method;
import polyu_af.models.AccessVars4Line;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liushanchen on 16/5/4.
 */
public class AccessVarVisitor4M extends AccessExpVisitor {
    private List<AccessVar4Method> accessVar4MethodList = new ArrayList<AccessVar4Method>();
    private AccessVar4Method accessVar4Method;
    private List<AccessVars4Line> accesVarsListM;


    public AccessVarVisitor4M(CompilationUnit root) {
        super(root);
    }

    @Override
    public final boolean visit(final MethodDeclaration node) {

        accessVar4Method = new AccessVar4Method(node.getName().toString());

        for (Object o : node.parameters()) {
            SingleVariableDeclaration svd = (SingleVariableDeclaration) o;
            if(svd.getType().resolveBinding().isPrimitive()){
                accessVar4Method.addParamType(svd.getType().toString());
            }else{
                accessVar4Method.addParamType(svd.getType().resolveBinding().getBinaryName());
            }


        }
        accesVarsListM = new ArrayList<AccessVars4Line>();
        return super.visit(node);
    }

    @Override
    public final void endVisit(final MethodDeclaration node) {
        super.endVisit(node);
        accessVar4Method.addLine(accesVarsListM);
        accessVar4MethodList.add(accessVar4Method);
    }

    @Override
    protected void outPutAccessibleVars(int position) {
        super.outPutAccessibleVars(position);
        accesVarsListM.add(super.accessVars4LineList.get(accessVars4LineList.size() - 1));
    }

    public List<AccessVar4Method> getAccessVar4MethodList() {
        return accessVar4MethodList;
    }
}
