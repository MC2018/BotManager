package botmanager.generic.commands;

import botmanager.generic.ICommand;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent;

public interface IMessageReactionRemoveCommand extends ICommand {

    default void run(Event event) {
        if (event instanceof MessageReactionRemoveEvent) {
            runOnReactionRemove((MessageReactionRemoveEvent) event);
        }
    }

    void runOnReactionRemove(MessageReactionRemoveEvent event);

}
