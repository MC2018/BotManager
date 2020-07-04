package botmanager.nsfwpolice.generic;

import botmanager.nsfwpolice.NSFWPolice;
import botmanager.generic.ICommand;

/**
 *
 * @author MC_2018 <mc2018.git@gmail.com>
 */

public abstract class NSFWPoliceCommandBase implements ICommand {

    protected NSFWPolice bot;
    
    public NSFWPoliceCommandBase(NSFWPolice bot) {
        this.bot = (NSFWPolice) bot;
    }
    
    public abstract String info();
    
    public NSFWPolice getBot() {
        return bot;
    }
    
}
