package polyu_af.models;

import polyu_af.TestUnit;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liushanchen on 16/5/31.
 */

/**
 * A testcase (a method) and a list of related method
 */
public class MLogAnalyResult {
    TestUnit testUnit =null;
    List<MyMethod> relatedMethods=null;

    public MLogAnalyResult(TestUnit testUnit) {
        this.testUnit = testUnit;
        this.relatedMethods=new ArrayList<MyMethod>();
    }

    public TestUnit getTestUnit() {
        return testUnit;
    }

    public List<MyMethod> getRelatedMethods() {
        return relatedMethods;
    }

    public void setRelatedMethods(List<MyMethod> relatedMethods) {
        this.relatedMethods = relatedMethods;
    }
    public void addRelatedMethods(MyMethod relatedMethod) {
        this.relatedMethods.add( relatedMethod);
    }

    @Override
    public String toString() {
        return "\nMLogAnalyResult{" +
                "testUnit=" + testUnit +
                "\n, relatedMethods=" + relatedMethods +
                '}';
    }
}
