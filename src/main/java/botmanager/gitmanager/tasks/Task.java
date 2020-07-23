package botmanager.gitmanager.tasks;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

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
    private int status;
    private int id;
    
    public Task(String name, String description, long assignee, long guildID, long channelID, long messageID, int status, int id) {
        this.name = name;
        this.description = description;
        this.assignee = assignee;
        this.guildID = guildID;
        this.channelID = channelID;
        this.messageID = messageID;
        this.status = status;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getAssignee() {
        return assignee;
    }

    public void setAssignee(long assignee) {
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

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getID() {
        return id;
    }

    public void setID(int id) {
        this.id = id;
    }
    
    public static MessageEmbed generateMessageEmbed(Task task) {
        EmbedBuilder eb = new EmbedBuilder();
        
        eb.setTitle("Task: " + task.getName());
        eb.addField("Task Number " + task.getID(), "", true);
        eb.addField("Description", task.getDescription(), true);
        return eb.build();
    }
    
}
