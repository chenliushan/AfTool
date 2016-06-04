package polyu_af.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liushanchen on 16/5/31.
 */

/**
 * A failure test (a method) and a list of related method
 */
public class MLogAnalyResult {
    MyMethod testFailure=null;
    List<MyMethod> relatedMethods=null;

    public MLogAnalyResult(MyMethod testFailure) {
        this.testFailure = testFailure;
        this.relatedMethods=new ArrayList<MyMethod>();
    }

    public MyMethod getTestFailure() {
        return testFailure;
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
                "testFailure=" + testFailure +
                "\n, relatedMethods=" + relatedMethods +
                '}';
    }
}
