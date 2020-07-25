package botmanager;

import botmanager.speedrunbot.SpeedrunBot;
import com.vdurmont.emoji.Emoji;
import com.vdurmont.emoji.EmojiManager;
import com.vdurmont.emoji.EmojiParser;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author MC_2018 <mc2018.git@gmail.com>
 */

public class Utils {
    
    public static String getCSVValueAtIndex(String csv, int index) {
        String[] values = csv.split(",");

        if (index < 0 || index >= values.length) {
            return null;
        }

        return values[index];
    }

    public static String buildCSV(String[] array) {
        String result = "";

        for (int i = 0; i < array.length; i++) {
            if (array[i] != null) {
                result += array[i];
            }

            if (i + 1 < array.length) {
                result += ",";
            }
        }

        return result;
    }

    public static boolean isNullOrEmpty(String s) {
        return s == null || s.equals("");
    }
    
    public static String getEmojiAlias(String unicode) {
        try {
            return EmojiParser.parseToAliases(unicode).replaceAll(":", "");
        } catch (Exception e) {
            return "";
        }
    }
    
    public static Emoji getEmoji(String potentialAlias) {
        Emoji emoji = EmojiManager.getForAlias(potentialAlias);
        
        if (emoji == null) {
        ArrayList<Emoji> emojis = new ArrayList(EmojiManager.getAll());
        double similarity = 0;
        
            for (Emoji potentialEmoji : emojis) {
                for (String alias : potentialEmoji.getAliases()) {
                    double potentialSimilarity = similarity(potentialAlias, alias);

                    if (potentialSimilarity > similarity) {
                        emoji = potentialEmoji;
                        similarity = potentialSimilarity;
                    }
                }
            }
        }
        
        return emoji;
    }

    public static String bestSimilarity(List<String> collection, String phrase) {
        String result = null;
        double bestSimilarity = -1;

        for (String s1 : collection) {
            double similarity = similarity(SpeedrunBot.simplify(s1), phrase);

            if (similarity > bestSimilarity) {
                result = s1;
                bestSimilarity = similarity;
            }
        }

        return result;
    }

    public static double similarity(String s1, String s2) {
        String longer = s1, shorter = s2;
        int longerLength;
        double result;
        
        if (s1.length() < s2.length()) {
            longer = s2;
            shorter = s1;
        }
        
        longerLength = longer.length();
        
        if (longerLength == 0) {
            return 1.0;
        }

        result = (longerLength - editDistance(longer, shorter)) / (double) longerLength;
        
        if (s1.contains(s2) || s2.contains(s1)) {
            result += (1 - result) / 2;
        }

        return result;
    }
    
    public static int editDistance(String s1, String s2) {
        int[] costs = new int[s2.length() + 1];
        s1 = s1.toLowerCase();
        s2 = s2.toLowerCase();

        for (int i = 0; i <= s1.length(); i++) {
            int lastValue = i;
            
            for (int j = 0; j <= s2.length(); j++) {
                if (i == 0) {
                    costs[j] = j;
                } else {
                    if (j > 0) {
                        int newValue = costs[j - 1];
                        
                        if (s1.charAt(i - 1) != s2.charAt(j - 1)) {
                            newValue = Math.min(Math.min(newValue, lastValue), costs[j]) + 1;
                        }
                        
                        costs[j - 1] = lastValue;
                        lastValue = newValue;
                    }
                }
            }
            
            if (i > 0) {
                costs[s2.length()] = lastValue;
            }
        }
        
        return costs[s2.length()];
    }
    
}
