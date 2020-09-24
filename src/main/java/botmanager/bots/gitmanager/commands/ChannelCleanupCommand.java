package botmanager.bots.gitmanager.commands;

import botmanager.bots.gitmanager.GitManager;
import botmanager.bots.gitmanager.generic.GitManagerCommandBase;
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
        
        if (bot.isBotChannel(event.getChannel()) && !event.getAuthor().getId().equals(bot.getJDA().getSelfUser().getId())) {
            try {
                event.getMessage().delete().queue();
            } catch (Exception e) {
            }
        }
    }
    
    @Override
    public MessageEmbed.Field info() {
        return new MessageEmbed.Field("", "All commands sent in the server and all non-bot task channel messages "
                + "will be deleted for the sake of cleanliness.", false);
    }

    @Override
    public MessageEmbed getFailureEmbed() {
        return null;
    }

}
