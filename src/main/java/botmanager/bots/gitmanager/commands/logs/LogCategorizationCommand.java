package botmanager.bots.gitmanager.commands.logs;

import botmanager.bots.gitmanager.GitManager;
import botmanager.bots.gitmanager.generic.GitManagerCommandBase;
import botmanager.bots.gitmanager.objects.GuildSettings;
import botmanager.bots.gitmanager.objects.Log;
import botmanager.bots.gitmanager.objects.LogType;
import botmanager.bots.gitmanager.objects.Task;
import botmanager.generic.commands.IMessageReactionAddCommand;
import botmanager.generic.commands.IMessageReceivedCommand;
import botmanager.utils.JDAUtils;
import botmanager.utils.Utils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;

import java.util.ArrayList;
import java.util.List;

public class LogCategorizationCommand extends GitManagerCommandBase implements IMessageReactionAddCommand {

    public LogCategorizationCommand(GitManager bot) {
        super(bot);
    }

    @Override
    public void runOnReactionAdd(MessageReactionAddEvent event) {
        GuildSettings gs;
        Message originalMessage;
        List<MessageEmbed> embeds;
        Log log;
        LogType newLogType = null;
        String title;
        int beginningIndex = -1;
        int logID = -1;

        if (!event.isFromGuild()) {
            return;
        }

        try {
            originalMessage = event.getChannel().retrieveMessageById(event.getMessageId()).complete();

            if (!originalMessage.getAuthor().getId().equals(bot.getJDA().getSelfUser().getId()) || event.getUserIdLong() == bot.getJDA().getSelfUser().getIdLong()) {
                return;
            }
        } catch (Exception e) {
            return;
        }

        gs = bot.getGuildSettings(event.getGuild().getIdLong());
        embeds = originalMessage.getEmbeds();

        if (embeds.isEmpty() || Utils.isNullOrEmpty(embeds.get(0).getTitle())) {
            return;
        }

        title = embeds.get(0).getTitle();

        for (int i = 0; i < title.length(); i++) {
            if ('0' <= title.charAt(i) && title.charAt(i) <= '9' && beginningIndex == -1) {
                beginningIndex = i;
            } else if ((title.charAt(i) < '0' ||  '9' < title.charAt(i)) && beginningIndex != -1) {
                logID = Integer.parseInt(title.substring(beginningIndex, i));
                break;
            }
        }

        if (logID <= 0) {
            return;
        } else {
            log = bot.getLog(event.getGuild().getIdLong(), logID);
        }

        if (event.getReactionEmote().isEmote() && LogType.getEmoteNames().contains(event.getReactionEmote().getName())) {
            newLogType = LogType.fromEmoteName(event.getReactionEmote().getName());
        } else if (event.getReactionEmote().isEmoji()) {
            for (String emoteName : LogType.getEmoteNames()) {
                if (Utils.getEmoji(emoteName).getUnicode().equals(event.getReactionEmote().getName())) {
                    newLogType = LogType.fromEmoteName(emoteName);
                    break;
                }
            }
        }

        event.getReaction().removeReaction(event.getUser()).queue();

        if (newLogType == null || newLogType == log.getType()) {
            return;
        }

        log.setType(newLogType);
        event.getChannel().editMessageById(event.getMessageId(), bot.generateLogEmbed(log)).queue();
        bot.writeLog(log);
    }

    @Override
    public MessageEmbed.Field info() {
        return new MessageEmbed.Field("Assignments", "Press the :red_circle: to assign and unassign yourself from a task.", false);
    }

    @Override
    public MessageEmbed getFailureEmbed() {
        return null;
    }

}
