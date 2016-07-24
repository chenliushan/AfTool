package polyu_af.process;

import polyu_af.exception.IllegalFormat;
import polyu_af.models.*;
import polyu_af.new_model.TargetFile;
import polyu_af.new_model.TargetLine;
import polyu_af.utils.Constants;
import process.VarLogConstants;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

/**
 * Created by liushanchen on 16/6/1.
 */
public class VarLogAnalyzerV2 extends LogAnalyzer {
    private boolean isAnalyzed=false;
    Hashtable<TargetLine, List<MyExp>> lineVarsTable;
    Hashtable<TargetLine, List<ExpValue>> lineVarsValTable;


    public VarLogAnalyzerV2(Hashtable<TargetLine, List<MyExp>> lineVarsTable,
                            Hashtable<TargetLine, List<ExpValue>> lineVarsValTable) {
        super(Constants.VarLogPath);
        this.lineVarsValTable=lineVarsValTable;
        this.lineVarsTable = lineVarsTable;
    }

    public Hashtable<TargetLine, List<ExpValue>> getLineVarsValTable() {
        if(!isAnalyzed){
            tcLogAnalyze();
        }
        return lineVarsValTable;
    }

    /**
     * Match the MyExpString and logged String value
     *
     * @return
     */
    public void tcLogAnalyze() {
        isAnalyzed=true;
        String line;
        TargetLine targetLine = null;
        List<ExpValue> lineVarsVal = null;

        try {
            /*遍历log的每一行,如果有lineStart则new一个tcLine,如果有lineEnd则将tcLine设为null;
            若果tcLine不为null则将log转换为MyExpString并插入tcLine。*/
            while ((line = myLog.readLine()) != null) {
                if (line.startsWith(VarLogConstants.lineStart)) {
                    line = line.substring(VarLogConstants.lineStart.length());
                    targetLine = lineStart2TargetLine(line);
                    lineVarsVal = new ArrayList<>();
                } else if (targetLine != null && line.startsWith(VarLogConstants.lineEnd)) {
                    if (analyzeLineEnd(line) == targetLine.getLocation()) {
                        lineVarsValTable.put(targetLine, lineVarsVal);
                    } else {
                        System.err.println("something wrong!!");
                    }
                    targetLine = null;
                    lineVarsVal = null;
                } else if (lineVarsVal != null && targetLine != null) {
                    ExpValue ev = line2ExpVal(line, targetLine);
                    if (ev != null) {
                        lineVarsVal.add(ev);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (IllegalFormat illegalFormat) {
            illegalFormat.printStackTrace();
        }

    }


    private TargetLine lineStart2TargetLine(String line) throws IllegalFormat {
        String[] expAndVal = line.split(":");
        if (expAndVal.length != 2) {
            throw new IllegalFormat(line);
        }
        int location = -1;
        location = Integer.valueOf(expAndVal[1]);

        if (location > 0 && expAndVal[0] != null) {
            for (TargetLine tl : lineVarsTable.keySet()) {
                if (tl.getLocation() == location && expAndVal[0].contains(tl.getTargetMethod().getLongName())) {
                    return tl;
                }
            }
        }
        return null;
    }


    private int analyzeLineEnd(String line) {
        line = line.substring(VarLogConstants.lineEnd.length());
        return Integer.valueOf(line);
    }

    private ExpValue line2ExpVal(String line, TargetLine tl) throws IllegalFormat {
        ExpValue expValue = null;
        String[] expAndVal = line.split(":");
        List<MyExp> lineVars = searchForLineVars(tl);
        if (lineVars != null) {
            MyExpAst expAst=  (MyExpAst) SearchForMyExp(expAndVal[0], lineVars);
            if(expAst!=null){
                expValue = new ExpValue(expAst);
                if (expAndVal.length != 2) {
                    expValue.setValueString("");
                } else {
                    expValue.setValueString(expAndVal[1]);
                }
            }

        }
        return expValue;
    }

    private List<MyExp> searchForLineVars(TargetLine tl) {
        return lineVarsTable.get(tl);
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
        return me;
    }
}
