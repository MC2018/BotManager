package botmanager.generic.commands;

import botmanager.generic.ICommand;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.guild.GuildBanEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public interface IGuildBanCommand extends ICommand {

    default void run(Event event) {
        if (event instanceof GuildBanEvent) {
            runOnGuildBan((GuildBanEvent) event);
        }
    }

    void runOnGuildBan(GuildBanEvent event);

}
