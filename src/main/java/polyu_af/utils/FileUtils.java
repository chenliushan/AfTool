package polyu_af.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import polyu_af.models.LineAccessVars;
import polyu_af.models.MethodAccessVars;
import polyu_af.models.MyExp;
import polyu_af.models.TargetFile;

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
    private static final String relativePath = "tmp/firstStepOut.json";

    public static void outputTfList(List<TargetFile> obj) {
        outputTfList(obj, relativePath);
    }

    public static void outputTfList(List<TargetFile> obj, String path) {
//        Gson gson = new GsonBuilder()
//                .registerTypeAdapter(obj.getClass(), new TfListTypeAdapter())
//                .create();
        Gson gson=new Gson();
        String jsonObj = gson.toJson(obj);
//        System.out.println("jsonObj:" + jsonObj);
        writeFile(jsonObj,path);
    }

    private static class TfListTypeAdapter extends TypeAdapter<List<TargetFile>> {


        @Override
        public void write(JsonWriter jsonWriter, List<TargetFile> targetFiles) throws IOException {

            jsonWriter.beginArray();
            for (TargetFile tf : targetFiles) {
                jsonWriter.beginObject();
                jsonWriter.name(TargetFile.TfPara.ABSOLUTE_DIR).value(tf.getAbsoluteDir());
                jsonWriter.name(TargetFile.TfPara.PACKAGE_NAME).value(tf.getPackageName());
                jsonWriter.name(TargetFile.TfPara.FILE_NAME).value(tf.getFileName());
                List<MethodAccessVars> mavs = tf.getMethodAccessVars();
                if (mavs == null) {
                    jsonWriter.name(TargetFile.TfPara.METHOD_ACCESS_VARS).nullValue();
                } else {
                    jsonWriter.name(TargetFile.TfPara.METHOD_ACCESS_VARS).beginArray();
                    for (MethodAccessVars mav : mavs) {
                        jsonWriter.beginObject();
                        jsonWriter.name(MethodAccessVars.MavPara.METHOD_NAME).value(mav.getMethodName());
                        List<String> paras = mav.getParams();
                        if (paras == null) {
                            jsonWriter.name(MethodAccessVars.MavPara.PARAM_TYPES).nullValue();
                        } else {
                            jsonWriter.name(MethodAccessVars.MavPara.PARAM_TYPES).beginArray();
                            for (String para : paras) {
                                jsonWriter.value(para);
                            }
                            jsonWriter.endArray();

                        }
                        List<LineAccessVars> las = mav.getVarsList();
                        if (las == null) {
                            jsonWriter.name(MethodAccessVars.MavPara.METHOD_NAME).nullValue();
                        } else {
                            jsonWriter.name(MethodAccessVars.MavPara.METHOD_NAME).beginArray();
                            for (LineAccessVars la : mav.getVarsList()) {
                                jsonWriter.beginObject();
                                jsonWriter.name(LineAccessVars.LavPara.LOCATION).value(la.getLocation());
                                List<MyExp> mes=la.getVarsList();
                                if(mes==null){
                                    jsonWriter.name(LineAccessVars.LavPara.VARS_LIST).nullValue();
                                }else{
                                    jsonWriter.name(LineAccessVars.LavPara.VARS_LIST).beginArray();
                                    for (MyExp me : la.getVarsList()) {
                                        jsonWriter.beginObject();
                                        jsonWriter.name(MyExp.MePara.EXP_VAR).value(me.getExpVar());
                                        jsonWriter.name(MyExp.MePara.TYPE).value(me.getType());
                                        jsonWriter.endObject();
                                    }
                                    jsonWriter.endArray();
                                }
                                jsonWriter.endObject();
                            }
                            jsonWriter.endArray();
                        }
                        jsonWriter.endObject();
                    }
                    jsonWriter.endArray();
                }
                jsonWriter.endObject();
            }
            jsonWriter.endArray();
        }

        @Override
        public List<TargetFile> read(JsonReader jsonReader) throws IOException {

            return null;
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

    public static List<TargetFile> json2Obj() {
        return json2Obj(relativePath);
    }

    public static List<TargetFile> json2Obj(String pathName) {
        Gson gson = new Gson();
        List<TargetFile>obj = gson.fromJson(getSource(pathName), new TypeToken<List<TargetFile>>(){}.getType());
        return obj;
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
                    StringBuffer buffer = new StringBuffer();
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
