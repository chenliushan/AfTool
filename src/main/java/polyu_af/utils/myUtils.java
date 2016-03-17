package polyu_af.utils;

import com.google.gson.Gson;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import polyu_af.domain.FaultFile;
import polyu_af.domain.FaultUnit;
import polyu_af.domain.InputFile;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by liushanchen on 16/3/17.
 */
public class MyUtils {
    private static Logger logger = LogManager.getLogger(MyUtils.class.getName());
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
    public static InputFile getInput(String inputPath) {
        String input = MyUtils.readFile(inputPath);
        if (input.trim() != "" && input != null) {
            logger.info("input:" + input);
            Gson gson = new Gson();
            InputFile inputFile=gson.fromJson(input, InputFile.class);
            logger.info("InputFile:" + inputFile.toString());
            return inputFile;
        }
        return null;
    }
    public static void printSampleInput(){
        String[] classpathEntries = new String[]{"/Users/liushanchen/IdeaProjects/AfTest/build/classes/main",
                "/Users/liushanchen/IdeaProjects/AfTest/lib/cofoja.asm-1.2-20140817.jar"};
        String[] sourcepathEntries = new String[]{"/Users/liushanchen/IdeaProjects/AfTest/src/main/java"};
        String[] encodings = new String[]{"UTF-8"};

        FaultUnit faultUnit=new FaultUnit(68,"exp",true);
        FaultUnit faultUnit1=new FaultUnit(70,"exp1",true);
        List<FaultUnit> faultUnits=new ArrayList<FaultUnit>();
        faultUnits.add(faultUnit);
        faultUnits.add(faultUnit1);

        FaultFile faultFile=new FaultFile();
        faultFile.setSourceName("polyu_af/MyList1.java");
        faultFile.setFaults(faultUnits);
        List<FaultFile> faultFiles=new ArrayList<FaultFile>();
        faultFiles.add(faultFile);

        InputFile inputFile=new InputFile();
        inputFile.setClasspathEntries(classpathEntries);
        inputFile.setSourcepathEntries(sourcepathEntries);
        inputFile.setEncodings(encodings);
        inputFile.setFaultFileList(faultFiles);

        Gson gson=new Gson();
        logger.info("inputFile:   "+gson.toJson(inputFile));
    }
}
