package botmanager.generic;

import net.dv8tion.jda.api.events.Event;

/**
 *
 * @author MC_2018 <mc2018.git@gmail.com>
 */
public abstract interface ICommand {
    
    public abstract void run(Event event);
    
}
