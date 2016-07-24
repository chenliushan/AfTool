package polyu_af.visitors;

import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import polyu_af.models.MyExp;
import polyu_af.models.MyExpAst;
import polyu_af.new_model.TargetFile;
import polyu_af.new_model.TargetLine;
import polyu_af.new_model.TargetMethod;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

/**
 * Created by liushanchen on 16/5/4.
 */
public class AccessVar2TableVisitor extends LAccessExpVisitor {
    private List<TargetMethod> targetMethods = new ArrayList<TargetMethod>();
    private Hashtable<TargetLine,List<MyExp>> lineExpTable;
    private TargetMethod targetMethod;
    private TargetFile tf;


    public AccessVar2TableVisitor(CompilationUnit root, TargetFile tf,Hashtable<TargetLine,List<MyExp>> lineExpTable) {
        super(root);
        this.tf=tf;
        this.lineExpTable=lineExpTable;
    }

    @Override
    public final boolean visit(final MethodDeclaration node) {

        targetMethod = new TargetMethod(node.getName().toString());
        targetMethod.setTargetFile(tf);

        for (Object o : node.parameters()) {
            SingleVariableDeclaration svd = (SingleVariableDeclaration) o;
            if(svd.getType().resolveBinding().isPrimitive()){
                targetMethod.addParamType(svd.getType().toString());
            }else{
                targetMethod.addParamType(svd.getType().resolveBinding().getBinaryName());
            }
        }
        return super.visit(node);
    }

    @Override
    public final void endVisit(final MethodDeclaration node) {
        super.endVisit(node);
        targetMethods.add(targetMethod);
    }


    @Override
    protected void outPutAccessibleVars(int position) {
        super.outPutAccessibleVars(position);
        TargetLine line=new TargetLine(root.getLineNumber(position));
        line.setTargetMethod(targetMethod);
        List<MyExp> vars= new ArrayList<>();
        for(MyExpAst ast:collectVars()){
            vars.add(ast);
        }
        lineExpTable.put(line,vars);
    }

    public List<TargetMethod> getTargetMethods() {
        return targetMethods;
    }

    public Hashtable<TargetLine, List<MyExp>> getLineExpTable() {
        return lineExpTable;
    }
}
