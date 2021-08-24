package botmanager.utils;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 *
 * @author MC_2018 <mc2018.git@gmail.com>
 */
public class IOUtils {
    
    public static String read(File file) throws IOException {
        StringBuilder result = new StringBuilder();
        FileReader fr = new FileReader(file);
        BufferedReader br = new BufferedReader(fr);
        String buffer;

        while ((buffer = br.readLine()) != null) {
            result.append(buffer);
        }

        br.close();
        fr.close();
        return result.toString();
    }

    public static List<String> readLines(File file) throws IOException {
        return Files.readLines(file, Charsets.UTF_8);
    }

    public static void write(File file, String info) throws IOException {
        verifyFilePathExists(file);

        FileWriter fw = new FileWriter(file, false);
        BufferedWriter bw = new BufferedWriter(fw);
        bw.write(info);
        bw.close();
        fw.close();
    }

    public static <T>T readGson(File file, Class<T> objClass) throws IOException {
        Gson gson = new GsonBuilder().serializeNulls().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").create();
        return gson.fromJson(read(file), objClass);
    }
    
    public static <T>void writeGson(File file, T obj) throws IOException {
        Gson gson = new GsonBuilder().serializeNulls().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").create();
        write(file, gson.toJson(obj));
    }
    
    public static <T>void writeGson(File file, T obj, boolean prettyPrinting) throws IOException {
        Gson gson;
        
        if (prettyPrinting) {
            gson = new GsonBuilder().serializeNulls().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").setPrettyPrinting().create();
        } else {
            gson = new GsonBuilder().serializeNulls().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").create();
        }

        write(file, gson.toJson(obj));
    }
    
    public static void verifyFilePathExists(File file) {
        File directory;
        String path = file.getAbsolutePath().replaceAll("\\\\", "/");
        String[] folderSeparation = path.split("/");
        StringBuilder directoryBuilder = new StringBuilder("");

        for (int i = 0; i < folderSeparation.length - 1; i++) {
            directoryBuilder.append(folderSeparation[i]);
            directoryBuilder.append("/");
        }

        directory = new File(directoryBuilder.toString());

        if (!directory.exists()) {
            directory.mkdirs();
        }
    }

    public static String getTrueFileName(File file) {
        return file.getName().split("\\.")[0];
    }


}
