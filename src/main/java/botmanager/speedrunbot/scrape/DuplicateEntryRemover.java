package botmanager.speedrunbot.scrape;

import botmanager.IOUtils;
import botmanager.speedrunbot.webdriver.SingleWebDriver;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

//where name equals an id that isn't the same id as name, freak out

/**
 *
 * @author MC_2018 <mc2018.git@gmail.com>
 */

public class DuplicateEntryRemover {

    public static void main(String[] args) {
        try {
            String[] customSet = read(new File("src/main/java/botmanager/speedrunbot/scrape/game_name_shortcuts_custom.csv")).split("\n");
            String[] scrapeSet = read(new File("src/main/java/botmanager/speedrunbot/scrape/game_name_shortcuts_scrape.csv")).split("\n");
            ArrayList<String> conglomerateSet = new ArrayList<>();
            ArrayList<String> conglomerateNameSet = new ArrayList<>();
            ArrayList<String> conglomerateIdSet = new ArrayList<>();
            
            for (String custom : customSet) {
                String name = custom.split(",")[0];
                String id = custom.split(",")[1];
                
                if (!conglomerateSet.contains(simplify(custom))) {
                    conglomerateSet.add(simplify(custom));
                }
                
                if (!conglomerateNameSet.contains(simplify(name))) {
                    conglomerateNameSet.add(simplify(name));
                } else {
                    System.out.println("repeat: " + custom);
                }
                
                if (!conglomerateIdSet.contains(id)) {
                    conglomerateIdSet.add(id);
                }
            }
            
            for (int i = 0; i < scrapeSet.length; i++) {
                scrapeSet[i] = simplify(scrapeSet[i]);
            }
            
            Collections.sort(conglomerateSet);
            SingleWebDriver web = new SingleWebDriver(false);
            
            int counter = 0;
            
            ArrayList<String> skippables = new ArrayList<>();
            
            for (String scrapeFullLine : scrapeSet) {
                String name = scrapeFullLine.split(",")[0];
                String id = scrapeFullLine.split(",")[1];
                String actual = name + "," + id;
                
                if (!conglomerateSet.contains(simplify(actual)) && !conglomerateNameSet.contains(simplify(name))) {
                    conglomerateSet.add(simplify(actual));
                    conglomerateNameSet.add(simplify(name));
                    
                    if (!skippables.contains(id) && !name.equals(id) && Integer.parseInt(scrapeFullLine.split(",")[2]) > 120 && counter < 10) {
                        skippables.add(id);
                        web.newTab("https://www.speedrun.com/" + id);
                        counter++;
                        System.out.println((!conglomerateIdSet.contains(id) ? "none\t" : "\t") + simplify(name) + "," + scrapeFullLine.split(",")[1] + "\t\t\t\t" + scrapeFullLine.split(",")[2]);
                    }
                }
            }
            
            conglomerateSet.sort((a, b) -> a.split(",")[0].length() - b.split(",")[0].length());
            String result = String.join("\n", conglomerateSet.toArray(new String[conglomerateSet.size()]));
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
        IOUtils.verifyFilePathExists(file);
        
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
