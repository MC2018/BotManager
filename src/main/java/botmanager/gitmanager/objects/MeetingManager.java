package botmanager.gitmanager.objects;

import botmanager.gitmanager.GitManager;
import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;

/**
 *
 * @author MC_2018 <mc2018.git@gmail.com>
 */

public class MeetingManager {
    
    private ArrayList<Meeting> meetings = new ArrayList();
    private ArrayList<Meeting> pastMeetings = new ArrayList();
    private ArrayList<String> dateFormats = new ArrayList();
    private long guildID;
    
    public class Meeting {
        
        private Date date;
        private String description;

        public Meeting(Date date) {
            this.date = date;
        }
        
        @Override
        public boolean equals(Object object) {
            try {
                Meeting meeting = (Meeting) object;
                return date.equals(meeting.date);
            } catch (Exception e) {
                return false;
            }
        }
        
        private Date getDate() {
            return date;
        }
        
    }
    
    public MeetingManager(long guildID) {
        this.guildID = guildID;
        dateFormats.add("M/d/yy ha");
        dateFormats.add("M/d/yy h:mma");
        dateFormats.add("M/d/yyyy ha");
        dateFormats.add("M/d/yyyy h:mma");
    }
    
    
    public static File getFile(GitManager bot, long guildID) {
        return new File("data/" + bot.getName() + "/guilds/" + guildID + "/meeting_manager.json");
    }

    public ArrayList<Meeting> getMeetings() {
        return meetings;
    }

    public Meeting getMeetingAtIndex(int index) {
        return meetings.get(index);
    }
    
    public void setMeetingDescriptionAtIndex(String description, int index) {
        meetings.get(index).description = description;
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

    public ArrayList<String> getDateFormats() {
        return dateFormats;
    }

    public void setDateFormats(ArrayList<String> dateFormats) {
        this.dateFormats = dateFormats;
    }

    public long getGuildID() {
        return guildID;
    }

    public void setGuildID(long guildID) {
        this.guildID = guildID;
    }
    
    
}
