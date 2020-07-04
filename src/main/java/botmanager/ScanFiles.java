package botmanager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author MC_2018 <mc2018.git@gmail.com>
 */

public class ScanFiles {

    public static void main(String[] args) {
        File startFile = new File("C:\\Users\\max\\Dropbox\\A Programming\\Spring 2020\\BotManager\\src\\main\\java");
        List<File> recursive = recursive(startFile);
        int count = 0;
        int totalCount = 0;
        
        for (File file : recursive) {
            if (file.getName().endsWith("java") || file.getName().endsWith("js") || file.getName().contains("custom")) {
                String text = Utilities.read(file);
                totalCount += text.length();
                text = text.replaceAll("  ", "");
                count += text.length();
                System.out.println(text.length() + "\t" + file.getAbsolutePath());
            }
        }
        
        System.out.println("Count: " + count);
        System.out.println("Total Count: " + totalCount);
    }
    
    public static List<File> recursive(File file) {
        List<File> list = new ArrayList();
        
        if (file.isDirectory()) {
            File[] children = file.listFiles();
            
            for (File child : children) {
                list.addAll(recursive(child));
            }
        } else if (file.isFile()) {
            list.add(file);
        }
        
        return list;
    }
    
}
