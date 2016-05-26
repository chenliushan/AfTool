package polyu_af.visitors;

import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import polyu_af.models.LineAccessVars;
import polyu_af.models.MethodAccessVars;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liushanchen on 16/5/4.
 */
public class MAccessVarVisitor extends LAccessExpVisitor {
    private List<MethodAccessVars> methodAccessVars = new ArrayList<MethodAccessVars>();
    private MethodAccessVars accessVar4Method;
    private List<LineAccessVars> accesVarsListM;


    public MAccessVarVisitor(CompilationUnit root) {
        super(root);
    }

    @Override
    public final boolean visit(final MethodDeclaration node) {

        accessVar4Method = new MethodAccessVars(node.getName().toString());

        for (Object o : node.parameters()) {
            SingleVariableDeclaration svd = (SingleVariableDeclaration) o;
            if(svd.getType().resolveBinding().isPrimitive()){
                accessVar4Method.addParamType(svd.getType().toString());
            }else{
                accessVar4Method.addParamType(svd.getType().resolveBinding().getBinaryName());
            }


        }
        accesVarsListM = new ArrayList<LineAccessVars>();
        return super.visit(node);
    }

    @Override
    public final void endVisit(final MethodDeclaration node) {
        super.endVisit(node);
        accessVar4Method.addLine(accesVarsListM);
        methodAccessVars.add(accessVar4Method);
    }

    @Override
    protected void outPutAccessibleVars(int position) {
        super.outPutAccessibleVars(position);
        accesVarsListM.add(super.accessVars4LineList.get(accessVars4LineList.size() - 1));
    }

    public List<MethodAccessVars> getMethodAccessVars() {
        return methodAccessVars;
    }
}
