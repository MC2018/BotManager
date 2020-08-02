package botmanager.gitmanager.objects;

import botmanager.generic.BotBase;
import java.io.File;
import java.util.ArrayList;

/**
 *
 * @author MC_2018 <mc2018.git@gmail.com>
 */

public class GuildSettings {

    private ArrayList<TaskChannel> taskChannels = new ArrayList();
    private String oauthToken = "";
    private String repoOwnerName = "";
    private String repoName = "";
    private String prAnnouncementChannel = "pr-announcements";
    private String meetingAnnouncementChannel = "general";
    private long id;
    private int defaultTaskChannelIndex = 0;
    
    public GuildSettings(long id) {
        this.id = id;
        taskChannels.add(new TaskChannel("to-do", "todo"));
        taskChannels.add(new TaskChannel("in-progress", "inprogress"));
        taskChannels.add(new TaskChannel("in-pr", "inpr"));
        taskChannels.add(new TaskChannel("completed", "completed"));
    }

    public static File getFile(BotBase bot, long guildID) {
        return new File("data/" + bot.getName() + "/guilds/" + guildID + "/settings.json");
    }
    
    public ArrayList<String> getTaskChannelNames() {
        ArrayList<String> taskChannelNames = new ArrayList();
        
        for (TaskChannel taskChannel : taskChannels) {
            taskChannelNames.add(taskChannel.channelName);
        }
        
        return taskChannelNames;
    }
    
    public ArrayList<String> getTaskReactionNames() {
        ArrayList<String> taskReactionNames = new ArrayList();
        
        for (TaskChannel taskChannel : taskChannels) {
            taskReactionNames.add(taskChannel.reactionName);
        }
        
        return taskReactionNames;
    }
    
    public String getOAuthToken() {
        return oauthToken;
    }

    public void setOAuthToken(String oauthToken) {
        this.oauthToken = oauthToken;
    }

    public String getRepoOwnerName() {
        return repoOwnerName;
    }

    public void setRepoOwnerName(String repoOwnerName) {
        this.repoOwnerName = repoOwnerName;
    }

    public String getRepoName() {
        return repoName;
    }

    public void setRepoName(String repoName) {
        this.repoName = repoName;
    }

    public String getPrAnnouncementChannel() {
        return prAnnouncementChannel;
    }

    public void setPrAnnouncementChannel(String prAnnouncementChannel) {
        this.prAnnouncementChannel = prAnnouncementChannel;
    }

    public long getID() {
        return id;
    }

    public void setID(long id) {
        this.id = id;
    }

    public int getDefaultTaskChannelIndex() {
        return defaultTaskChannelIndex;
    }
    
    
    private class TaskChannel {
        
        private String channelName = "";
        private String reactionName = "";
        
        private TaskChannel(String channelName, String reactionName) {
            this.channelName = channelName;
            this.reactionName = reactionName;
        }
        
    }
    
}
