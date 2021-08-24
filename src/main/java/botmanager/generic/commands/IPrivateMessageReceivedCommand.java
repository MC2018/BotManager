package botmanager.generic.commands;

import botmanager.generic.ICommand;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;

public interface IPrivateMessageReceivedCommand extends ICommand {

    default void run(Event event) {
        if (event instanceof PrivateMessageReceivedEvent) {
            runOnPrivateMessage((PrivateMessageReceivedEvent) event);
        }
    }

    void runOnPrivateMessage(PrivateMessageReceivedEvent event);

}
