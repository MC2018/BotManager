package botmanager.bots.gitmanager.objects;

import botmanager.utils.IOUtils;
import botmanager.utils.Utils;
import botmanager.generic.BotBase;
import java.io.File;
import java.io.IOException;

/**
 *
 * @author MC_2018 <mc2018.git@gmail.com>
 */
public class TaskBuilder {

    private BotBase bot;
    private String name;
    private String description = "TBD";
    private long author = -1;
    private long assignee = -1;
    private long guildID = -1;
    private long channelID = -1;
    private long messageID = -1;
    private int status = 0;
    
    public TaskBuilder(BotBase bot) {
        this.bot = bot;
    }
    
    public Task build() throws Exception {
        if (Utils.isNullOrEmpty(name)) {
            throw new Exception("Name is empty.");
        } else if (author < 0) {
            throw new Exception("Author is empty.");
        }else if (guildID < 0) {
            throw new Exception("Guild ID is empty.");
        }
        
        return new Task(name, description, author, assignee, guildID, channelID, messageID, status, generateID());
    }
    
    public BotBase getBot() {
        return bot;
    }
    
    public String getName() {
        return name;
    }
    
    public TaskBuilder setName(String name) {
        this.name = name;
        return this;
    }
    
    public String getDescription() {
        return description;
    }
    
    public TaskBuilder setDescription(String description) {
        this.description = description;
        return this;
    }
    
    public long getAuthor() {
        return author;
    }
    
    public TaskBuilder setAuthor(long author) {
        this.author = author;
        return this;
    }
    
    public long getAssignee() {
        return assignee;
    }
    
    public TaskBuilder setAssignee(long assignee) {
        this.assignee = assignee;
        return this;
    }
    
    public long getGuildID() {
        return guildID;
    }
    
    public TaskBuilder setGuildID(long guildID) {
        this.guildID = guildID;
        return this;
    }
    
    public long getChannelID() {
        return channelID;
    }
    
    public TaskBuilder setChannelID(long channelID) {
        this.channelID = channelID;
        return this;
    }
    
    public long getMessageID() {
        return messageID;
    }
    
    public TaskBuilder setMessageID(long messageID) {
        this.messageID = messageID;
        return this;
    }
    
    public int getStatus() {
        return status;
    }
    
    public TaskBuilder setStatus(int status) {
        this.status = status;
        return this;
    }

    public static File getCounterFile(BotBase bot, long guildID) {
        return new File("data/" + bot.getName() + "/guilds/" + guildID + "/task_counter.json");
    }
    
    private int generateID() {
        try {
            File taskCounterFile = getCounterFile(bot, guildID);
            Integer counter = IOUtils.readGson(taskCounterFile, Integer.class);
            IOUtils.writeGson(taskCounterFile, ++counter);
            return counter;
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }
    }
    
}
