package polyu_af.deprecated;

import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IType;

/**
 * Created by zjzhang27 on 4/3/16.
 */
public class TestNodeFinder {
//    private boolean handleConstantValue(IField field, boolean link) throws JavaModelException {
//        String text = null;
//
//        ISourceRange nameRange = field.getNameRange();
//        if (SourceRange.isAvailable(nameRange)) {
//            CompilationUnit cuNode = ASTProvider.createAST(field.getTypeRoot(), null);
//            if (cuNode != null) {
//                ASTNode nameNode = NodeFinder.perform(cuNode, nameRange);
//                if (nameNode instanceof SimpleName) {
//                    IBinding binding = ((SimpleName)nameNode).resolveBinding();
//                    if (binding instanceof IVariableBinding) {
//                        IVariableBinding variableBinding = (IVariableBinding)binding;
//                        Object constantValue = variableBinding.getConstantValue();
//                        if (constantValue != null) {
//                            if (constantValue instanceof String) {
//                                text = ASTNodes.getEscapedStringLiteral((String)constantValue);
//                            } else {
//                                text = constantValue.toString(); // Javadoc tool is even worse for chars...
//                            }
//                        }
//                    }
//                }
//            }
//        }
//
//        if (text == null) {
//            Object constant = field.getConstant();
//            if (constant != null) {
//                text = constant.toString();
//            }
//        }
//
//
//        return false;
//    }


private static IField getFieldToPostProcess(String par, IType type) {
    String[] candidates = par.trim().split(" ");
    for (String candidate : candidates) {
        IField field = type.getField(candidate);
        if (field != null && field.exists()) {
            return field;
        }
    }
    return null;
}

}

