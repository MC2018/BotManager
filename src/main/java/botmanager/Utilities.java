package botmanager;

import static botmanager.speedrunbot.SpeedrunBot.simplify;
import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.vdurmont.emoji.Emoji;
import com.vdurmont.emoji.EmojiManager;
import com.vdurmont.emoji.EmojiParser;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.guild.GenericGuildEvent;

/**
 *
 * @author MC_2018 <mc2018.git@gmail.com>
 */
public class Utilities {

    //IO begin
    public static String read(File file) {
        StringBuilder result = new StringBuilder("");

        try {
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            String buffer;

            while ((buffer = br.readLine()) != null) {
                result.append(buffer);
            }

            br.close();
            fr.close();
            return result.toString();
        } catch (IOException e) {
            return result.toString();
        }
    }

    public static List<String> readLines(File file) {
        try {
            return Files.readLines(file, Charsets.UTF_8);
        } catch (Exception e) {
            return null;
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

    //IO end
    //Discord Begin
    public static String findUserId(Guild guild, String potentialName) {
        ArrayList<Member> names = new ArrayList<>();
        User potentialUser;

        names.addAll(guild.getMembersByName(potentialName, true));
        names.addAll(guild.getMembersByEffectiveName(potentialName, true));
        names.addAll(guild.getMembersByNickname(potentialName, true));

        if (names.size() > 0) {
            return names.get(0).getUser().getId();
        }

        try {
            potentialUser = guild.getJDA().getUserById(potentialName);

            if (potentialUser != null) {
                return potentialUser.getId();
            }
        } catch (Exception e) {

        }

        return null;
    }

    public static void sendGuildMessage(TextChannel channel, String message) {
        if (message.length() > 1950) {
            throw new RuntimeException("Message attempted to send too long:\n" + message);
        }

        channel.sendMessage(message).queue();
    }

    public static void sendGuildMessage(TextChannel channel, MessageEmbed me) {
        channel.sendMessage(me).queue();
    }

    public static Message sendGuildMessageReturn(TextChannel channel, String message) {
        if (message.length() >= Message.MAX_CONTENT_LENGTH) {
            throw new RuntimeException("Message attempted to send too long:\n" + message);
        }

        return channel.sendMessage(message).complete();
    }

    public static Message sendGuildMessageReturn(TextChannel channel, MessageEmbed me) {
        return channel.sendMessage(me).complete();
    }

    public static void sendGuildMessageWithReactions(TextChannel channel, String message, String[] reactionNames) {
        Message sentMessage;

        if (message.length() > 1950) {
            throw new RuntimeException("Message attempted to send too long:\n" + message);
        }

        sentMessage = channel.sendMessage(message).complete();

        for (String reactionName : reactionNames) {
            Utilities.addReaction(sentMessage, reactionName);
        }
    }

    public static void sendGuildMessageWithReactions(TextChannel channel, String message, String[] reactionNames, File file) {
        Message sentMessage;

        if (message.length() > 1950) {
            throw new RuntimeException("Message attempted to send too long:\n" + message);
        }

        sentMessage = channel.sendMessage(message).addFile(file).complete();

        for (String reactionName : reactionNames) {
            Utilities.addReaction(sentMessage, reactionName);
        }
    }

    public static void sendPrivateMessage(User user, String message) {
        if (message.length() > 1950) {
            throw new RuntimeException("Message attempted to send too long:\n" + message);
        }

        user.openPrivateChannel().queue((channel) -> channel.sendMessage(message).queue());
    }

    public static void sendPrivateMessage(User user, MessageEmbed message) {
        user.openPrivateChannel().queue((channel) -> channel.sendMessage(message).queue());
    }

    public static void sendPrivateMessageWithReactions(TextChannel channel, String message, String[] reactionNames) {
        Message sentMessage;

        if (message.length() > 1950) {
            throw new RuntimeException("Message attempted to send too long:\n" + message);
        }

        sentMessage = channel.sendMessage(message).complete();

        for (String reactionName : reactionNames) {
            Utilities.addReaction(sentMessage, reactionName);
        }
    }

    public static Role getRole(GenericGuildEvent event, String roleName) {
        List<Role> roles = event.getGuild().getRoles();

        for (Role role : roles) {
            if (role.getName().equals(roleName)) {
                return role;
            }
        }

        return null;
    }

    public static boolean hasRole(Member member, String roleName) {
        try {
            return member.getRoles().stream().anyMatch((role) -> (role.getName().equals(roleName)));
        } catch (Exception e) {
            return false;
        }
    }

    public static GuildChannel findChannelByName(Guild guild, String name) {
        List<GuildChannel> channels = guild.getChannels();

        for (GuildChannel channel : channels) {
            if (channel.getName().equalsIgnoreCase(name)) {
                return channel;
            }
        }

        return null;
    }

    public static String getFormattedUserTimeJoined(Member member, String dateFormat) {
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        Date date = Date.from(member.getTimeJoined().toInstant());
        return sdf.format(date);
    }

    public static String getFormattedUserTimeCreated(User user, String dateFormat) {
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        Date date = Date.from(user.getTimeCreated().toInstant());
        return sdf.format(date);
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
    
    public static void addReaction(Message message, String potentialName) {
        List<Emote> emotes = message.getGuild().getEmotesByName(potentialName, true);
        Emote emote;

        if (!emotes.isEmpty()) {
            emote = emotes.get(0);
            message.addReaction(emote).queue();
        } else {
            message.addReaction(getEmoji(potentialName).getUnicode()).queue();
        }
    }

    public static String bestSimilarity(List<String> collection, String phrase) {
        String result = null;
        double bestSimilarity = -1;

        for (String s1 : collection) {
            double similarity = similarity(simplify(s1), phrase);

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
