package polyu_af.process;

import polyu_af.exception.IllegalFormat;
import polyu_af.models.*;
import polyu_af.utils.Constants;
import process.VarLogConstants;

import java.io.IOException;
import java.util.List;

/**
 * Created by liushanchen on 16/6/1.
 */
public class VarLogAnalyzer extends LogAnalyzer {
    private List<TargetFileOld> targetFileOlds;
    private MyMethod currentMethod;
    private TcMethod tcMethod;
    private TcLine tcLine = null;
    private TestCaseR testCaseR;


    public VarLogAnalyzer(List<TargetFileOld> tfs) {
        super(Constants.VarLogPath);
        this.targetFileOlds = tfs;
        this.testCaseR = new TestCaseR();
    }

    /**
     * Match the MyExpString and logged String value
     *
     * @return
     */
    public void tcLogAnalyze() {
        String line;
        int location = -1;
        String methodQName = null;
        try {
            /*遍历log的每一行,如果有lineStart则new一个tcLine,如果有lineEnd则将tcLine设为null;
            若果tcLine不为null则将log转换为MyExpString并插入tcLine。*/
            while ((line = myLog.readLine()) != null) {
                if (line.startsWith(VarLogConstants.lineStart)) {
                    line = line.substring(VarLogConstants.lineStart.length());
                    location = lineStartLocation(line);
                    methodQName = analyzeLineStart(line);
                    tcLine = new TcLine(location);

                } else if (location > 0 && line.startsWith(VarLogConstants.lineEnd)) {
                    if (analyzeLineEnd(line) == location) {
                        tcMethod.addTcLineList(tcLine);
                    } else {
                        System.err.println("something wrong!!");
                    }
                    location = -1;
                    tcLine = null;
                    methodQName = null;
                } else if (location > 0 && methodQName != null) {
                    ExpValue ev = line2ExpVal(line, methodQName, location);
                    if (ev != null && tcLine != null) {
                        tcLine.addExpValueList(ev);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (IllegalFormat illegalFormat) {
            illegalFormat.printStackTrace();
        }

    }

    public TestCaseR getTestCaseR() {
        return testCaseR;
    }

    private int lineStartLocation(String line) throws IllegalFormat {
        String[] expAndVal = line.split(":");
        if (expAndVal.length != 2) {
            throw new IllegalFormat(line);
        }
        return Integer.valueOf(expAndVal[1]);
    }

    /**
     * @param line
     * @return the method 's qualify name
     * @throws IllegalFormat
     */
    private String analyzeLineStart(String line) throws IllegalFormat {
        String[] expAndVal = line.split(":");
        if (expAndVal.length != 2) {
            throw new IllegalFormat(line);
        }
        return expAndVal[0];
    }

    private int analyzeLineEnd(String line) {
        line = line.substring(VarLogConstants.lineEnd.length());
        return Integer.valueOf(line);
    }

    private ExpValue line2ExpVal(String line, String mQName, int location) throws IllegalFormat {
        ExpValue expValue = null;
        String[] expAndVal = line.split(":");
        LineVars lineVars = searchForMyLineVars(mQName, location);
        if (lineVars != null) {
            expValue = new ExpValue((MyExpAst) SearchForMyExp(expAndVal[0], lineVars.getVarsList()));
            if (expAndVal.length != 2) {
                expValue.setValueString("");
            } else {
                expValue.setValueString(expAndVal[1]);
            }
        }
        return expValue;
    }

    /**
     * find the LineVars from the this.targetFileOlds
     * 找到 targetLine
     *
     * @param mQName
     * @return
     */
    private LineVars searchForMyLineVars(String mQName, int location) {
        List<LineVars> lavList = null;
        if (currentMethod != null && mQName.contains(currentMethod.getLongName())) {
            lavList = currentMethod.getLineVarsList();
        } else {
            /*搜索targetFiles找到targetFile,然后找到targetMethod*/
            for (TargetFileOld tf : targetFileOlds) {
                if (mQName.contains(tf.getQualifyFileName() + "#")) {
                    for (FixMethod mm : tf.getMyMethodWithAccessVars()) {
                        if(mm instanceof MyMethod){
                            MyMethod mm1=(MyMethod)mm;
                            if (mQName.contains(mm.getLongName())) {
                                currentMethod = mm1;
                                tcMethod = new TcMethod(mm.getMethodName(), mm.getParams());
                                testCaseR.addTcMethodList(tcMethod);
                                lavList = mm1.getLineVarsList();
                                break;
                            }
                        }
                    }
                }
            }
        }
        /*然后找到targetLine*/
        if (lavList != null) {
            for (LineVars lav : lavList) {
                if (lav.getLocation() == location) {
                    return lav;
                }
            }
        }
        return null;
    }

    /**
     * 找到targetExpString
     *
     * @param expName
     * @param meList
     * @return
     */
    private MyExp SearchForMyExp(String expName, List<MyExp> meList) {
        MyExp me = null;
        if (meList != null) {
            for (MyExp myExp : meList) {
                if (myExp.getExpVar().equals(expName)) {
                    me = myExp;
                    break;
                }
            }
        }
        if(me==null){
            System.err.println("not find "+expName+" in table");
        }
        return me;
    }
}
