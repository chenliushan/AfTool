package polyu_af.utils;

import com.google.gson.Gson;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import polyu_af.domain.FaultFile;
import polyu_af.domain.FaultUnit;
import polyu_af.domain.InputFile;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
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

    /*
    read the input file and create inputFile object
     */
    public static InputFile getInput(String inputPath) {
        String input = ReadFileUtils.readFile(inputPath);
        if (input.trim() != "" && input != null) {
            logger.info("input:" + input);
            Gson gson = new Gson();
            InputFile inputFile = gson.fromJson(input, InputFile.class);
            logger.info("InputFile:" + inputFile.toString());
            if(inputFile.getProjectDir()!=null){
                inputFile=readClasspathE(inputFile,inputFile.getProjectDir());
            }
            logger.info("InputFile:" + inputFile.toString());
            return inputFile;
        }
        return null;
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

        FaultFile faultFile = new FaultFile();
        faultFile.setSourceName("polyu_af/MyList1.java");
        faultFile.setFaults(faultUnits);
        List<FaultFile> faultFiles = new ArrayList<FaultFile>();
        faultFiles.add(faultFile);

        InputFile inputFile = new InputFile();
        inputFile.setClasspathEntries(classpathEntries);
        inputFile.setSourcepathEntries(sourcepathEntries);
        inputFile.setEncodings(encodings);
        inputFile.setFaultFileList(faultFiles);

        Gson gson = new Gson();
        logger.info("inputFile:   " + gson.toJson(inputFile));
    }

    /*
    get classpathEntries & sourcepathEntries from eclipse's .classpath file
     */
    public static InputFile readClasspathE(InputFile inputFile,String path) {
        List<String> classpathEntries=new ArrayList<String>();
        List<String> sourcepathEntries=new ArrayList<String>();
        try {
            File iFile = new File(path+"/.classpath");
            if(iFile!=null){
                DocumentBuilderFactory dbFactory
                        = DocumentBuilderFactory.newInstance();
                DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                Document doc = dBuilder.parse(iFile);
                doc.getDocumentElement().normalize();
//                logger.info("Root element :" + doc.getDocumentElement().getNodeName());
                NodeList nList = doc.getDocumentElement().getElementsByTagName("classpathentry");
                for (int temp = 0; temp < nList.getLength(); temp++) {
                    Element eElement = (Element) nList.item(temp);
                    if(eElement.getAttribute("kind").equals("lib")){
                        classpathEntries.add(eElement.getAttribute("path"));
                    }

                    if(sourcepathEntries.isEmpty()&&eElement.getAttribute("kind").equals("src")){
                        String spE=eElement.getAttribute("path");
                        if(!spE.startsWith("/")){
                           spE=path+"/"+spE;
                        }
                        sourcepathEntries.add(spE);
                    }
                }
            }
        } catch (Exception e) {

        }
        if(!classpathEntries.isEmpty()){
            inputFile.setClasspathEntries(classpathEntries.toArray(new String[classpathEntries.size()]));
        }
        if(!sourcepathEntries.isEmpty()){
            inputFile.setSourcepathEntries(sourcepathEntries.toArray(new String[sourcepathEntries.size()]));
        }
        return inputFile;
    }
}