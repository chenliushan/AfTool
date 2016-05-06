package polyu_af.process;

import com.google.gson.Gson;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import polyu_af.exception.NotFoundException;
import polyu_af.models.TargetProgram;
import polyu_af.utils.ReadFileUtils;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
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

    public GetConfiguration(String inputPath) throws NotFoundException {
        this.inputPath = inputPath;
        getInput();
    }

    public TargetProgram getTargetProgram() {
        return targetProgram;
    }


    public void saveNewFaultClass(String source) {
        if (targetProgram != null) {
            try {
                String className = targetProgram.getTargetClassList().get(0).getSourceName();
                int idx = className.lastIndexOf("/");
                String subPath = className.substring(0, idx);
                String name = className.substring(idx + 1);
                Path p = Paths.get(targetProgram.getSourcePath(), subPath + "_af");
                if (Files.exists(p)) {

                } else {
                    Files.createDirectory(p);
                }
                Files.write(Paths.get(targetProgram.getSourcePath(), subPath + "_af", name), source.getBytes());
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
    private void getInput() throws NotFoundException {
        String input = ReadFileUtils.readFile(inputPath);
        if (input.trim() != "" && input != null) {
            Gson gson = new Gson();
            targetProgram = gson.fromJson(input, TargetProgram.class);
            if (targetProgram.getProjectDir() != null) {
                if (targetProgram.getSourcePath() == null || targetProgram.getClasspathEntries() == null) {
                    readClasspathI(targetProgram.getProjectDir());
                }
                if (targetProgram.getSourcePath() == null || targetProgram.getClasspathEntries() == null) {
                    readClasspathE(targetProgram.getProjectDir());
                    //sometimes the <component> <output> will miss and will turn to default path "out". this problem not solve yet
                }
            }
            logger.info("TargetProgram:" + targetProgram.toString());
            if (targetProgram.getSourcePath() == null || targetProgram.getClasspathEntries() == null) {
                logger.error("SourcepathEntries: " + targetProgram.getSourcePath());
                logger.error("ClasspathEntries: " + targetProgram.getClasspathEntries());
                throw new NotFoundException("Did not get the desire environment.");
            }

        }

    }


    /**
     * get classpathEntries & sourcepathEntries from eclipse's .classpath file
     *
     * @param path the project path
     * @return
     */
    private void readClasspathE(String path) {
        List<String> classpathEntries = new ArrayList<String>();
        String sourcepath=null;
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
                    if (sourcepath==null && eElement.getAttribute("kind").equals("src")) {
                        String spE = eElement.getAttribute("path");
                        sourcepath=ReadFileUtils.joinDir(path, spE);
                    }
                }
            }
        } catch (Exception e) {

        }
        if (!classpathEntries.isEmpty()) {
            targetProgram.setClasspathEntries(classpathEntries.toArray(new String[classpathEntries.size()]));
        }
        if (sourcepath!=null) {
            targetProgram.setSourcePath(sourcepath);
        }
    }


    /**
     * get classpathEntries & sourcepathEntries from Idea's .iml file
     *
     * @param path the project path
     * @return
     */
    private void readClasspathI(String path) {
        List<String> classpathEntries = new ArrayList<String>();
        String sourcepathE = null;
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

                //get outputPath
                NodeList outputList = component.getElementsByTagName("output");
                Element output = (Element) outputList.item(0);
                if (output != null) {
                    targetProgram.setOutputPath(imlUrl2Path(path, output.toString()));
                } else {
                    targetProgram.setOutputPath(imlUrl2Path(path, "file://$MODULE_DIR$/build/classes/main"));
                }

                //get sourcePath
                NodeList contentList = component.getElementsByTagName("content");
                Element content = (Element) contentList.item(0);
                NodeList sourceFolderList = content.getElementsByTagName("sourceFolder");
                String spathNodeValue = null;
                for (int i = 0; i < sourceFolderList.getLength(); i++) {
                    Element sourceFolder = (Element) sourceFolderList.item(i);
                    String sf = sourceFolder.getAttribute("url");
                    if (sf.endsWith("java") && !sf.contains("test")) {
                        spathNodeValue = sf;
                        break;
                    }
                }
                if (spathNodeValue == null) {
                    return;
                }
                sourcepathE=(imlUrl2Path(path, spathNodeValue));

                //get classPath
                NodeList libraryList = component.getElementsByTagName("library");
                for (int temp = 0; temp < libraryList.getLength(); temp++) {
                    Element library = (Element) libraryList.item(temp);
                    Element classNode = (Element) library.getElementsByTagName("CLASSES").item(0);
                    Element pathNode = (Element) classNode.getElementsByTagName("root").item(0);
                    String pathNodeValue = pathNode.getAttribute("url");
                    classpathEntries.add(imlUrl2Path(path, pathNodeValue));
                }
            }
        } catch (Exception e) {

        }
        if (!classpathEntries.isEmpty()) {
            targetProgram.setClasspathEntries(classpathEntries.toArray(new String[classpathEntries.size()]));
        }
        if (sourcepathE!=null) {
            targetProgram.setSourcePath(sourcepathE);
        }

    }

    public static String imlUrl2Path(String path, String subPath) {
        if (subPath.endsWith("!/")) {
            subPath = subPath.substring(0, subPath.length() - 2);
        }
        if (subPath.contains("$MODULE_DIR$")) {
            subPath = subPath.substring(subPath.indexOf("$MODULE_DIR$") + 12);
        }else{
            subPath = subPath.substring(subPath.indexOf(":/") + 3);
            return subPath;
        }

        return ReadFileUtils.joinDir(path, subPath);

    }
//    public static String imlUrl2Path(String projectPath, String subPath) throws MalformedURLException {
//        URL url=new URL(subPath);
//        String path=url.getPath();
//        logger.info("URL.path:"+path);
//        if (path.endsWith("!/")) {
//            subPath = subPath.substring(0, subPath.length() - 2);
//        }
//        if (subPath.contains("$MODULE_DIR$")) {
//            subPath = subPath.substring(subPath.indexOf("$MODULE_DIR$") + 12);
//        }else{
//            subPath = subPath.substring(subPath.indexOf(":/") + 3);
//            return subPath;
//        }
//
//        return ReadFileUtils.joinDir(projectPath, subPath);
//
//    }

}
