package botmanager.speedrunbot.scrape;

import botmanager.speedrunbot.*;
import botmanager.Utilities;
import static botmanager.Utilities.verifyFilePathExists;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/**
 *
 * @author MC_2018 <mc2018.git@gmail.com>
 */

public class DuplicateEntryRemover {

    public static void main(String[] args) {
        try {
            String[] custom = read(new File("data/Speedrun Bot/game_name_shortcuts_custom.csv")).split("\n");
            String[] scrape = read(new File("data/Speedrun Bot/game_name_shortcuts_scrape.csv")).split("\n");
            ArrayList<String> array = new ArrayList<>();
            ArrayList<String> partial = new ArrayList<>();
            
            for (String custom1 : custom) {
                String part = custom1.split(",")[0];
                if (!array.contains(simplify(custom1)) && !partial.contains(simplify(part))) {
                    array.add(simplify(custom1));
                    partial.add(simplify(part));
                }
            }
            
            for (String scrape1 : scrape) {
                String part = scrape1.split(",")[0];
                String actual = part + "," + scrape1.split(",")[1];
                if (!array.contains(simplify(scrape1)) && !partial.contains(simplify(part))) {
                    array.add(simplify(actual));
                    partial.add(simplify(part));
                    
                    if (Integer.parseInt(scrape1.split(",")[2]) > 120 && !part.contains("é")) {
                        System.out.println(part + "\t" + scrape1.split(",")[2] + "\t\t" + actual);
                    }
                }
            }
            
            array.sort((a, b) -> a.split(",")[0].length() - b.split(",")[0].length());
            String result = String.join("\n", array.toArray(new String[array.size()]));
            write(new File("data/Speedrun Bot/game_name_shortcuts.csv"), result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static String simplify(String str) {
        return str.toLowerCase().replaceAll("&", "and").replaceAll("\\+", "plus").replaceAll("[^-a-z0-9._,]", "");
    }
    
    public static String read(File file) {
        StringBuilder result = new StringBuilder("");
        
        try {
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            String buffer;
            
            while ((buffer = br.readLine()) != null) {
                result.append(buffer).append("\n");
            }
            
            br.close();
            fr.close();
            return result.toString();
        } catch (IOException e) {
            //e.printStackTrace();
            throw new RuntimeException(e.getLocalizedMessage());
        }
    }
    
    public static void write(File file, String info) {
        verifyFilePathExists(file);
        
        try {
            FileWriter fw = new FileWriter(file, false);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(info);
            bw.close();
            fw.close();
        } catch (IOException e) {
            throw new RuntimeException(e.getLocalizedMessage());
        }
    }
    
}
