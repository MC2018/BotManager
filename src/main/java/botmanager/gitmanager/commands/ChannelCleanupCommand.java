package botmanager.gitmanager.commands;

import botmanager.gitmanager.GitManager;
import botmanager.gitmanager.generic.GitManagerCommandBase;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

/**
 *
 * @author MC_2018 <mc2018.git@gmail.com>
 */

public class ChannelCleanupCommand extends GitManagerCommandBase {

    public ChannelCleanupCommand(GitManager bot) {
        super(bot);
    }

    @Override
    public void run(Event genericEvent) {
        GuildMessageReceivedEvent event;

        if (!(genericEvent instanceof GuildMessageReceivedEvent)) {
            return;
        }

        event = (GuildMessageReceivedEvent) genericEvent;
        
        if (bot.isTaskChannel(event.getChannel()) && !event.getAuthor().isBot()) {
            event.getMessage().delete().queue();
        }
    }
    
    @Override
    public MessageEmbed.Field info() {
        return new MessageEmbed.Field("", "Any commands sent in the server will be deleted for the sake of cleanliness.", false);
    }

    @Override
    public MessageEmbed getFailureEmbed() {
        return null;
    }

}
