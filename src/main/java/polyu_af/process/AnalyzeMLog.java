package polyu_af.process;

import polyu_af.Constants;
import polyu_af.MyJunitConstants;
import polyu_af.TestUnit;
import polyu_af.exception.IllegalFormat;
import polyu_af.models.MLogAnalyResult;
import polyu_af.models.MyMethod;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by liushanchen on 16/5/31.
 */
public class AnalyzeMLog extends AnalyzeLog {
    List<MLogAnalyResult> resultList = null;
    List<String> relatedClass = null;

    public AnalyzeMLog() {
        super(Constants.MLogPath);
        resultList = new ArrayList<MLogAnalyResult>();
        relatedClass = new ArrayList<String>();
    }


    public void analyze(List<TestUnit> failures) {
        for (TestUnit failure : failures) {
            resultList.addAll(analyze(failure.getQualifyName()));
        }
    }

    public List<MLogAnalyResult> getResultList() {
        return resultList;
    }

    public List<String> getRelatedClass() {
        return relatedClass;
    }

    private List<MLogAnalyResult> analyze(String qualifyName) {
        String line;
        List<MLogAnalyResult> resultList = new ArrayList<MLogAnalyResult>();
        MLogAnalyResult result = null;

        try {
            while ((line = myLog.readLine()) != null) {
                if (line.startsWith(MyJunitConstants.TestStart) && line.contains(qualifyName)) {
                    result = new MLogAnalyResult(line2Method(line));
                } else if (line.startsWith(MyJunitConstants.TestFinish)) {
                    if (result != null && line.contains(qualifyName)) {
                        resultList.add(result);
                        return resultList;
                    }

                    result = null;
                } else if (result != null) {
                    result.addRelatedMethods(line2Method(line));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (IllegalFormat illegalFormat) {
            illegalFormat.printStackTrace();
        }
        return resultList;
    }

    private MyMethod line2Method(String line) throws IllegalFormat {
        List<String> paras = null;
        if (line.startsWith(MyJunitConstants.TestStart)) {
            line = line.substring(MyJunitConstants.TestStart.length());
        } else if (line.startsWith(MyJunitConstants.TestFinish)) {
            line = line.substring(MyJunitConstants.TestFinish.length());
        } else {
            String[] methodAndParas = line.split("\\(");
            paras = line2Paras(methodAndParas[1]);
            line = methodAndParas[0];
            if (methodAndParas.length != 2) {
                throw new IllegalFormat(line);

            }
        }
        String[] classAndMethod = line.split("#");
        if (classAndMethod.length != 2) throw new IllegalFormat(line);
        MyMethod myMethod = new MyMethod(classAndMethod[0], classAndMethod[1]);
        if(!relatedClass.contains(classAndMethod[0])){
            relatedClass.add(classAndMethod[0]);
        }
        myMethod.setParamTypes(paras);
        return myMethod;
    }

    private List<String> line2Paras(String line) {
        if (line.startsWith("(")) line = line.substring(1);
        if (line.endsWith(")")) line = line.substring(0, line.length() - 1);
        String[] ps = line.split(",");
        return Arrays.asList(ps);
    }


}
