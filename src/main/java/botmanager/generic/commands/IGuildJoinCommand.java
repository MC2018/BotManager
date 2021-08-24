package botmanager.generic.commands;

import botmanager.generic.ICommand;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;

public interface IGuildJoinCommand extends ICommand {

    default void run(Event event) {
        if (event instanceof GuildJoinEvent) {
            runOnGuildJoin((GuildJoinEvent) event);
        }
    }

    void runOnGuildJoin(GuildJoinEvent event);

}
