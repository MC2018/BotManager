package botmanager.gitmanager.objects;

import java.util.Date;

/**
 *
 * @author MC_2018 <mc2018.git@gmail.com>
 */
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

    public Date getDate() {
        return date;
    }

    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }

}
