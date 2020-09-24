package botmanager.bots.gitmanager.objects;

import botmanager.generic.BotBase;
import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;

/**
 *
 * @author MC_2018 <mc2018.git@gmail.com>
 */

public class GuildSettings {

    private ArrayList<Meeting> meetings = new ArrayList();
    private ArrayList<String> dateFormats = new ArrayList();
    private ArrayList<TaskChannel> taskChannels = new ArrayList();
    private String oauthToken = "";
    private String repoOwnerName = "";
    private String repoName = "";
    private String prAnnouncementChannel = "pr-announcements";
    private String meetingAnnouncementChannel = "general";
    private String bumpReactionName = "";
    private String logChannel = "work-logs";
    private long id;
    private int defaultTaskChannelIndex = 0;
    
    public GuildSettings(long id) {
        this.id = id;
        taskChannels.add(new TaskChannel("to-do", "todo"));
        taskChannels.add(new TaskChannel("in-progress", "inprogress"));
        taskChannels.add(new TaskChannel("in-pr", "inpr"));
        taskChannels.add(new TaskChannel("completed", "completed"));
        dateFormats.add("M/d/yyyy h:mma");
        dateFormats.add("M/d/yyyy ha");
        dateFormats.add("M/d/yy h:mma");
        dateFormats.add("M/d/yy ha");
    }

    public void clean() {
        if (meetings == null) {
            meetings = new ArrayList();
        }
        
        if (dateFormats == null) {
            dateFormats = new ArrayList();
        }
        
        if (taskChannels == null) {
            taskChannels = new ArrayList();
        }
    }
    
    public static File getFile(BotBase bot, long guildID) {
        return new File("data/" + bot.getName() + "/guilds/" + guildID + "/settings.json");
    }
    
    public ArrayList<Meeting> getMeetings() {
        return meetings;
    }
    
    public ArrayList<String> getDateFormats() {
        return dateFormats;
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

    public String getLogChannel() {
        return logChannel;
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
    
    public String getMeetingAnnouncementChannel() {
        return meetingAnnouncementChannel;
    }

    public void setMeetingAnnouncementChannel(String meetingAnnouncementChannel) {
        this.meetingAnnouncementChannel = meetingAnnouncementChannel;
    }

    public String getBumpReactionName() {
        return bumpReactionName;
    }

    public void setBumpReactionName(String bumpReactionName) {
        this.bumpReactionName = bumpReactionName;
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
    
    public Meeting getMeetingAtIndex(int index) {
        return meetings.get(index);
    }
    
    public void addMeeting(Date date) {
        meetings.add(new Meeting(date));
        meetings.sort(Comparator.comparing(Meeting::getDate));
    }
    
    public void removeMeeting(Date date) {
        meetings.remove(new Meeting(date));
    }
    
    public void replaceMeeting(Date date) {
        removeMeeting(date);
        addMeeting(date);
    }

    public int getMeetingIndexAtDate(Date date) {
        for (int i = 0; i < meetings.size(); i++) {
            if (meetings.get(i).getDate().equals(date)) {
                return i;
            }
        }
        
        return -1;
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
