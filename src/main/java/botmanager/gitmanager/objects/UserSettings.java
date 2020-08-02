package botmanager.gitmanager.objects;

import botmanager.generic.BotBase;
import java.io.File;

/**
 *
 * @author MC_2018 <mc2018.git@gmail.com>
 */

public class UserSettings {

    private long id;
    private long defaultGuildID = -1;
    
    public UserSettings(long id, long defaultGuildID) {
        this.id = id;
        this.defaultGuildID = defaultGuildID;
    }
    
    public static File getFile(BotBase bot, long userID) {
        return new File("data/" + bot.getName() + "/users/" + userID + "/settings.json");
    }
    
    public long getID() {
        return id;
    }

    public void setID(long id) {
        this.id = id;
    }

    public long getDefaultGuildID() {
        return defaultGuildID;
    }
    
    public void setDefaultGuildID(long defaultGuildID) {
        this.defaultGuildID = defaultGuildID;
    }
    
}
