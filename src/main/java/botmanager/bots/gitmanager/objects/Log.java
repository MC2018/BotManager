package botmanager.bots.gitmanager.objects;

import botmanager.generic.BotBase;
import java.io.File;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;

/**
 *
 * @author MC_2018 <mc2018.git@gmail.com>
 */
public class Log {

    private LogType type;
    private long author;
    private long guildID;
    private long channelID;
    private long messageID;
    private long epochMilli;
    private long id;
    private int minutes;

    public Log(LogType type, long author, long guildID, long channelID, long messageID, int minutes, long id) {
        this.type = type;
        this.author = author;
        this.guildID = guildID;
        this.channelID = channelID;
        this.messageID = messageID;
        this.epochMilli = Instant.now().toEpochMilli();
        this.id = id;
        this.minutes = minutes;
    }

    public static File getFile(BotBase bot, long guildID, long logID) {
        return new File("data/" + bot.getName() + "/guilds/" + guildID + "/logs/" + logID + ".json");
    }

    public LogType getType() {
        return type;
    }

    public void setType(LogType type) {
        this.type = type;
    }

    public long getAuthor() {
        return author;
    }

    public long getGuildID() {
        return guildID;
    }

    public void setGuildID(long guildID) {
        this.guildID = guildID;
    }

    public long getChannelID() {
        return channelID;
    }

    public void setChannelID(long channelID) {
        this.channelID = channelID;
    }

    public long getMessageID() {
        return messageID;
    }

    public void setMessageID(long messageID) {
        this.messageID = messageID;
    }

    public long getEpochMilli() {
        return epochMilli;
    }

    public long getID() {
        return id;
    }

    public int getMinutes() {
        return minutes;
    }

}
