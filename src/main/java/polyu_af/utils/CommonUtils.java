package polyu_af.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import polyu_af.models.MyExpression;

import java.util.*;

/**
 * Created by liushanchen on 16/4/11.
 */
public class CommonUtils {
    private static Logger logger = LogManager.getLogger(CommonUtils.class.getName());

    public static Map<Integer, List<MyExpression>> printMap(Map<Integer, List<MyExpression>> map) {
        Map<Integer, List<MyExpression>> treeMap = new TreeMap<Integer, List<MyExpression>>(map);
        Set s = treeMap.entrySet();
        Iterator it = s.iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            Integer key = (Integer) entry.getKey();
            List<MyExpression> value = (List<MyExpression>) entry.getValue();
            logger.info(key + " => " + value);

        }
        logger.info(" map.size= " + treeMap.size());

        return treeMap;
    }

    //    public static void printMap(Map<Object,Object> map){
//        Iterator<Map.Entry<Object,Object>> entries = map.entrySet().iterator();
//        while (entries.hasNext()) {
//            Map.Entry<Object,Object> entry = entries.next();
//            Object node=entry.getValue();
//            logger.info("Key = " + entry.getKey() + ", Value = " + node.toString() );
//
//        }
//    }

    public static void printAccessibleVariables(Map<Integer, List<MyExpression>> accessibleVariables) {
        Iterator<Map.Entry<Integer, List<MyExpression>>> entries = accessibleVariables.entrySet().iterator();
        while (entries.hasNext()) {
            Map.Entry<Integer, List<MyExpression>> entry = entries.next();
            Object node = entry.getValue();
            logger.info("Key = " + entry.getKey() + ", Value = " + node.toString());

        }
    }
    public static void printExpList(List<MyExpression> list) {
       for(MyExpression exp:list){
           logger.info("exp:"+exp.getText());
       }
    }

    public static void printPos2TypeDecl(Map<Integer, TypeDeclaration> pos2TypeDecl) {
        Iterator<Map.Entry<Integer, TypeDeclaration>> entries = pos2TypeDecl.entrySet().iterator();
        while (entries.hasNext()) {
            Map.Entry<Integer, TypeDeclaration> entry = entries.next();
            Object node = entry.getValue();
            logger.info("Key = " + entry.getKey() + ", Value = " + node.toString());

        }
    }


    public static void printPos2ArgMap(Map<Integer, Expression> pos2ArgMap) {
        Iterator<Map.Entry<Integer, Expression>> entries = pos2ArgMap.entrySet().iterator();
        while (entries.hasNext()) {
            Map.Entry<Integer, Expression> entry = entries.next();
            Object node = entry.getValue();
            logger.info("Key = " + entry.getKey() + ", Value = " + node.toString());

        }
    }
}
