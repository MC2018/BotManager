package botmanager.bots.gitmanager.commands;

import botmanager.bots.gitmanager.GitManager;
import botmanager.bots.gitmanager.generic.GitManagerCommandBase;
import botmanager.generic.commands.IMessageReceivedCommand;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

/**
 *
 * @author MC_2018 <mc2018.git@gmail.com>
 */

public class ChannelCleanupCommand extends GitManagerCommandBase implements IMessageReceivedCommand {

    public ChannelCleanupCommand(GitManager bot) {
        super(bot);
    }

    @Override
    public void runOnMessage(MessageReceivedEvent event) {
        if (event.isFromGuild() && bot.isBotChannel(event.getTextChannel()) && !event.getAuthor().getId().equals(bot.getJDA().getSelfUser().getId())) {
            event.getMessage().delete().queue();
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
