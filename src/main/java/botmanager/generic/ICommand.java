package botmanager.generic;

import net.dv8tion.jda.api.events.Event;

/**
 *
 * @author MC_2018 <mc2018.git@gmail.com>
 */
public interface ICommand {
    
    public default void run(Event genericEvent) {
        throw new UnsupportedOperationException("This method was never implemented");
    }
    
}
