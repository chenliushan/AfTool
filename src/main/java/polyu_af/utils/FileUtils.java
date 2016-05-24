package polyu_af.utils;

import com.google.gson.Gson;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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

/**
 * Created by liushanchen on 16/3/17.
 */
public class FileUtils {
    private static Logger logger = LogManager.getLogger(FileUtils.class.getName());
    private static final String relativePath = "tmp";
    private static final String suffix = ".json";

    public static void outputObj(Object obj) {
        StringBuilder fn = new StringBuilder(obj.getClass().getSimpleName());
        fn.append(obj);
        outputObj(obj, relativePath, fn.toString());
    }

    public static void outputObj(Object obj, String path, String fn) {
        Gson gson = new Gson();
        String jsonObj = gson.toJson(obj);
        Path p = Paths.get(path);
        try {
            if (!Files.exists(p)) {
                Files.createDirectory(p);
            }
            Files.write(Paths.get(path, fn), jsonObj.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Object json2Obj(Object obj) {
        return json2Obj(relativePath, obj);
    }

    public static Object json2Obj(String pathName, Object obj) {
        Gson gson = new Gson();
        obj = gson.fromJson(getSource(pathName), obj.getClass());
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
