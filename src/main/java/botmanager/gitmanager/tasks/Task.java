package botmanager.gitmanager.tasks;

import botmanager.gitmanager.GitManager;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;

/**
 *
 * @author MC_2018 <mc2018.git@gmail.com>
 */
public class Task {

    private String title;
    private String description;
    private long assignee;
    private long guildID;
    private long channelID;
    private long messageID;
    private long epochMilli;
    private int status;
    private int id;
    private ArrayList<UpdateLog> updateLogs;
    
    public Task(String title, String description, long assignee, long guildID, long channelID, long messageID, int status, int id) {
        this.title = title;
        this.description = description;
        this.assignee = assignee;
        this.guildID = guildID;
        this.channelID = channelID;
        this.messageID = messageID;
        this.status = status;
        this.id = id;
        this.epochMilli = Instant.now().toEpochMilli();
        updateLogs = new ArrayList();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String name, long userID) {
        updateLogs.add(new UpdateLog("Updated name", this.title, title, userID));
        this.title = title;
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

    public long getEpochMilli() {
        return epochMilli;
    }
    
    public int getStatus() {
        return status;
    }

    public void updateStatus(int status, long channelID, long messageID, long userID) {
        updateLogs.add(new UpdateLog("Updated status", String.valueOf(this.status), String.valueOf(status), userID));
        updateLogs.add(new UpdateLog("Updated channelID", String.valueOf(this.channelID), String.valueOf(channelID), userID));
        updateLogs.add(new UpdateLog("Updated messageID", String.valueOf(this.messageID), String.valueOf(messageID), userID));
        this.status = status;
        this.channelID = channelID;
        this.messageID = messageID;
    }
    
    public int getID() {
        return id;
    }
    
    public static MessageEmbed generateTaskEmbed(Task task, GitManager bot) {
        EmbedBuilder eb = new EmbedBuilder();
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        boolean assigneeTBD = task.getAssignee() <= 0;
        
        eb.setTitle("Task #" + task.getID() + ": " + task.getTitle());
        eb.addField("Description", task.getDescription(), false);
        
        if (assigneeTBD) {
            eb.addField("Assignee", "TBD\u200B \u200B \u200B \u200B \u200B \u200B \u200B \u200B \u200B \u200B \u200B Click :red_circle: to be assigned/unassigned", false);
        } else {
            User user = bot.getJDA().getUserById(task.getAssignee());
            eb.addField("Assignee", user.getAsMention(), false);
            eb.setThumbnail(user.getAvatarUrl());
        }
        
        eb.appendDescription("Last Updated " + sdf.format(new Date(task.getEpochMilli())));
        eb.setTimestamp(Instant.now());
        return eb.build();
    }
    
    public class UpdateLog {
        
        private String changeLog;
        private String before;
        private String after;
        private long userID;
        private long epochMilli;
        
        public UpdateLog(String changeLog, String before, String after, long userID) {
            this.changeLog = changeLog;
            this.before = before;
            this.after = after;
            this.userID = userID;
            epochMilli = Instant.now().toEpochMilli();
        }
        
    }
    
}
