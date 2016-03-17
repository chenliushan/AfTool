package polyu_af.utils;

import com.google.gson.Gson;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import polyu_af.domain.FaultUnit;
import polyu_af.domain.InputFile;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

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
            logger.info("InputFile:" + input.toString());
        }
        return null;
    }
}
