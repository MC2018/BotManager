package botmanager.gitmanager.commands;

import botmanager.Utils;
import botmanager.gitmanager.GitManager;
import botmanager.gitmanager.generic.GitManagerCommandBase;
import botmanager.gitmanager.tasks.Task;
import java.util.List;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.MessageEmbed.Field;
import net.dv8tion.jda.api.entities.MessageReaction.ReactionEmote;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;

/**
 *
 * @author MC_2018 <mc2018.git@gmail.com>
 */

public class AssignCommand extends GitManagerCommandBase {

    public AssignCommand(GitManager bot) {
        super(bot);
    }

    @Override
    public void run(Event genericEvent) {
        GuildMessageReactionAddEvent event;
        Message message;
        List<MessageEmbed> embeds;
        Task task;
        String title;
        int beginningIndex = -1;
        int taskID = -1;
        
        if (!(genericEvent instanceof GuildMessageReactionAddEvent)) {
            return;
        }

        try {
            event = (GuildMessageReactionAddEvent) genericEvent;
            message = event.getChannel().retrieveMessageById(event.getMessageId()).complete();
        } catch (Exception e) {
            return;
        }
        
        if (!message.getAuthor().getId().equals(bot.getJDA().getSelfUser().getId()) || event.getUserIdLong() == bot.getJDA().getSelfUser().getIdLong()) {
            return;
        }
        
        if (event.getReactionEmote().isEmote() || !Utils.getEmojiAlias(event.getReactionEmote().getEmoji()).equals("red_circle")) {
            return;
        }
        
        event.getReaction().removeReaction(event.getUser()).queue();
        embeds = message.getEmbeds();
        
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
        
        if (task.getAssignee() == event.getUserIdLong()) {
            task.setAssignee(-1, event.getUserIdLong());
        } else {
            task.setAssignee(event.getUserIdLong(), event.getUserIdLong());
        }
        
        message.editMessage(Task.generateTaskEmbed(task, bot)).queue();
        bot.writeTask(task);
    }
    
    @Override
    public MessageEmbed.Field info() {
        return new Field("Assignments", "Press the :red_circle: to assign and unassign yourself from a task.", false);
    }

    @Override
    public MessageEmbed getFailureEmbed() {
        return null;
    }

}
