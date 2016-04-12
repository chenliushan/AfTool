package polyu_af.utils;

import com.google.gson.Gson;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import polyu_af.models.FaultClass;
import polyu_af.models.FaultUnit;
import polyu_af.models.TargetProgram;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by liushanchen on 16/3/17.
 */
public class ReadFileUtils {
    private static Logger logger = LogManager.getLogger(ReadFileUtils.class.getName());

    public static String readFile(String fileName) {
        String fileContent = "";
        File file = new File(fileName);
        if (file.exists()) {
            if (file.isFile()) {
                try {
                    BufferedReader input = new BufferedReader(new FileReader(file));
                    StringBuffer buffer = new StringBuffer();
                    String text;
                    while ((text = input.readLine()) != null)
                        buffer.append(text + "\n");
                    fileContent = buffer.toString();
                } catch (IOException ioException) {
                    logger.error("readFile: " + ioException.getStackTrace().toString());
                }
            }
        }
        return fileContent;
    }


    public static void printSampleInput() {
        String[] classpathEntries = new String[]{"/Users/liushanchen/IdeaProjects/AfTest/build/classes/main",
                "/Users/liushanchen/IdeaProjects/AfTest/lib/cofoja.asm-1.2-20140817.jar"};
        String[] sourcepathEntries = new String[]{"/Users/liushanchen/IdeaProjects/AfTest/src/main/java"};
        String[] encodings = new String[]{"UTF-8"};

        FaultUnit faultUnit = new FaultUnit(68, "exp", true);
        FaultUnit faultUnit1 = new FaultUnit(70, "exp1", true);
        List<FaultUnit> faultUnits = new ArrayList<FaultUnit>();
        faultUnits.add(faultUnit);
        faultUnits.add(faultUnit1);

        FaultClass faultClass = new FaultClass();
        faultClass.setSourceName("polyu_af/MyList1.java");
        faultClass.setFaults(faultUnits);
        List<FaultClass> faultClasses = new ArrayList<FaultClass>();
        faultClasses.add(faultClass);

        TargetProgram targetProgram = new TargetProgram();
        targetProgram.setClasspathEntries(classpathEntries);
        targetProgram.setSourcepathEntries(sourcepathEntries);
        targetProgram.setEncodings(encodings);
        targetProgram.setFaultClassList(faultClasses);

        Gson gson = new Gson();
        logger.info("targetProgram:   " + gson.toJson(targetProgram));
    }


    public static String joinDir(String path, String subPath) {
        if (!subPath.startsWith("/") && !path.endsWith("/")) {
            subPath = path + "/" + subPath;
        } else {
            subPath = path + subPath;
        }
        return subPath;
    }


}
