package botmanager.bots.gitmanager.objects;

import botmanager.generic.BotBase;
import botmanager.utils.IOUtils;
import botmanager.utils.Utils;

import java.io.File;

/**
 *
 * @author MC_2018 <mc2018.git@gmail.com>
 */
public class LogBuilder {

    private BotBase bot;
    private LogType type = LogType.UNDECIDED_OTHER;
    private long author = -1;
    private long guildID = -1;
    private long channelID = -1;
    private long messageID = -1;
    private int minutes = -1;
    
    public LogBuilder(BotBase bot) {
        this.bot = bot;
    }
    
    public Log build() throws Exception {
        if (author < 0) {
            throw new Exception("Author is empty.");
        } else if (guildID < 0) {
            throw new Exception("Guild ID is empty.");
        } else if (minutes < 0) {
            throw new Exception("Minutes is empty.");
        }
        
        return new Log(type, author, guildID, channelID, messageID, minutes, generateID());
    }
    
    public BotBase getBot() {
        return bot;
    }
    
    public LogType getType() {
        return type;
    }
    
    public LogBuilder setType(LogType type) {
        this.type = type;
        return this;
    }

    public long getAuthor() {
        return author;
    }
    
    public LogBuilder setAuthor(long author) {
        this.author = author;
        return this;
    }
    
    public long getGuildID() {
        return guildID;
    }
    
    public LogBuilder setGuildID(long guildID) {
        this.guildID = guildID;
        return this;
    }
    
    public long getChannelID() {
        return channelID;
    }
    
    public LogBuilder setChannelID(long channelID) {
        this.channelID = channelID;
        return this;
    }
    
    public long getMessageID() {
        return messageID;
    }
    
    public LogBuilder setMessageID(long messageID) {
        this.messageID = messageID;
        return this;
    }

    public LogBuilder setMinutes(int minutes) {
        this.minutes = minutes;
        return this;
    }

    public static File getCounterFile(BotBase bot, long guildID) {
        return new File("data/" + bot.getName() + "/guilds/" + guildID + "/log_counter.json");
    }
    
    private int generateID() {
        File taskCounterFile = getCounterFile(bot, guildID);
        Integer number = IOUtils.readGson(taskCounterFile, Integer.class);
        int counter = 0;
        
        if (number != null) {
            try {
                counter = number;
            } catch (Exception e) {
            }
        }
        
        IOUtils.writeGson(taskCounterFile, ++counter);
        return counter;
    }
    
}
