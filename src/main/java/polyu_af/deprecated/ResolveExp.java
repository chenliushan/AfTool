package polyu_af.deprecated;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jdt.core.dom.*;
import polyu_af.models.FaultUnit;
import polyu_af.utils.AstUtils;

/**
 * Created by liushanchen on 16/3/23.
 *
 * discard
 * it is replaced by the AccessibleVariables
 */
public class ResolveExp {
    private static Logger logger = LogManager.getLogger(ResolveExp.class.getName());

    private CompilationUnit root;
    private ASTNode faultNode;
    private FaultUnit faultUnit;

    public ResolveExp(ASTNode faultNode, FaultUnit faultUnit, CompilationUnit root) {
        this.faultNode = faultNode;
        this.faultUnit = faultUnit;
        this.root = root;
    }

    public void resolveExp() {
        if (!faultUnit.getExpression().equals("exp")) {
            Expression expAst = AstUtils.createExpAST(faultUnit.getExpression());
            /**
             * Since there are limit kinds of input exp, (infix expression, method invocation...)
             * we can write a switch block here to cast the expression for different kind
             * and do different operation;
             * here is one of the possible operations: when the input exp is infixExpression.
             * 1.get the leftOperand of the infixExpression
             * 2.find 'same name variable' in the fault node by visit the fault node
             * 3.resolve the 'same name variable' in the fault node, so we can get the java type
             *
             * shortcoming: the 'same name variable' may occur more than once in the fault node
             * or maybe it not occur in the fault node ( visit the root to resolve?
             * there may be more different 'same name variable' in the root)
             */
            InfixExpression in = (InfixExpression) expAst;
            final Expression leftOperand = in.getLeftOperand();
            ASTVisitor inputResolveVisitor = new ASTVisitor() {
                @Override
                public boolean visit(SimpleName node) {
                    if (node.getIdentifier().toString().equals(leftOperand.toString())){
                        logger.info("find the same name node");
                        IBinding binding = node.resolveBinding();
                        if (binding != null) {
                            logger.info("getName: " + binding.getName());
                            logger.info("getKey: " + binding.getKey());
                            logger.info("getKind: " + binding.getKind());
                        }
                    }
                        return super.visit(node);
                }
            };
            faultNode.accept(inputResolveVisitor);
            /**
             * Or write a visitor do a unify operation to all the simpleName in the input expression?
             * not sure yet...
             */
//            expAst.accept(AstUtils.seeTypeVisitor);
        }
    }
}
