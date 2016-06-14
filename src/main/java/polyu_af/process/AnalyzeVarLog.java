package polyu_af.process;

import polyu_af.exception.IllegalFormat;
import polyu_af.models.*;
import polyu_af.utils.Constants;
import process.VarLogConstants;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by liushanchen on 16/6/1.
 */
public class AnalyzeVarLog extends AnalyzeLog {
    List<TargetFile> targetFiles;
    MyMethod currentMethod;
    LineVars lv = null;


    public AnalyzeVarLog(List<TargetFile> tfs) {
        super(Constants.VarLogPath);
        this.targetFiles = tfs;
    }

    /**
     * Match the MyExpString and logged String value
     *
     * @return
     */
    public List<LineVars> analyze() {
        String line;
        String methodQName = null;
        List<LineVars> LineVarList = new ArrayList<LineVars>();
        LineVars lineVar = null;
        try {
            /*遍历log的每一行,如果有lineStart则new一个LineVar,如果有lineEnd则将lineVar设为null;
            若果lineVar不为null则将log转换为MyExpString并插入lineVar。*/
            while ((line = myLog.readLine()) != null) {
                if (line.startsWith(VarLogConstants.lineStart)) {
                    line = line.substring(VarLogConstants.lineStart.length());
                    lineVar = new LineVars(lineStartLocation(line));
                    methodQName = analyzeLineStart(line);
                } else if (lineVar != null && line.startsWith(VarLogConstants.lineEnd)) {
                    if (analyzeLineEnd(line) == lineVar.getLocation()) {
                        LineVarList.add(lineVar);
                        currentMethod.addLine(lineVar);
                    } else {
                        System.err.println("something wrong!!");
                    }
                    lineVar = null;
                    methodQName = null;
                } else if (lineVar != null && methodQName != null) {
                    ExpValue ev = line2ExpVal(line, methodQName, lineVar.getLocation());
                    if (ev != null) {
                        lineVar.addExpValueList(ev);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (IllegalFormat illegalFormat) {
            illegalFormat.printStackTrace();
        }
        return LineVarList;
    }

    /**
     * Match the MyExpString and logged String value
     *
     * @return
     */
    public void logAnalyze() {
        String line;
        int location = -1;
        String methodQName = null;
        try {
            /*遍历log的每一行,如果有lineStart则new一个LineVar,如果有lineEnd则将lineVar设为null;
            若果lineVar不为null则将log转换为MyExpString并插入lineVar。*/
            while ((line = myLog.readLine()) != null) {
                if (line.startsWith(VarLogConstants.lineStart)) {
                    line = line.substring(VarLogConstants.lineStart.length());
                    location = lineStartLocation(line);
                    methodQName = analyzeLineStart(line);
                } else if (location > 0 && line.startsWith(VarLogConstants.lineEnd)) {
                    if (analyzeLineEnd(line) == location) {
                        location = -1;
                    } else {
                        System.err.println("something wrong!!");
                    }
                    methodQName = null;
                } else if (location > 0 && methodQName != null) {
                    ExpValue ev = line2ExpVal(line, methodQName, location);
                    if (ev != null && lv != null) {
                        lv.addExpValueList(ev);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (IllegalFormat illegalFormat) {
            illegalFormat.printStackTrace();
        }

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
        searchForMyExp(mQName, location);
        expValue = new ExpValue((MyExpAst) SearchForMyExp(expAndVal[0], lv.getVarsList()));
        if (expAndVal.length != 2) {
            expValue.setValueString("");
        } else {
            expValue.setValueString(expAndVal[1]);
        }
        return expValue;
    }

    /**
     * find the LineVars from the this.targetFiles
     * 找到targetLine
     *
     * @param mQName
     * @return
     */
    private void searchForMyExp(String mQName, int location) {
        List<LineVars> lavList = null;
        if (currentMethod != null && mQName.contains(currentMethod.getLongName())) {
            lavList = currentMethod.getLineVarsList();
        } else {
            /*搜索targetFiles找到targetFile,然后找到targetMethod*/
            for (TargetFile tf : targetFiles) {
                if (mQName.contains(tf.getQualifyFileName() + "#")) {
                    for (MyMethod mm : tf.getMyMethodAccessVars()) {
                        if (mQName.contains(mm.getLongName())) {
                            currentMethod = mm;
                            lavList = mm.getLineVarsList();
                            break;
                        }
                    }
                }
            }
        }
        /*然后找到targetLine*/
        if (lavList != null) {
            for (LineVars lav : lavList) {
                if (lav.getLocation() == location) {
                    lv = lav;
                    break;
                }
            }
        }
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
