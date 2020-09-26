package botmanager.generic.commands;

import botmanager.generic.ICommand;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public interface IMessageReceivedCommand extends ICommand {

    default void run(Event event) {
        if (event instanceof MessageReceivedEvent) {
            runOnMessage((MessageReceivedEvent) event);
        }
    }

    void runOnMessage(MessageReceivedEvent event);

}
