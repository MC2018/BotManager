package botmanager.bots.gitmanager.commands.tasks;

import botmanager.generic.commands.IMessageReactionAddCommand;
import botmanager.utils.Utils;
import botmanager.bots.gitmanager.GitManager;
import botmanager.bots.gitmanager.generic.GitManagerCommandBase;
import botmanager.bots.gitmanager.objects.Task;
import java.util.List;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;

/**
 *
 * @author MC_2018 <mc2018.git@gmail.com>
 */

public class TaskAssignCommand extends GitManagerCommandBase implements IMessageReactionAddCommand {

    public TaskAssignCommand(GitManager bot) {
        super(bot);
    }

    @Override
    public void runOnReactionAdd(MessageReactionAddEvent event) {
        Message message;
        List<MessageEmbed> embeds;
        Task task;
        String title;
        int beginningIndex = -1;
        int taskID = -1;

        if (!event.isFromGuild() || event.getReactionEmote().isEmote() || !Utils.getEmojiAlias(event.getReactionEmote().getEmoji()).equals("red_circle")) {
            return;
        }

        try {
            message = event.getChannel().retrieveMessageById(event.getMessageId()).complete();

            if (!message.getAuthor().getId().equals(bot.getJDA().getSelfUser().getId()) || event.getUserIdLong() == bot.getJDA().getSelfUser().getIdLong()) {
                return;
            }
        } catch (Exception e) {
            return;
        }

        event.getReaction().removeReaction(event.getUser()).queue();
        embeds = message.getEmbeds();

        if (embeds.isEmpty() || Utils.isNullOrEmpty(embeds.get(0).getTitle())) {
            return;
        }

        title = embeds.get(0).getTitle();

        for (int i = 0; i < title.length(); i++) {
            if ('0' <= title.charAt(i) && title.charAt(i) <= '9' && beginningIndex == -1) {
                beginningIndex = i;
            } else if ((title.charAt(i) < '0' ||  '9' < title.charAt(i)) && beginningIndex != -1) {
                taskID = Integer.parseInt(title.substring(beginningIndex, i));
                break;
            }
        }
        
        if (taskID <= 0) {
            return;
        }
        
        task = bot.readTask(event.getGuild().getIdLong(), taskID);
        
        if (task.getAssignee() == event.getUserIdLong()) {
            task.setAssignee(-1, event.getUserIdLong());
        } else {
            task.setAssignee(event.getUserIdLong(), event.getUserIdLong());
        }
        
        message.editMessage(bot.generateTaskEmbed(task)).queue();
        bot.writeTask(task);
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
