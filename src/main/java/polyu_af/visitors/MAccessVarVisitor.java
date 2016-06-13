package polyu_af.visitors;

import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import polyu_af.models.LineVars;
import polyu_af.models.MyMethod;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liushanchen on 16/5/4.
 */
public class MAccessVarVisitor extends LAccessExpVisitor {
    private List<MyMethod> myMethodAccessVars = new ArrayList<MyMethod>();
    private MyMethod accessVar4Method;
    private List<LineVars> accesVarsListM;


    public MAccessVarVisitor(CompilationUnit root) {
        super(root);
    }

    @Override
    public final boolean visit(final MethodDeclaration node) {

        accessVar4Method = new MyMethod(node.getName().toString());

        for (Object o : node.parameters()) {
            SingleVariableDeclaration svd = (SingleVariableDeclaration) o;
            if(svd.getType().resolveBinding().isPrimitive()){
                accessVar4Method.addParamType(svd.getType().toString());
            }else{
                accessVar4Method.addParamType(svd.getType().resolveBinding().getBinaryName());
            }


        }
        accesVarsListM = new ArrayList<LineVars>();
        return super.visit(node);
    }

    @Override
    public final void endVisit(final MethodDeclaration node) {
        super.endVisit(node);
        accessVar4Method.addLine(accesVarsListM);
        myMethodAccessVars.add(accessVar4Method);
    }

    @Override
    protected void outPutAccessibleVars(int position) {
        super.outPutAccessibleVars(position);
        accesVarsListM.add(super.accessVars4LineList.get(accessVars4LineList.size() - 1));
    }

    public List<MyMethod> getMyMethodAccessVars() {
        return myMethodAccessVars;
    }
}
