package polyu_af.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import polyu_af.TestCluster;
import polyu_af.models.*;
import polyu_af.new_model.TargetFile;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
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
public class FileUtils {
    private static Logger logger = LogManager.getLogger(FileUtils.class.getName());

    public static void outputTfList(List<TargetFileOld> obj) {
        outputTfList(obj, Constants.firstStepOutputPath);
    }
    public static void output(Object obj) {
        outputTfList(obj, Constants.firstStepOutputPath);
    }


    public static void outputTfList(Object obj, String path) {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(MyExp.class, new MyExpTypeAdapter())
                .disableHtmlEscaping()
                .create();
//        Gson gson = new GsonBuilder().disableHtmlEscaping().create();
        String jsonObj = gson.toJson(obj);
        writeFile(jsonObj, path);
    }

    private static class MyExpTypeAdapter extends TypeAdapter<MyExp> {

        @Override
        public void write(JsonWriter jsonWriter, MyExp myExpAst) throws IOException {
                jsonWriter.beginObject();
                jsonWriter.name(MyExp.MyExpAstPara.TYPE).value(myExpAst.getType());
                jsonWriter.name(MyExp.MyExpAstPara.EXPVAR).value(myExpAst.getExpVar());
                jsonWriter.endObject();
        }

        @Override
        public MyExp read(JsonReader jsonReader) throws IOException {
            if (jsonReader.peek() == JsonToken.NULL) {
                jsonReader.nextNull();
                return null;
            }
             MyExp myExp = new MyExpString();
            jsonReader.beginObject();
            while (jsonReader.hasNext()) {
                switch (jsonReader.nextName()) {
                    case MyExp.MyExpAstPara.TYPE:
                        myExp.setType(jsonReader.nextString());
                        break;
                    case MyExp.MyExpAstPara.EXPVAR:
                        myExp.setExpVar(jsonReader.nextString());
                        break;

                }
            }
            jsonReader.endObject();

            return myExp;
//            String xy = jsonReader.nextString();
//            String[] parts = xy.split(",");
//            String type = parts[0];
//            String expvar = parts[1];
//            return new MyExpString(type, expvar);

        }
    }

    private static void writeFile(String source, String path) {
        Path p = Paths.get(path).getParent();
        try {
            if (!Files.exists(p)) {
                Files.createDirectory(p);
            }
            Files.write(Paths.get(path), source.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<TargetFileOld> json2TfList() {
        String pathName = Constants.firstStepOutputPath;
//        Gson gson = new Gson();
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(MyExpAst.class, new MyExpTypeAdapter())
                .registerTypeAdapter(MyExp.class, new MyExpTypeAdapter())
                .registerTypeAdapter(MyExpString.class, new MyExpTypeAdapter())
                .create();
        List<TargetFileOld> obj = gson.fromJson(getSource(pathName), new TypeToken<List<TargetFileOld>>() {
        }.getType());
        return obj;
    }

    public static List<TestCluster> json2TestClusterList() {
        String pathName = Constants.myJunitLogPath;
        Gson gson = new Gson();
        BufferedReader input = getSourceBuffer(new File(pathName));
        List<TestCluster> testClusters=new ArrayList<TestCluster>();
        String text;
        try {
            while ((text = input.readLine()) != null){
                if(text=="\n"||text.length()==0){continue;}
                TestCluster cluster = gson.fromJson(text, new TypeToken<TestCluster>() {}.getType());
                testClusters.add(cluster);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return testClusters;
    }


    public static String getSource(String pathName) {
        File file = new File(pathName);
        return getSource(file);
    }

    public static String getSource(File file) {
        String fileContent = "";
        if (file.exists()) {
            if (file.isFile()) {
                try {
                    BufferedReader input = new BufferedReader(new FileReader(file));
                    StringBuilder buffer = new StringBuilder();
                    String text;
                    while ((text = input.readLine()) != null)
                        buffer.append(text + "\n");
                    fileContent = buffer.toString();
                } catch (IOException ioException) {
                    logger.error("getSource: " + ioException.getStackTrace().toString());
                }
            }
        }
        return fileContent;
    }

    public static BufferedReader getSourceBuffer(File file) {
        BufferedReader input = null;
        if (file.exists()) {
            if (file.isFile()) {
                try {
                    input = new BufferedReader(new FileReader(file));
                } catch (IOException ioException) {
                    logger.error("getSource: " + ioException.getStackTrace().toString());
                }
            }
        }
        return input;
    }

    public static ArrayList<File> getListFiles(String path) {
        File directory = new File(path);
        return getListFiles(directory);
    }

    public static ArrayList<File> getListFiles(File directory) {
        ArrayList<File> files = new ArrayList<File>();
        if (directory.isFile()) {
            files.add(directory);
            return files;
        } else if (directory.isDirectory()) {
            File[] fileArr = directory.listFiles();
            for (int i = 0; i < fileArr.length; i++) {
                File fileOne = fileArr[i];
                files.addAll(getListFiles(fileOne));
            }
        }
        return files;
    }


    public static String joinDir(String path, String subPath) {
        if (!subPath.startsWith("/") && !path.endsWith("/")) {
            subPath = path + "/" + subPath;
        } else {
            subPath = path + subPath;
        }
        return subPath;
    }

    public static URL getURL(String path) {
        File files = new File(path);
        try {
            return files.toURI().toURL();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }


}
