package polyu_af.process;

import polyu_af.MyJunitConstants;
import polyu_af.TestUnit;
import polyu_af.exception.IllegalFormat;
import polyu_af.models.MLogAnalyResult;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by liushanchen on 16/5/31.
 */
public class MLogAnalyzerFailure extends MLogAnalyzer {

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

}
