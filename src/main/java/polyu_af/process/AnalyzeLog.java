package polyu_af.process;

import polyu_af.utils.FileUtils;

import java.io.BufferedReader;
import java.io.File;

/**
 * Created by liushanchen on 16/5/31.
 */
public class AnalyzeLog {
    protected BufferedReader myLog;

    public AnalyzeLog(String logPath) {
        this.myLog = FileUtils.getSourceBuffer(new File(logPath));
    }
}
