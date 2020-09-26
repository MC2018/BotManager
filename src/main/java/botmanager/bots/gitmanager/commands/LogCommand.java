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
            bot.prefix + "log"
    };
    private final String[] HOURS = {
            "hours",
            "hour",
            "hrs",
            "hr",
            "h",
    };
    private final String[] MINUTES = {
            "minutes",
            "minute",
            "mins",
            "min",
            "m",
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
        int[] parsedNumbers = new int[2];
        boolean found = false;

        for (String keyword : KEYWORDS) {
            if (input.toLowerCase().startsWith(keyword + " ")) {
                input = input.substring(keyword.length() + 1);
                found = true;
                break;
            } else if (input.toLowerCase().replaceAll(" ", "").equals(keyword.replaceAll(" ", ""))) {
                JDAUtils.sendPrivateMessage(user, getFailureEmbed(guildID));
            }
        }

        if (!found) {
            return;
        }

        for (String hour : HOURS) {
            input = input.replaceAll(hour, "h");
        }

        for (String minute : MINUTES) {
            input = input.replaceAll(minute, "m");
        }

        try {
            Integer.parseInt(input.replaceFirst("m", "").replaceFirst("h", ""));

        } catch (Exception e) {
            JDAUtils.sendPrivateMessage(user, getFailureEmbed(guildID));
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
