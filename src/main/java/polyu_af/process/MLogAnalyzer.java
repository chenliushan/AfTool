package polyu_af.process;

import polyu_af.utils.Constants;
import polyu_af.MyJunitConstants;
import polyu_af.TestUnit;
import polyu_af.exception.IllegalFormat;
import polyu_af.models.MLogAnalyResult;
import polyu_af.models.MyMethod;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by liushanchen on 16/6/7.
 */
public abstract class MLogAnalyzer extends LogAnalyzer {
    List<MLogAnalyResult> resultList = null;
    List<String> relatedClass = null;

    public MLogAnalyzer() {
        super(Constants.MLogPath);
        resultList = new ArrayList<MLogAnalyResult>();
        relatedClass = new ArrayList<String>();
    }

    public abstract void analyze(List<TestUnit> failures);

    /**
     * 解析一行log
     *
     * @param line
     * @return TestUnit 一个测试单元
     * @throws IllegalFormat
     */
    protected TestUnit line2TestUnit(String line) throws IllegalFormat {
        line = removeStarter(line);
        String[] classAndMethod = line.split("#");
        if (classAndMethod.length != 2) throw new IllegalFormat(line);
        TestUnit testUnit = new TestUnit(classAndMethod[0], classAndMethod[1]);
        testUnit.setPassing(true);
        addToRelatedClass(classAndMethod[0]);
        return testUnit;
    }

    /**
     * 解析一行log
     *
     * @param line
     * @return TestUnit 一个测试单元
     * @throws IllegalFormat
     */
    protected MyMethod line2Method(String line) throws IllegalFormat {
        List<String> paras = null;
        if (line.startsWith(MyJunitConstants.TestStart) || line.startsWith(MyJunitConstants.TestFinish)) {
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


    private void addToRelatedClass(String classQName) {
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

    protected String removeStarter(String line) throws IllegalFormat {
        if (line.startsWith(MyJunitConstants.TestStart)) {
            line = line.substring(MyJunitConstants.TestStart.length());
        } else if (line.startsWith(MyJunitConstants.TestFinish)) {
            line = line.substring(MyJunitConstants.TestFinish.length());
        } else {
            throw new IllegalFormat(line);
        }
        return line;
    }

    public List<MLogAnalyResult> getResultList() {
        return resultList;
    }

    public List<String> getRelatedClass() {
        return relatedClass;
    }


}
