package polyu_af.process;

import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import polyu_af.models.AccessVar4Method;
import polyu_af.models.AccessibleVars;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liushanchen on 16/5/4.
 */
public class AccessibleVarVisitor4M extends AccessibleVarVisitor {
    private List<AccessVar4Method> accessVar4MethodList = new ArrayList<AccessVar4Method>();
    private AccessVar4Method accessVar4Method;
    private List<AccessibleVars> accesVarsListM;


    public AccessibleVarVisitor4M(CompilationUnit root) {
        super(root);
    }

    @Override
    public final boolean visit(final MethodDeclaration node) {
        accessVar4Method = new AccessVar4Method(node.getName().toString());
        accesVarsListM = new ArrayList<AccessibleVars>();
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
        accesVarsListM.add(super.accessibleVarsList.get(accessibleVarsList.size() - 1));
    }

    public List<AccessVar4Method> getAccessVar4MethodList() {
        return accessVar4MethodList;
    }
}
