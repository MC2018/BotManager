package botmanager.bots.gitmanager.commands;

import botmanager.bots.gitmanager.GitManager;
import botmanager.bots.gitmanager.generic.GitManagerCommandBase;
import botmanager.bots.gitmanager.objects.GuildSettings;
import botmanager.generic.commands.IMessageReceivedCommand;
import botmanager.utils.JDAUtils;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class LogCommand extends GitManagerCommandBase implements IMessageReceivedCommand {

    private final String[] KEYWORDS = {
            bot.getPrefix() + "log"
    };

    public LogCommand(GitManager bot) {
        super(bot);
    }

    @Override
    public void runOnMessage(MessageReceivedEvent event) {
        GuildSettings guildSettings;
        User user = event.getAuthor();
        String input = event.getMessage().getContentRaw();
        long guildID = event.isFromGuild() ? event.getGuild().getIdLong() : bot.readUserSettings(user.getIdLong()).getDefaultGuildID();
        boolean found = false;

        for (String keyword : KEYWORDS) {
            if (input.toLowerCase().startsWith(keyword + " ")) {
                input = input.substring(keyword.length() + 1, input.length());
                found = true;
                break;
            } else if (input.toLowerCase().replaceAll(" ", "").equals(keyword.replaceAll(" ", ""))) {
                JDAUtils.sendPrivateMessage(user, getFailureEmbed(guildID));
            }
        }
    }

    @Override
    public MessageEmbed.Field info() {
        return new MessageEmbed.Field("", "", false);
    }

    @Override
    public MessageEmbed getFailureEmbed() {
        return null;
    }

    public MessageEmbed getFailureEmbed(long guildID) {
        return null;
    }

}
