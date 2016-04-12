package polyu_af.deprecated;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jdt.core.dom.*;

/**
 * Created by liushanchen on 16/4/1.
 */
public class FindBindings {

    private static Logger logger = LogManager.getLogger(FindBindings.class.getName());

    public static ASTVisitor resolveTypeVisitor = new ASTVisitor() {
        @Override
        public boolean visit(SimpleName node) {
            logger.info("getIdentifier: " + node.getIdentifier());
            logger.info("getNodeType: " + ASTNode.nodeClassForType(node.getNodeType()));
            IBinding binding = node.resolveBinding();
            if (binding != null) {
                logger.info("binding.getName: " + binding.getName());
                logger.info("binding.getKey: " + binding.getKey());
                logger.info("binding.getKind: " + binding.getKind());
                logger.info("binding.getJavaElement: " + binding.getJavaElement());

            }
            ITypeBinding typeBinding = node.resolveTypeBinding();
            if (typeBinding != null) {
                logger.info("typeBinding.getQualifiedName: " + typeBinding.getQualifiedName());
//                logger.info("typeBinding.getTypeDeclaration: " + typeBinding.getTypeDeclaration());
                logger.info("typeBinding.getDeclaredFields: " );
                traversalITypeBinding(typeBinding.getDeclaredFields());
                logger.info("typeBinding.getDeclaredTypes: " );//该obj所有的type
                traversalITypeBinding( typeBinding.getDeclaredTypes());
                logger.info("typeBinding.getDeclaringClass: " + typeBinding.getDeclaringClass());
                logger.info("typeBinding.getDeclaredMethods: " );//获取该obj能够调用的方法集合
                traversalITypeBinding(typeBinding.getDeclaredMethods());
                logger.info("typeBinding.getDeclaringMethod: " + typeBinding.getDeclaringMethod());
                logger.info("typeBinding.getTypeAnnotations: ");
                traversalITypeBinding( typeBinding.getTypeAnnotations());
                logger.info("typeBinding.getComponentType: " + typeBinding.getComponentType());
                logger.info("typeBinding.isGenericType: " + typeBinding.isGenericType());
                logger.info("typeBinding.getTypeParameters: " );
                traversalITypeBinding(typeBinding.getTypeParameters());

            }
            logger.info("==========================");
            return super.visit(node);
        }
//
//        @Override
//        public boolean visit(TypeDeclaration node) {
//            super.endVisit(node);
//            IBinding binding = node.resolveBinding();
//            if (binding != null) {
//                logger.info("binding.getName: " + binding.getName());
//                logger.info("binding.getKey: " + binding.getKey());
//                logger.info("binding.getKind: " + binding.getKind());
//                logger.info("binding.getJavaElement: " + binding.getJavaElement());
//                logger.info("binding.toString: " + binding.toString());
//            }
//            return super.visit(node);
//        }
//
//
//        @Override
//        public boolean visit(SingleMemberAnnotation node) {
//            IAnnotationBinding annotationBinding = node.resolveAnnotationBinding();
//            if (annotationBinding != null) {
//                logger.info("annotationBinding: " + annotationBinding);
//                logger.info("getAllMemberValuePairs: ");
//                IMemberValuePairBinding[] allValue = annotationBinding.getAllMemberValuePairs();
//                for (IMemberValuePairBinding item : allValue) {
//                    Object[] os = (Object[]) item.getValue();
//                    for (Object o : os) {
//                        logger.info("item: " + item.getName() + "; value: " + o);
//                    }
//                }
//                logger.info("++++++++++++++++++++++++++");
//            }
//            return super.visit(node);
//        }
    };

    private static void traversalITypeBinding(ITypeBinding[] binding) {
        if (binding.length > 0) {
            for (int i = 0; i < binding.length; i++) {
                logger.info("getName:" + binding[i].getName());
                logger.info("getComponentType:" + binding[i].getComponentType());
                logger.info("getDeclaringMethod:" + binding[i].getDeclaringMethod());
                logger.info("getQualifiedName:" + binding[i].getQualifiedName());
                logger.info("getRank:" + binding[i].getRank());
            }

        } else {
            logger.info("binding list is empty!");
        }
        logger.info("-----------------------");
    }

    private static void traversalITypeBinding(IVariableBinding[] binding) {
        if (binding.length > 0) {
            for (int i = 0; i < binding.length; i++) {
                logger.info("getName:" + binding[i].getName());
                logger.info("getComponentType:" + binding[i].toString());
                logger.info("getDeclaringMethod:" + binding[i].getDeclaringMethod());
                logger.info("getConstantValue:" + binding[i].getConstantValue());
                logger.info("getVariableId:" + binding[i].getVariableId());
            }

        } else {
            logger.info("binding list is empty!");
        }
        logger.info("-----------------------");
    }
    private static void traversalITypeBinding(IMethodBinding[] binding) {
        if (binding.length > 0) {
            for (int i = 0; i < binding.length; i++) {
                logger.info("getName: " + binding[i].getName());
                logger.info("getComponentType: " + binding[i].toString());
            }

        } else {
            logger.info("binding list is empty!");
        }
        logger.info("-----------------------");
    }
    private static void traversalITypeBinding(IAnnotationBinding[] binding) {
        if (binding.length > 0) {
            for (int i = 0; i < binding.length; i++) {
                logger.info("getName:" + binding[i].getName());
                logger.info("getComponentType:" + binding[i].toString());

            }

        } else {
            logger.info("binding list is empty!");
        }
        logger.info("-----------------------");
    }
}
