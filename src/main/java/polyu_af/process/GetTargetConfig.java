package polyu_af.process;

import com.google.gson.Gson;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import polyu_af.exception.NotFoundException;
import polyu_af.models.TargetConfig;
import polyu_af.utils.FileUtils;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by liushanchen on 16/3/17.
 */
public class GetTargetConfig {
    private static Logger logger = LogManager.getLogger(GetTargetConfig.class.getName());

    private TargetConfig tc = null;
    private String inputPath = null;//the configuration file's path

    public GetTargetConfig(String inputPath) throws NotFoundException {
        this.inputPath = inputPath;
        getInput();
    }

    public TargetConfig getTc() {
        return tc;
    }

//
//    public void saveNewFaultClass(String source) {
//        if (tc != null) {
//            try {
//                String className = tc.getTargetClassList().get(0).getSourceName();
//                int idx = className.lastIndexOf("/");
//                String subPath = className.substring(0, idx);
//                String name = className.substring(idx + 1);
//                Path p = Paths.get(tc.getSourcePath(), subPath + "_af");
//                if (Files.exists(p)) {
//
//                } else {
//                    Files.createDirectory(p);
//                }
//                Files.write(Paths.get(tc.getSourcePath(), subPath + "_af", name), source.getBytes());
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//
//
//    }

    /**
     * read the input file and create inputFile object
     *
     * @return
     */
    private void getInput() throws NotFoundException {
        String input = FileUtils.getSource(inputPath);
        if (input.trim() != "" && input != null) {
            Gson gson = new Gson();
            tc = gson.fromJson(input, TargetConfig.class);
            if (tc.getProjectDir() != null) {
                if (tc.getSourcePath() == null || tc.getClasspathEntries() == null) {
                    readClasspathI(tc.getProjectDir());
                }
                if (tc.getSourcePath() == null || tc.getClasspathEntries() == null) {
                    readClasspathE(tc.getProjectDir());
                    //sometimes the <component> <output> will miss and will turn to default path "out". this problem not solve yet
                }
            }
            logger.info("TargetProgram:" + tc.toString());
            if (tc.getSourcePath() == null || tc.getClasspathEntries() == null) {
                logger.error("SourcepathEntries: " + tc.getSourcePath());
                logger.error("ClasspathEntries: " + tc.getClasspathEntries());
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
        String sourcepath = null;
        try {
            File iFile = new File(path + "/.classpath");
            Element rootElement = getRootElement(iFile);
            if (rootElement == null) return;
            NodeList nList = rootElement.getElementsByTagName("classpathentry");
            for (int temp = 0; temp < nList.getLength(); temp++) {
                Element eElement = (Element) nList.item(temp);
                if (eElement.getAttribute("kind").equals("lib")) {
                    classpathEntries.add(eElement.getAttribute("path"));
                }
                if (sourcepath == null && eElement.getAttribute("kind").equals("src")) {
                    String spE = eElement.getAttribute("path");
                    sourcepath = FileUtils.joinDir(path, spE);
                }
            }
        } catch (Exception e) {

        }
        if (!classpathEntries.isEmpty()) {
            tc.setClasspathEntries(classpathEntries.toArray(new String[classpathEntries.size()]));
        }
        if (sourcepath != null) {
            tc.setSourcePath(sourcepath);
        }
    }


    /**
     * get classpathEntries & sourcepathEntries from Idea's .iml file
     *
     * @param path the project path
     * @return
     */
    private void readClasspathI(String path) {
        List<String> classpathEntries = null;
        String sourcepathE = null;
        try {
            String projectName = path.substring(path.lastIndexOf('/') + 1);
            File iFile = new File(path + "/" + projectName + ".iml");
            Element rootElement = getRootElement(iFile);
            if (rootElement == null) return;
            NodeList componentList = rootElement.getElementsByTagName("component");
            Element component = (Element) componentList.item(0);
            //get output path
            String opPath = getIOutputPath(component);
            tc.setOutputPath(imlUrl2Path(path, opPath));
            //get test clas path
            String testPath=getITestPath(component);
            tc.setTestClassPath(imlUrl2Path(path, testPath));
            //get sourcePath
            String spathNodeValue = getISourcePath(component);
            if (spathNodeValue == null) return;
            sourcepathE = (imlUrl2Path(path, spathNodeValue));
            //get classPath
            classpathEntries = getIClassPath(path, component);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (classpathEntries != null && !classpathEntries.isEmpty()) {
            tc.setClasspathEntries(classpathEntries.toArray(new String[classpathEntries.size()]));
        }
        if (sourcepathE != null) {
            tc.setSourcePath(sourcepathE);
        }

    }

    /**
     * get the root element of the target configuration file
     *
     * @param iFile
     * @return
     * @throws ParserConfigurationException
     * @throws IOException
     * @throws SAXException
     */
    private Element getRootElement(File iFile) throws ParserConfigurationException, IOException, SAXException {
        if (iFile == null) return null;
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(iFile);
        doc.getDocumentElement().normalize();
        return doc.getDocumentElement();
    }

    /**
     * get outputPath
     *
     * @param component
     * @return
     */
    private String getIOutputPath(Element component) {
        String op = null;
        if (component != null) {
            NodeList outputList = component.getElementsByTagName("output");
            Element output = (Element) outputList.item(0);
            if (output != null) op = output.getAttribute("url");
        }
        if (op == null) {
            return "file://$MODULE_DIR$/build/classes/main";
        }
        return op;
    }
    /**
     * get test class path
     *
     * @param component
     * @return
     */
    private String getITestPath(Element component) {
        String op = null;
        if (component != null){
            NodeList outputList = component.getElementsByTagName("output-test");
            Element output = (Element) outputList.item(0);
            if (output != null) op = output.getAttribute("url");
        }
        if (op == null) {
            return "file://$MODULE_DIR$/build/classes/test";
        }
        return op;
    }

    /**
     * get sourcePath
     *
     * @param component
     * @return
     */
    private String getISourcePath(Element component) {
        if (component == null) return null;
        NodeList contentList = component.getElementsByTagName("content");
        Element content = (Element) contentList.item(0);
        NodeList sourceFolderList = content.getElementsByTagName("sourceFolder");
        for (int i = 0; i < sourceFolderList.getLength(); i++) {
            Element sourceFolder = (Element) sourceFolderList.item(i);
            String sf = sourceFolder.getAttribute("url");
            if (sf.endsWith("java") && !sf.contains("test")) {
                return sf;
            }
        }
        return null;
    }

    /**
     * get target project classPaths
     *
     * @param path
     * @param component
     * @return
     * @throws URISyntaxException
     */
    private List<String> getIClassPath(String path, Element component) throws URISyntaxException {
        if (component == null) return null;
        List<String> classpathEntries = new ArrayList<String>();
        NodeList libraryList = component.getElementsByTagName("library");
        for (int temp = 0; temp < libraryList.getLength(); temp++) {
            Element library = (Element) libraryList.item(temp);
            Element classNode = (Element) library.getElementsByTagName("CLASSES").item(0);
            Element pathNode = (Element) classNode.getElementsByTagName("root").item(0);
            String pathNodeValue = pathNode.getAttribute("url");
            classpathEntries.add(imlUrl2Path(path, pathNodeValue));
        }
        return classpathEntries;
    }

    private String imlUrl2Path(String projectDir, String subPath) throws URISyntaxException {
        if (subPath.contains("$MODULE_DIR$")) {
            subPath = subPath.replace("$MODULE_DIR$", projectDir);
        }
        if (subPath.endsWith("!/")) {
            subPath = subPath.substring(0, subPath.length() - 2);
        }
        URI uri = new URI(subPath);
        return uri.getPath();

    }
}
