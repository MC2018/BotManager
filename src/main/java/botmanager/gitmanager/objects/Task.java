package botmanager.gitmanager.objects;

import java.time.Instant;
import java.util.ArrayList;

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
