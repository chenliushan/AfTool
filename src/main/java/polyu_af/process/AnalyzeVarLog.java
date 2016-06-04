package polyu_af.process;

import polyu_af.Constants;
import polyu_af.exception.IllegalFormat;
import polyu_af.models.*;
import process.VarLogConstants;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by liushanchen on 16/6/1.
 */
public class AnalyzeVarLog extends AnalyzeLog {
    List<TargetFile> targetFiles;

    public AnalyzeVarLog(List<TargetFile> tfs) {
        super(Constants.VarLogPath);
        this.targetFiles = tfs;
    }

    /**
     * Match the MyExp and logged String value
     * @return
     */
    public List<LineState> analyze() {
        String line;
        String methodQName = null;
        List<LineState> lineStateList = new ArrayList<LineState>();
        LineState lineState = null;
        try {
            while ((line = myLog.readLine()) != null) {
                if (line.startsWith(VarLogConstants.lineStart)) {
                    line = line.substring(VarLogConstants.lineStart.length());
                    lineState = new LineState(lineStartLocation(line));
                    methodQName = analyzeLineStart(line);
                } else if (lineState != null && line.startsWith(VarLogConstants.lineEnd)) {
                    if (analyzeLineEnd(line) == lineState.getLineNum()) {
                        lineStateList.add(lineState);
                    } else {
                        System.err.println("something wrong!!");
                    }
                    lineState = null;
                    methodQName = null;
                } else if (lineState != null && methodQName != null) {
                    ExpValue ev = line2ExpVal(line, methodQName, lineState.getLineNum());
                    if (ev != null) {
                        lineState.addExpValueList(ev);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (IllegalFormat illegalFormat) {
            illegalFormat.printStackTrace();
        }
        return lineStateList;
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

        expValue = new ExpValue(searchForMyExp(expAndVal[0], mQName, location));
        if (expAndVal.length != 2) {
            expValue.setValueString("");
        } else {
            expValue.setValueString(expAndVal[1]);
        }
        return expValue;
    }

    /**
     * find the MyExp from the this.targetFiles
     *
     * @param expName
     * @param mQName
     * @return
     */
    private MyExp searchForMyExp(String expName, String mQName, int location) {
        MyExp me = null;
        List<LineAccessVars> lavList = null;
        for (TargetFile tf : targetFiles) {
            if (mQName.contains(tf.getQualifyFileName())) {
                for (MyMethod mm : tf.getMyMethodAccessVars()) {
                    if (mQName.contains(mm.getLongName())) {
                        lavList = mm.getVarsList();
                        break;
                    }
                }
            }
        }
        if (lavList != null) {
            List<MyExp> meList = null;
            for (LineAccessVars lav : lavList) {
                if (lav.getLocation() == location) {
                    meList = lav.getVarsList();
                    break;
                }
            }
            if (meList != null) {
                for (MyExp myExp : meList) {
                    if (myExp.getExpVar().equals(expName)) {
                        me = myExp;
                        break;
                    }
                }
            }
        }

        return me;
    }
}
