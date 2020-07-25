package botmanager.gitmanager.tasks;

import botmanager.generic.BotBase;
import java.time.Instant;
import java.util.ArrayList;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;

/**
 *
 * @author maxclausius
 */
public class Task {

    private String name;
    private String description;
    private long assignee;
    private long guildID;
    private long channelID;
    private long messageID;
    private long epochSecond;
    private int status;
    private int id;
    private ArrayList<UpdateLog> updateLogs;
    
    public Task(String name, String description, long assignee, long guildID, long channelID, long messageID, int status, int id) {
        this.name = name;
        this.description = description;
        this.assignee = assignee;
        this.guildID = guildID;
        this.channelID = channelID;
        this.messageID = messageID;
        this.status = status;
        this.id = id;
        epochSecond = Instant.now().getEpochSecond();
        updateLogs = new ArrayList();
    }

    public String getName() {
        return name;
    }

    public void setName(String name, long userID) {
        updateLogs.add(new UpdateLog("Updated name", this.name, name, userID));
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description, long userID) {
        updateLogs.add(new UpdateLog("Updated description", this.description, description, userID));
        this.description = description;
    }

    public long getAssignee() {
        return assignee;
    }

    public void setAssignee(long assignee, long userID) {
        updateLogs.add(new UpdateLog("Updated assignee", String.valueOf(this.assignee), String.valueOf(assignee), userID));
        this.assignee = assignee;
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

    public long getEpochSecond() {
        return epochSecond;
    }
    
    public int getStatus() {
        return status;
    }

    public void setStatus(int status, long userID) {
        updateLogs.add(new UpdateLog("Updated status", String.valueOf(this.status), String.valueOf(status), userID));
        this.status = status;
    }

    public int getID() {
        return id;
    }
    
    public static MessageEmbed generateMessageEmbed(Task task, BotBase bot) {
        EmbedBuilder eb = new EmbedBuilder();
        
        eb.setTitle("Task #" + task.getID() + ": " + task.getName());
        eb.addField("Description", task.getDescription(), false);
        
        if (task.getAssignee() > 0) {
            User user = bot.getJDA().getUserById(task.getAssignee());
            eb.addField("Assignee", user.getAsMention(), false);
            eb.setImage(user.getAvatarUrl());
        } else {
            eb.addField("Assignee", "TBD", false);
        }
        
        eb.setTimestamp(Instant.now());
        
        return eb.build();
    }
    
    public class UpdateLog {
        
        private String changeLog;
        private String before;
        private String after;
        private long userID;
        private long epochSecond;
        
        public UpdateLog(String changeLog, String before, String after, long userID) {
            this.changeLog = changeLog;
            this.before = before;
            this.after = after;
            this.userID = userID;
            epochSecond = Instant.now().getEpochSecond();
        }
        
    }
    
}
