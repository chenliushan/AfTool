package polyu_af.models;

import com.google.gson.Gson;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import polyu_af.utils.ReadFileUtils;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by liushanchen on 16/3/17.
 */
public class GetConfiguration {
    private static Logger logger = LogManager.getLogger(GetConfiguration.class.getName());

    private TargetProgram targetProgram = null;
    private String inputPath = null;//the configuration file's path

    public GetConfiguration(String inputPath) {
        this.inputPath = inputPath;
        getInput();
    }

    public TargetProgram getTargetProgram() {
        return targetProgram;
    }


    public void saveNewFaultClass(String source) {
        if (targetProgram != null) {
            try {
                String className = targetProgram.getFaultClassList().get(0).getSourceName();
                int idx = className.lastIndexOf("/");
                String subPath = className.substring(0, idx);
                String name = className.substring(idx + 1);
                Path p = Paths.get(targetProgram.getSourcepathEntries()[0], subPath+"_af");
                if(Files.exists(p)){

                }else{
                    Files.createDirectory(p);
                }
                Files.write(Paths.get(targetProgram.getSourcepathEntries()[0], subPath+"_af",name), source.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }

    /**
     * read the input file and create inputFile object
     *
     * @return
     */
    private void getInput() {
        String input = ReadFileUtils.readFile(inputPath);
        if (input.trim() != "" && input != null) {
            Gson gson = new Gson();
            targetProgram = gson.fromJson(input, TargetProgram.class);
            if (targetProgram.getProjectDir() != null) {
                if (targetProgram.getSourcepathEntries() == null && targetProgram.getClasspathEntries() == null) {
                    targetProgram = readClasspathI(targetProgram.getProjectDir());
                }
                if (targetProgram.getSourcepathEntries() == null && targetProgram.getClasspathEntries() == null) {
                    targetProgram = readClasspathE(targetProgram.getProjectDir());
                    //sometimes the <component> <output> will miss and will turn to default path "out". this problem not solve yet
                }
            }
            logger.info("TargetProgram:" + targetProgram.toString());
            if (targetProgram.getSourcepathEntries() == null && targetProgram.getClasspathEntries() == null) {
                logger.error("Did not get the desire environment.");
                logger.error("SourcepathEntries: " + targetProgram.getSourcepathEntries());
                logger.error("ClasspathEntries: " + targetProgram.getClasspathEntries());
                return;
            }

        }

    }


    /**
     * get classpathEntries & sourcepathEntries from eclipse's .classpath file
     *
     * @param path the project path
     * @return
     */
    private TargetProgram readClasspathE(String path) {
        List<String> classpathEntries = new ArrayList<String>();
        List<String> sourcepathEntries = new ArrayList<String>();
        try {
            File iFile = new File(path + "/.classpath");
            if (iFile != null) {
                DocumentBuilderFactory dbFactory
                        = DocumentBuilderFactory.newInstance();
                DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                Document doc = dBuilder.parse(iFile);
                doc.getDocumentElement().normalize();
//                logger.info("Root element :" + doc.getDocumentElement().getNodeName());
                NodeList nList = doc.getDocumentElement().getElementsByTagName("classpathentry");
                for (int temp = 0; temp < nList.getLength(); temp++) {
                    Element eElement = (Element) nList.item(temp);
                    if (eElement.getAttribute("kind").equals("lib")) {
                        classpathEntries.add(eElement.getAttribute("path"));
                    }
                    if (sourcepathEntries.isEmpty() && eElement.getAttribute("kind").equals("src")) {
                        String spE = eElement.getAttribute("path");
                        sourcepathEntries.add(ReadFileUtils.joinDir(path, spE));
                    }
                }
            }
        } catch (Exception e) {

        }
        if (!classpathEntries.isEmpty()) {
            targetProgram.setClasspathEntries(classpathEntries.toArray(new String[classpathEntries.size()]));
        }
        if (!sourcepathEntries.isEmpty()) {
            targetProgram.setSourcepathEntries(sourcepathEntries.toArray(new String[sourcepathEntries.size()]));
        }
        return targetProgram;
    }


    /**
     * get classpathEntries & sourcepathEntries from Idea's .iml file
     *
     * @param path the project path
     * @return
     */
    private TargetProgram readClasspathI(String path) {
        List<String> classpathEntries = new ArrayList<String>();
        List<String> sourcepathEntries = new ArrayList<String>();
        try {
            String projectName = path.substring(path.lastIndexOf('/') + 1);

            File iFile = new File(path + "/" + projectName + ".iml");
            if (iFile != null) {
                DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                Document doc = dBuilder.parse(iFile);
                doc.getDocumentElement().normalize();
                Element rootElement = (Element) doc.getDocumentElement();
                NodeList componentList = rootElement.getElementsByTagName("component");
                Element component = (Element) componentList.item(0);
                NodeList contentList = component.getElementsByTagName("content");
                Element content = (Element) contentList.item(0);
                NodeList sourceFolderList = content.getElementsByTagName("sourceFolder");
                Element sourceFolder = (Element) sourceFolderList.item(0);

                String spathNodeValue = sourceFolder.getAttribute("url");
                String sourcepath = spathNodeValue.substring(spathNodeValue.indexOf("$MODULE_DIR$") + 12);
                sourcepathEntries.add(ReadFileUtils.joinDir(path, sourcepath));


                NodeList libraryList = component.getElementsByTagName("library");
                for (int temp = 0; temp < libraryList.getLength(); temp++) {
                    Element library = (Element) libraryList.item(temp);
                    Element classNode = (Element) library.getElementsByTagName("CLASSES").item(0);
                    Element pathNode = (Element) classNode.getElementsByTagName("root").item(0);
                    String pathNodeValue = pathNode.getAttribute("url");
                    String classpath = pathNodeValue.substring(pathNodeValue.indexOf("$MODULE_DIR$") + 12);

                    if (classpath.endsWith("!/")) {
                        classpath = classpath.substring(0, classpath.length() - 2);
                    }
                    classpathEntries.add(ReadFileUtils.joinDir(path, classpath));


                }
            }
        } catch (Exception e) {

        }
        if (!classpathEntries.isEmpty()) {
            targetProgram.setClasspathEntries(classpathEntries.toArray(new String[classpathEntries.size()]));
        }
        if (!sourcepathEntries.isEmpty()) {
            targetProgram.setSourcepathEntries(sourcepathEntries.toArray(new String[sourcepathEntries.size()]));
        }
        return targetProgram;
    }


}
