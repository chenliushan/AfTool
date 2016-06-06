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
public class AnalyzeMLogFailure extends AnalyzeLog {
    List<MLogAnalyResult> resultList = null;
    List<String> relatedClass = null;

    public AnalyzeMLogFailure() {
        super(Constants.MLogPath);
        resultList = new ArrayList<MLogAnalyResult>();
        relatedClass = new ArrayList<String>();
    }


    public void analyze(List<TestUnit> failures) {
        for (TestUnit failure : failures) {
            resultList.addAll(analyze(failure));
        }
    }

    /**
     * Analyze one failure test unit
     * - find the failure test's related method
     * @param failure the test unit to be analyze
     * @return analyzed result
     */
    private List<MLogAnalyResult> analyze(TestUnit failure) {
        String qualifyName=failure.getQualifyName();
        String line;
        List<MLogAnalyResult> resultList = new ArrayList<MLogAnalyResult>();
        MLogAnalyResult result = null;

        try {
            while ((line = myLog.readLine()) != null) {
                if (line.startsWith(MyJunitConstants.TestStart) && line.contains(qualifyName)) {
                    result = new MLogAnalyResult(failure);
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

    public List<MLogAnalyResult> getResultList() {
        return resultList;
    }

    public List<String> getRelatedClass() {
        return relatedClass;
    }

    /**
     * 解析一行log
     * @param line
     * @return TestUnit 一个测试单元
     * @throws IllegalFormat
     */
    private TestUnit line2TestUnit(String line) throws IllegalFormat {
        if (line.startsWith(MyJunitConstants.TestStart)) {
            line = line.substring(MyJunitConstants.TestStart.length());
        } else if (line.startsWith(MyJunitConstants.TestFinish)) {
            line = line.substring(MyJunitConstants.TestFinish.length());
        } else {
            throw new IllegalFormat(line);
        }
        String[] classAndMethod = line.split("#");
        if (classAndMethod.length != 2) throw new IllegalFormat(line);
        TestUnit testUnit = new TestUnit(classAndMethod[0], classAndMethod[1]);
        addToRelatedClass(classAndMethod[0]);
        return testUnit;
    }
    /**
     * 解析一行log
     * @param line
     * @return TestUnit 一个测试单元
     * @throws IllegalFormat
     */
    private MyMethod line2Method(String line) throws IllegalFormat {
        List<String> paras = null;
        if (line.startsWith(MyJunitConstants.TestStart)||line.startsWith(MyJunitConstants.TestFinish)) {
            throw new IllegalFormat(line);
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
        addToRelatedClass(classAndMethod[0]);
        myMethod.setParamTypes(paras);
        return myMethod;
    }


    private void addToRelatedClass(String classQName){
        if (!relatedClass.contains(classQName)) {
            relatedClass.add(classQName);
        }
    }
    private List<String> line2Paras(String line) {
        if (line.startsWith("(")) line = line.substring(1);
        if (line.endsWith(")")) line = line.substring(0, line.length() - 1);
        String[] ps = line.split(",");
        return Arrays.asList(ps);
    }


}
