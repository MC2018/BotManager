package botmanager.generic.commands;

import botmanager.generic.ICommand;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public interface IGuildMessageReceivedCommand extends ICommand {

    default void run(Event event) {
        if (event instanceof GuildMessageReceivedEvent) {
            runOnGuildMessage((GuildMessageReceivedEvent) event);
        }
    }

    void runOnGuildMessage(GuildMessageReceivedEvent event);

}
