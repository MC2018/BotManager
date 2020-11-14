package botmanager.bots.gitmanager.commands.logs;

import botmanager.bots.gitmanager.GitManager;
import botmanager.bots.gitmanager.generic.GitManagerCommandBase;
import botmanager.bots.gitmanager.objects.*;
import botmanager.generic.commands.IMessageReactionAddCommand;
import botmanager.utils.Utils;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import java.util.List;

public class LogCategorizationCommand extends GitManagerCommandBase implements IMessageReactionAddCommand {

    public LogCategorizationCommand(GitManager bot) {
        super(bot);
    }

    @Override
    public void runOnReactionAdd(MessageReactionAddEvent event) {
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
        String description = "Emotes used to select differing log categories are as follows:";

        for (LogType logType : LogType.values()) {
            description += "\n:" + logType.getEmoteName() + ": " + logType.getName();
        }

        return new MessageEmbed.Field("Log Categories", description, false);
    }

    @Override
    public MessageEmbed getFailureEmbed() {
        return null;
    }

}
