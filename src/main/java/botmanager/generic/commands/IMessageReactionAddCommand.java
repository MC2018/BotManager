package botmanager.generic.commands;

import botmanager.generic.ICommand;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;

public interface IMessageReactionAddCommand extends ICommand {

    default void run(Event event) {
        if (event instanceof MessageReactionAddEvent) {
            runOnReactionAdd((MessageReactionAddEvent) event);
        }
    }

    void runOnReactionAdd(MessageReactionAddEvent event);

}
