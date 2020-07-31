package botmanager.gitmanager.objects;

import java.util.ArrayList;

/**
 *
 * @author MC_2018 <mc2018.git@gmail.com>
 */

public class GuildSettings {

    private class TaskChannel {
        
        private String channelName = "";
        private String reactionName = "";
        
        private TaskChannel(String channelName, String reactionName) {
            this.channelName = channelName;
            this.reactionName = reactionName;
        }
        
    }
    
    private ArrayList<TaskChannel> taskChannels = new ArrayList();
    private String oauthToken = "";
    private String repoOwnerName = "";
    private String repoName = "";
    private String prUpdateChannel = "pr-updates";
    private long id;
    private int defaultTaskChannelIndex = 0;
    
    public GuildSettings(long id) {
        this.id = id;
        taskChannels.add(new TaskChannel("to-do", "todo"));
        taskChannels.add(new TaskChannel("in-progress", "inprogress"));
        taskChannels.add(new TaskChannel("in-pr", "inpr"));
        taskChannels.add(new TaskChannel("completed", "completed"));
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

    public String getPrUpdateChannel() {
        return prUpdateChannel;
    }

    public void setPrUpdateChannel(String prUpdateChannel) {
        this.prUpdateChannel = prUpdateChannel;
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
    
}
