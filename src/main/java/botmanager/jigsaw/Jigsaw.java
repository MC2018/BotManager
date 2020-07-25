package botmanager.jigsaw;

import botmanager.JDAUtils;
import botmanager.jigsaw.commands.WordTrackerCommand;
import botmanager.IOUtils;
import botmanager.Utils;
import botmanager.generic.BotBase;
import botmanager.generic.ICommand;
import botmanager.generic.commands.PMRepeaterCommand;
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

/**
 *
 * @author MC_2018 <mc2018.git@gmail.com>
 */
public class Jigsaw extends BotBase {

    //make change on all bots to store guild information under "guilds" directory, maybe "members" or "users" too
    
    List<String> dirtyWords;
    TimerTask banTimerTask;
    Timer banTimer = new Timer();
    
    public Jigsaw(String botToken, String name) {
        super(botToken, name);
        getJDA().getPresence().setActivity(Activity.playing("with your mind"));
        generateDirtyWords();
        generateBanTimer();
        setCommands(new ICommand[] {
            new WordTrackerCommand(this),
            new PMRepeaterCommand(this)
        });
        
        System.out.println(IOUtils.getTrueFileName(new File("data/bbbots_tokens.txt")));
    }
    
    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        for (ICommand command : getCommands()) {
            command.run(event);
        }
    }
    
    public String getUserCSVAtIndex(Guild guild, User user, int index) {
        File file = new File("data/" + getName() + "/guilds/" + guild.getId() + "/members/" + user.getId() + ".csv");

        if (!file.exists()) {
            return "";
        }

        return Utils.getCSVValueAtIndex(IOUtils.read(file), index);
    }

    public void setUserCSVAtIndex(Guild guild, User user, int index, String newValue) {
        File file = new File("data/" + getName() + "/guilds/" + guild.getId() + "/members/" + user.getId() + ".csv");
        String data = IOUtils.read(file);
        String[] originalValues = data.split(",");
        String[] newValues;

        if (originalValues.length > index) {
            newValues = data.split(",");
        } else {
            newValues = new String[index + 1];
            System.arraycopy(originalValues, 0, newValues, 0, originalValues.length);

            for (int i = originalValues.length; i < newValues.length; i++) {
                newValues[i] = "";
            }
        }
        
        newValues[index] = newValue;
        IOUtils.write(file, Utils.buildCSV(newValues));
    }
    
    public int getUserDirtyWords(Guild guild, User user) {
        try {
            return Integer.parseInt(getUserCSVAtIndex(guild, user, 0));
        } catch (NumberFormatException e) {
            return 0;
        }
    }
    
    public void setUserDirtyWords(Guild guild, User user, int amount) {
        setUserCSVAtIndex(guild, user, 0, String.valueOf(amount));
    }
    
    public void incrementUserDirtyWords(Guild guild, User user) {
        int dirtyWordCount = getUserDirtyWords(guild, user);
        
        if (dirtyWordCount == -1) {
            dirtyWordCount++;
        }
        
        setUserDirtyWords(guild, user, dirtyWordCount + 1);
    }
    
    public void generateDirtyWords() {
        dirtyWords = IOUtils.readLines(new File("data/" + getName() + "/dirty_words.txt"));
        
        if (dirtyWords != null) {
            dirtyWords.forEach(x -> x = x.toLowerCase());
        } else {
            dirtyWords = new ArrayList();
        }
    }
    
    public List<String> getDirtyWords() {
        return dirtyWords;
    }
    
    public void generateBanTimer() {
        banTimerTask = new TimerTask() {
            // 71200
            DateFormat dateFormatter = new SimpleDateFormat("uHHmm");
            
            @Override
            public void run() {
                String formattedDate = dateFormatter.format(new Date());
                
                if (formattedDate.equals("71200")) {
                    unbanPrevious();
                    randomBans();
                }
            }
        };
        
        banTimer.schedule(banTimerTask, 0, 60000);
    }
    
    public File[] getGuildFolders() {
        File[] dataFiles = new File("data/" + getName() + "/guilds/").listFiles();
        List<File> guildFolders = new ArrayList();
        File[] array;
        
        for (File dataFile : dataFiles) {
            if (dataFile.isDirectory()) {
                String folderName = dataFile.getName();
                
                try {
                    Long.parseLong(folderName);
                    guildFolders.add(dataFile);
                } catch (Exception e) {
                }
            }
        }
        
        array = new File[guildFolders.size()];
        guildFolders.toArray(array);
        return array;
    }
    
    public List<UserData> getAllUserDirtyWords(File guildFolder) {
        File[] guildFiles = guildFolder.listFiles();
        List<UserData> result = new ArrayList();
        Guild guild = getJDA().getGuildById(IOUtils.getTrueFileName(guildFolder));
        
        for (File potentialUserFile : guildFiles) {
            try {
                String potentialID = IOUtils.getTrueFileName(potentialUserFile);
                User user;
                
                Integer.parseInt(potentialID);
                user = getJDA().getUserById(potentialID);
                result.add(new UserData(potentialID, getUserDirtyWords(guild, user)));
            } catch (Exception e) {
            }
        }
        
        return result;
    }
    
    public void randomBans() {
        File[] guildFolders = getGuildFolders();
        
        for (File guildFolder : guildFolders) {
            Guild guild = getJDA().getGuildById(guildFolder.getName());
            String userID = IOUtils.read(new File(guildFolder.getAbsolutePath() + "/temp_banned.txt"));
            
            if (!userID.isEmpty()) {
                User user = getJDA().getUserById(userID);
                TextChannel channel = (TextChannel) JDAUtils.findChannelByName(guild, "action-logs");
                
                guild.unban(user).complete();
                JDAUtils.sendPrivateMessage(user, "You are now unbanned from the " + guild.getName() + " server.");
                
                if (channel != null) {
                    JDAUtils.sendGuildMessage(channel, "Unbanned user " + user.getName() + "#" + user.getDiscriminator() + ", ID " + user.getId());
                }
            }
        }
    }
    
    public void unbanPrevious() {
        File[] guildFolders = getGuildFolders();
        
        for (File guildFolder : guildFolders) {
            String userID = IOUtils.read(new File(guildFolder.getAbsolutePath() + "/temp_banned.txt"));
            
            if (!userID.isEmpty()) {
                Guild guild = getJDA().getGuildById(IOUtils.getTrueFileName(guildFolder));
                User user = getJDA().getUserById(userID);
                TextChannel channel = (TextChannel) JDAUtils.findChannelByName(guild, "action-logs");
                
                guild.unban(user).complete();
                JDAUtils.sendPrivateMessage(user, "You are now unbanned from the " + guild.getName() + " server.");
                
                if (channel != null) {
                    JDAUtils.sendGuildMessage(channel, "Unbanned user " + user.getName() + "#" + user.getDiscriminator() + ", ID " + user.getId());
                }
            }
        }
    }
    
    public class UserData {
        
        String id;
        int dirtyWordCount;
        
        public UserData(String id, int dirtyWordCount) {
            this.id = id;
            this.dirtyWordCount = dirtyWordCount;
        }
        
    }
    
}
