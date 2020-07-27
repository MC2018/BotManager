package botmanager.gitmanager;

/**
 *
 * @author MC_2018 <mc2018.git@gmail.com>
 */

public class User {

    private long id;
    private long defaultGuild = -1;
    
    public User(long id) {
        this.id = id;
    }
    
    public User(long id, long defaultGuild) {
        this(id);
        this.defaultGuild = defaultGuild;
    }
    
}
