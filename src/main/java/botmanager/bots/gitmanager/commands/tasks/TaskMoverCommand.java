package botmanager.bots.gitmanager.commands.tasks;

import botmanager.generic.commands.IMessageReactionAddCommand;
import botmanager.utils.*;
import botmanager.bots.gitmanager.GitManager;
import botmanager.bots.gitmanager.generic.GitManagerCommandBase;
import botmanager.bots.gitmanager.objects.*;
import java.util.ArrayList;
import java.util.List;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;

/**
 *
 * @author MC_2018 <mc2018.git@gmail.com>
 */

public class TaskMoverCommand extends GitManagerCommandBase implements IMessageReactionAddCommand {

    public TaskMoverCommand(GitManager bot) {
        super(bot);
    }

    @Override
    public void runOnReactionAdd(MessageReactionAddEvent event) {
        Message originalMessage;
        Message newMessage;
        List<MessageEmbed> embeds;
        GuildSettings gs;
        ArrayList<String> taskReactionNames;
        Task task;
        String title;
        int newStatus = -1;
        int beginningIndex = -1;
        long taskID = -1;
        
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
        taskReactionNames = gs.getTaskReactionNames();
        embeds = originalMessage.getEmbeds();
        
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
        } else {
            task = bot.getTask(event.getGuild().getIdLong(), taskID);
        }
        
        if (event.getReactionEmote().isEmote() && taskReactionNames.contains(event.getReactionEmote().getName())) {
            newStatus = taskReactionNames.indexOf(event.getReactionEmote().getName());
        } else if (!Utils.isNullOrEmpty(gs.getBumpReactionName()) && event.getReactionEmote().getName().contains(Utils.getEmoji(gs.getBumpReactionName()).getUnicode())) {
            newStatus = task.getStatus();
        } else if (event.getReactionEmote().isEmoji()) {
            for (String taskReactionName : taskReactionNames) {
                if (Utils.getEmoji(taskReactionName).getUnicode().equals(event.getReactionEmote().getName())) {
                    newStatus = taskReactionNames.indexOf(taskReactionName);
                    break;
                }
            }
        }
        
        if (newStatus == -1) {
            return;
        }
        
        newMessage = JDAUtils.sendGuildMessageReturn(bot.getTaskChannel(event.getGuild().getIdLong(), newStatus), bot.generateTaskEmbed(task));
        GitManager.addTaskReactions(newMessage, gs, newStatus);
        originalMessage.delete().queue();
        task.updateStatus(newStatus, newMessage.getChannel().getIdLong(), newMessage.getIdLong(), event.getUserIdLong());
        bot.writeTask(task);
    }
    
    @Override
    public MessageEmbed.Field info() {
        return new MessageEmbed.Field("Moving Tasks", "Select one of the categorical reactions to move the task.", false);
    }

    @Override
    public MessageEmbed getFailureEmbed() {
        return null;
    }

}
