package botmanager.speedrunbot;

import botmanager.speedrunbot.commands.PlaceCommand;
import botmanager.speedrunbot.commands.LeaderboardCommand;
import botmanager.speedrunbot.commands.RunCommand;
import botmanager.speedrunbot.commands.WorldRecordCommand;
import botmanager.speedrunbot.commands.HelpCommand;
import botmanager.generic.BotBase;
import botmanager.IOUtils;
import botmanager.Utils;
import com.tsunderebug.speedrun4j.game.Category;
import com.tsunderebug.speedrun4j.game.Game;
import com.tsunderebug.speedrun4j.game.Leaderboard;
import com.tsunderebug.speedrun4j.game.run.PlacedRun;
import com.tsunderebug.speedrun4j.game.run.Player;
import com.tsunderebug.speedrun4j.game.run.Run;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.internal.utils.tuple.Pair;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import botmanager.generic.ICommand;
import botmanager.generic.commands.PMForwarderCommand;
import botmanager.generic.commands.PMRepeaterCommand;
import botmanager.speedrunbot.webdriver.WebDriverManager;
import java.awt.Color;
import java.io.IOException;
import java.util.LinkedHashMap;

//idea: encrypter(s) built in?
/**
 *
 * @author MC_2018 <mc2018.git@gmail.com>
 */
public final class SpeedrunBot extends BotBase {

    private final WebDriverManager webDrivers = new WebDriverManager(10);
    private LinkedHashMap<String, String> gameSynonyms;
    private ArrayList<String> uniqueGameIds;
    private final String separator = "/";
    private final String errorUrl = "https://i.imgur.com/OUBCmGA.png";
    private String prefix;
    
    //look into making help button with embeds
    //add info command
    //idea: add (Tied for Xth place) in title of place/run
    //idea: for efficiency, organize gameSynonyms by length, only search for objects in range of similarity percentage
    //idea: keep top 10-100 most frequented games always queued up? refresh every day perhaps
    public SpeedrunBot(String botToken, String name) {
        super(botToken, name);
        prefix = "$";
        getJDA().getPresence().setActivity(Activity.playing(prefix + "help for info"));
        
        setCommands(new ICommand[] {
            new LeaderboardCommand(this),
            new WorldRecordCommand(this),
            new PlaceCommand(this),
            new RunCommand(this),
            new HelpCommand(this),
            new PMRepeaterCommand(this),
            new PMForwarderCommand(this)
        });
        
        buildHashMap(IOUtils.readLines(new File("data/" + name + "/game_name_shortcuts.csv")));
        
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                shutdown();
            }
        });
    }

    @Override
    public void onPrivateMessageReceived​(PrivateMessageReceivedEvent event) {
        Thread thread = new Thread() {
            @Override
            public void run() {
                for (ICommand command : getCommands()) {
                    command.run(event);
                }
            }
        };
        
        thread.start();
    }

    @Override
    public void onGuildMessageReceived​(GuildMessageReceivedEvent event) {
        Thread thread = new Thread() {
            @Override
            public void run() {
                for (ICommand command : getCommands()) {
                    command.run(event);
                }
            }
        };
        
        thread.start();
    }

    @Override
    public void shutdown() {
        try {
            webDrivers.close();
        } catch (Exception e) {
            
        }
        
        try {
            getJDA().shutdown();
        } catch (Exception e) {
            
        }
    }
    
    public void buildHashMap(List<String> information) {
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        uniqueGameIds = new ArrayList();
        String problem = null;
        
        try {
            for (String synonymSet : information) {
                String[] split = synonymSet.split(",");
                problem = synonymSet;
                
                if (split.length != 2) {
                    throw new Exception("Commas are broken");
                }
                
                split[1] = simplify(split[1]);
                uniqueGameIds.add(split[1]);
                split = (simplify(split[0]) + "," + capitalize(split[0]) + separator + split[1]).split(",");

                String test = map.put(split[0], split[1]);

                if (test != null) {
                    System.out.println(synonymSet);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Problem: " + problem);
        }
        
        gameSynonyms = map;
    }
    
    public ArrayList<String> getLeaderboardNames(Leaderboard lb, String url) {
        Document doc;
        Elements places;
        ArrayList<String> result = new ArrayList<>();
        
        doc = Jsoup.parse(webDrivers.getPageSource(url));
        places = doc.getElementById("leaderboarddiv").getElementsByClass("linked");
        
        for (int i = 0; i < places.size(); i++) {
            Elements names = places.get(i).getElementsByClass("username-light");
            StringBuilder nameset = new StringBuilder();
            
            for (int j = 0; j < names.size(); j++) {
                if (nameset.length() == 0) {
                    nameset = nameset.append(names.get(j).text());
                } else {
                    nameset = nameset.append(", ").append(names.get(j).text());
                }
            }
            
            result.add(nameset.toString());
        }
        
        return result;
    }
    
    public String getPrefix() {
        return prefix;
    }

    public String determineGameID(String game) {
        double bestSimilarity = -1;
        String result;

        game = simplify(game);

        result = gameSynonyms.get(game);

        if (result != null) {
            return result.split(separator)[1];
        }

        if (uniqueGameIds.contains(game)) {
            return game;
        }

        for (HashMap.Entry<String, String> set : gameSynonyms.entrySet()) {
            double similarity = Utils.similarity(game, set.getKey());

            if (similarity > bestSimilarity) {
                result = set.getValue().split(separator)[1];
                bestSimilarity = similarity;
            }
        }

        if (bestSimilarity >= 0.85) {
            return result;
        } else {
            return game;
        }
    }

    public boolean determineGameNameBool(String game) {
        double bestSimilarity = -1;
        game = simplify(game);

        if (gameSynonyms.get(game) != null) {
            return true;
        }

        for (HashMap.Entry<String, String> set : gameSynonyms.entrySet()) {
            double similarity = Utils.similarity(game, set.getKey());

            if (similarity > bestSimilarity) {
                bestSimilarity = similarity;
            }
        }

        if (bestSimilarity >= 0.95) {
            return true;
        } else {
            return false;
        }
    }

    public ArrayList<Pair<String, String>> getCloseGames(String game) {
        ArrayList<Pair<String, String>> result = new ArrayList<>();
        ArrayList<Double> bestSimilarity = new ArrayList<>();

        for (int i = 0; i < 6; i++) {
            bestSimilarity.add(-1.0);
        }

        for (int i = 0; i < bestSimilarity.size(); i++) {
            result.add(Pair.of("", "IGNORE%" + separator + "THIS" + i + "%"));
        }

        game = simplify(game);

        for (HashMap.Entry<String, String> set : gameSynonyms.entrySet()) {
            double similarity = Utils.similarity(game, set.getKey());

            for (int i = bestSimilarity.size() - 2; i >= 0; i--) {
                if (similarity > bestSimilarity.get(i)) {
                    //if (i != bestSimilarity.length - 1) {
                    bestSimilarity.set(i + 1, bestSimilarity.get(i));
                    result.set(i + 1, result.get(i));
                    //}

                    bestSimilarity.set(i, similarity);
                    result.set(i, Pair.of(set.getKey(), set.getValue()));
                } else {
                    break;
                }
            }

            for (int i = 0; i < result.size(); i++) {
                for (int j = i + 1; j < result.size(); j++) {
                    if (result.get(i).getRight().split(separator)[1].equals(result.get(j).getRight().split(separator)[1])) {
                        result.remove(j);
                        result.add(Pair.of("", "IGNORE%" + separator + "THIS" + j + "%"));
                        bestSimilarity.remove(j);
                        bestSimilarity.add(-1.0);
                        i = result.size();
                        j = result.size();
                    }
                }
            }
        }

        result.remove(result.size() - 1);
        return result;
    }

    public static int getRunIndexFromUsername(Leaderboard lb, String name) {
        PlacedRun[] runs = lb.getRuns();
        int result = -1;
        double bestSimilarity = -1;
        
        for (int i = 0; i < runs.length; i++) {
            double similarity = 0;//similarity(name, playerArrayToString(runs[i].getRun().getPlayers()));
            playerArrayToString(runs[i].getRun().getPlayers());
            //double similarity = similarity("sigfried", "royal");
            if (similarity > bestSimilarity) {
                result = i;
                bestSimilarity = similarity;
            }
            
            if (i == 100) {
                System.out.println("stop here");
            }
        }
        
        return result;
    }
    
    public static ArrayList<Leaderboard> getTopLeaderboards(ArrayList<Leaderboard> leaderboards, int number) {
        leaderboards.sort((p1, p2) -> p2.getRuns().length - p1.getRuns().length);
        
        while (leaderboards.size() > number) {
            leaderboards.remove(leaderboards.size() - 1);
        }
        
        while (!leaderboards.isEmpty() && leaderboards.get(leaderboards.size() - 1).getRuns().length == 0) {
            leaderboards.remove(leaderboards.size() - 1);
        }
        
        return leaderboards;
    }
    
    public static String[] getRunnerStatsFromLeaderboard(Leaderboard lb, int total) {
        String[] result = new String[lb.getRuns().length >= total ? total : lb.getRuns().length];
        String[] placeTimes = new String[lb.getRuns().length >= total ? total : lb.getRuns().length];
        PlacedRun[] placedRuns = lb.getRuns();
        int longestPlaceTime = 0;

        for (int i = 0; i < result.length; i++) {
            Run run = placedRuns[i].getRun();
            placeTimes[i] = getNumericalSuffix(i);
            placeTimes[i] += (i + 1 < 10 ? "    " : "   ") + formatTime(run.getTimes().getPrimary());
            result[i] = playerArrayToString(run.getPlayers());

            if (placeTimes[i].length() > longestPlaceTime) {
                longestPlaceTime = placeTimes[i].length();
            }
        }

        for (int i = 0; i < placeTimes.length; i++) {
            while (placeTimes[i].length() < longestPlaceTime) {
                placeTimes[i] += " ";
            }

            result[i] = placeTimes[i] + "    " + result[i];
        }

        return result;
        //? maybe make links for each of em idk
    }

    public static String formatTime(String time) {
        String result = "";
        String str = time.replace("PT", "");
        String buffer = "";
        int hours = 0;
        int minutes = 0;
        double seconds = 0;

        for (int i = 0; i < str.length(); i++) {
            char character = str.charAt(i);

            switch (character) {
                case 'H':
                    hours = Integer.parseInt(buffer);
                    buffer = "";
                    break;
                case 'M':
                    minutes = Integer.parseInt(buffer);
                    buffer = "";
                    break;
                case 'S':
                    seconds = Double.parseDouble(buffer);
                    buffer = "";
                    break;
                default:
                    buffer += character;
                    break;
            }
        }

        if (hours == 0) {
            result += minutes + ":";
        } else {
            result += hours + ":";

            if (minutes / 10 < 1) {
                result += "0" + minutes + ":";
            } else {
                result += minutes + ":";
            }
        }

        if (seconds % 1 == 0) {
            if (seconds / 10 < 1) {
                result += "0" + (int) seconds;
            } else {
                result += (int) seconds;
            }
        } else {
            if (seconds / 10 < 1) {
                result += "0" + seconds;
            } else {
                result += seconds;
            }
        }

        return result;
    }

    public static String playerArrayToString(Player[] players) {
        StringBuilder result = new StringBuilder("");

        for (int i = 0; i < players.length; i++) {
            try {
                result.append(players[i].getName());

                if (i + 1 < players.length) {
                    result.append(", ");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return result.toString();
    }

    public static Game getGame(String name) {
        try {
            return Game.fromID(name);
        } catch (IOException e) {
            return null;
        }
    }
    
    public static Category getCategory(Game game, String name) {
        Category result = null;
        double bestSimilarity = -1;

        try {
            Category[] categories = game.getCategories().getCategories();
            
            for (Category category : categories) {
                double similarity = Utils.similarity(simplify(name), simplify(category.getName()));

                if (similarity > bestSimilarity) {
                    result = category;
                    bestSimilarity = similarity;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return result;
    }

    public static Leaderboard getLeaderboard(Category category) {
        try {
            return Leaderboard.forCategory(category);
        } catch (Exception e) {
            return null;
        }
    }

    public static String getFirstVideo(Run run) {
        String result = null;
        
        if (run.getVideos().getLinks().length > 0) {
            result = run.getVideos().getLinks()[0].getUri();
        }
        
        return result;
    }
    
    public static String getName(Map<String, String> names) {
        String result = null;
        boolean isJapanese = false;

        for (Map.Entry<String, String> name : names.entrySet()) {
            if (name.getKey().equals("international")) {
                result = name.getValue();
                isJapanese = false;
            } else if ((result == null || isJapanese) && name.getKey().equals("twitch")) {
                result = name.getValue();
                isJapanese = false;
            } else if (result == null) {
                result = name.getValue();
                isJapanese = true;
            }
        }

        return result;
    }

    public static String getNumericalSuffix(int index) {
        String result;
        int number = index + 1;
        result = number + "";

        if (11 <= number % 100 && number % 100 <= 14) {
            result += "th";
        } else if (number % 10 == 1) {
            result += "st";
        } else if (number % 10 == 2) {
            result += "nd";
        } else if (number % 10 == 3) {
            result += "rd";
        } else {
            result += "th";
        }

        return result;
    }
    
    public static String simplify(String str) {
        return str.toLowerCase().replaceAll("&", "and").replaceAll("\\+", "plus").replaceAll("[^-a-z0-9._]", "");
    }

    public static String capitalize(String string) {
        char[] chars = string.toLowerCase().toCharArray();
        boolean found = false;
        for (int i = 0; i < chars.length; i++) {
            if (!found && Character.isLetter(chars[i])) {
                chars[i] = Character.toUpperCase(chars[i]);
                found = true;
            } else if (Character.isWhitespace(chars[i]) || chars[i] == '.' || chars[i] == '\'') { // You can add other chars here
                found = false;
            }
        }
        return String.valueOf(chars);
    }

    public String getSeparator() {
        return separator;
    }
    
    public String getErrorUrl() {
        return errorUrl;
    }
    
    public static Color getEmbedColor() {
        return new Color(217, 159, 36);
    }
    
    public static Color getEmbedFailureColor() {
        return new Color(255, 85, 41);
    }
    
}
