package botmanager.bots.gitmanager.commands.logs;

import botmanager.bots.gitmanager.GitManager;
import botmanager.bots.gitmanager.generic.GitManagerCommandBase;
import botmanager.bots.gitmanager.objects.GuildSettings;
import botmanager.bots.gitmanager.objects.Log;
import botmanager.bots.gitmanager.objects.LogBuilder;
import botmanager.bots.gitmanager.objects.LogType;
import botmanager.generic.commands.IMessageReceivedCommand;
import botmanager.utils.JDAUtils;
import botmanager.utils.Utils;
import net.dv8tion.jda.api.EmbedBuilder;
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
        String input = Utils.startsWithReplace(event.getMessage().getContentRaw(), KEYWORDS);
        long guildID = event.isFromGuild() ? event.getGuild().getIdLong() : bot.getUserSettings(user.getIdLong()).getDefaultGuildID();
        guildSettings = bot.getGuildSettings(guildID);

        if (input == null || guildID == -1 || guildSettings == null || Utils.isNullOrEmpty(guildSettings.getLogChannel())) {
            return;
        } else if (event.isFromGuild() && !bot.isBotChannel(event.getTextChannel())) {
            event.getMessage().delete().queue();
        }

        for (String hour : HOURS) {
            input = input.replaceAll(hour, "h");
        }

        for (String minute : MINUTES) {
            input = input.replaceAll(minute, "m");
        }

        try {
            Message message;
            Log log;
            Guild guild = bot.getJDA().getGuildById(guildID);
            int hours = 0;
            int minutes = 0;
            Integer.parseInt(input.replaceFirst("h", "").replaceFirst("m", ""));

            if (input.contains("m") && input.indexOf("m") < input.indexOf("h")) {
                throw new Exception();
            } else if (input.contains("h")) {
                hours = Integer.parseInt(input.split("h")[0]);
            }

            if (input.contains("m") || input.split("h").length > 1) {
                minutes = Integer.parseInt(input.substring(input.indexOf("h") + 1).replaceAll("m", ""));
            }

            if ((hours <= 0 && minutes <= 0) || (minutes >= 60)) {
                throw new Exception();
            }

            log = new LogBuilder(bot)
                    .setAuthor(user.getIdLong())
                    .setGuildID(guildID)
                    .setChannelID(event.getChannel().getIdLong())
                    .setMessageID(event.getMessageIdLong())
                    .setMinutes(hours * 60 + minutes)
                    .build();
            message = JDAUtils.sendGuildMessageReturn(JDAUtils.findTextChannel(guild, guildSettings.getLogChannel()), bot.generateLogEmbed(log));
            GitManager.addLogReactions(message, guildSettings);
            bot.writeLog(log);
        } catch (Exception e) {
            JDAUtils.sendPrivateMessage(user, getFailureEmbed());
        }
    }

    @Override
    public MessageEmbed.Field info() {
        return new MessageEmbed.Field("Creating a Log", "```" + bot.prefix + "log <time spent>```Formatting Examples: 2 hours, 3h20m, 15 minutes, 1hr20min", false);
    }

    @Override
    public MessageEmbed getFailureEmbed() {
        EmbedBuilder eb = new EmbedBuilder();

        eb.addField(
                "Command Failed",
                "Please use proper syntax:```\n"
                        + KEYWORDS[0] + " <time spent>```" +
                        "Formatting Examples: 2 hours, 3h20m, 15 minutes, 1hr20min",
                true);

        return eb.build();
    }

}
