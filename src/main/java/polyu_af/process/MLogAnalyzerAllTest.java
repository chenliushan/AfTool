package polyu_af.process;

import polyu_af.MyJunitConstants;
import polyu_af.TestUnit;
import polyu_af.exception.IllegalFormat;
import polyu_af.models.MLogAnalyResult;

import java.io.IOException;
import java.util.List;

/**
 * Created by liushanchen on 16/5/31.
 */
public class MLogAnalyzerAllTest extends MLogAnalyzer {

    /**
     * Analyze one failure test unit
     * - find the failure test's related method
     *
     * @param failures the test units to be analyze
     * @return analyzed result
     */
    public void analyze(List<TestUnit> failures) {
        String line;
        MLogAnalyResult result = null;

        try {
            while ((line = myLog.readLine()) != null) {
                if (line.startsWith(MyJunitConstants.TestStart)) {
                    TestUnit tu = isFailure(line, failures);
                    if (tu != null) {
                        result = new MLogAnalyResult(tu);
                    } else {
                        result = new MLogAnalyResult(line2TestUnit(line));
                    }
                } else if (line.startsWith(MyJunitConstants.TestFinish)) {
                    if (result != null && line.contains(result.getTestUnit().getQualifyName())) {
                        resultList.add(result);
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
    }

    private TestUnit isFailure(String line, List<TestUnit> failures) throws IllegalFormat {
        line = removeStarter(line);
        for (TestUnit tu : failures) {
            if (line.contains(tu.getQualifyName())) {
                return tu;
            }
        }
        return null;
    }


}
