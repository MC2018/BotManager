package botmanager.gitmanager.commands;

import botmanager.JDAUtils;
import botmanager.Utils;
import botmanager.gitmanager.GitManager;
import botmanager.gitmanager.generic.GitManagerCommandBase;
import botmanager.gitmanager.tasks.Task;
import java.util.Arrays;
import java.util.List;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.MessageEmbed.Field;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;

/**
 *
 * @author MC_2018 <mc2018.git@gmail.com>
 */

public class TaskMoverCommand extends GitManagerCommandBase {

    public TaskMoverCommand(GitManager bot) {
        super(bot);
    }

    @Override
    public void run(Event genericEvent) {
        GuildMessageReactionAddEvent event;
        Message originalMessage;
        Message newMessage;
        List<MessageEmbed> embeds;
        Task task;
        String[] reactions = {"todo", "inprogress", "inpr", "completed", "red_circle"};
        String title;
        int newStatus;
        int beginningIndex = -1;
        int taskID = -1;
        
        if (!(genericEvent instanceof GuildMessageReactionAddEvent)) {
            return;
        }

        try {
            event = (GuildMessageReactionAddEvent) genericEvent;
            originalMessage = event.getChannel().retrieveMessageById(event.getMessageId()).complete();
        } catch (Exception e) {
            return;
        }
        
        if (!originalMessage.getAuthor().getId().equals(bot.getJDA().getSelfUser().getId()) || event.getUserIdLong() == bot.getJDA().getSelfUser().getIdLong()) {
            return;
        }
        
        if (Arrays.asList(reactions).contains(event.getReactionEmote().getName())) {
            newStatus = Arrays.asList(reactions).indexOf(event.getReactionEmote().getName());
        } else {
            return;
        }
        
        embeds = originalMessage.getEmbeds();
        
        if (embeds.isEmpty() || Utils.isNullOrEmpty(embeds.get(0).getTitle())) {
            return;
        }
        
        title = embeds.get(0).getTitle();
        
        if (title == null) {
            return;
        }
        
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
        newMessage = JDAUtils.sendGuildMessageReturn(bot.getTaskChannel(event.getGuild().getIdLong(), newStatus), Task.generateTaskEmbed(task, bot));
        GitManager.addTaskReactions(newMessage, newStatus);
        originalMessage.delete().queue();
        task.updateStatus(newStatus, newMessage.getChannel().getIdLong(), newMessage.getIdLong(), event.getUserIdLong());
        bot.writeTask(task);
    }
    
    @Override
    public MessageEmbed.Field info() {
        return new Field("Moving Tasks", "Select one of the categorical reactions to move the task.", false);
    }

    @Override
    public MessageEmbed getFailureEmbed() {
        return null;
    }

}
