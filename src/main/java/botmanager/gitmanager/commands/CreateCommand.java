package botmanager.gitmanager.commands;

import botmanager.JDAUtils;
import botmanager.gitmanager.GitManager;
import botmanager.gitmanager.generic.GitManagerCommandBase;
import botmanager.gitmanager.tasks.StatusType;
import botmanager.gitmanager.tasks.Task;
import botmanager.gitmanager.tasks.TaskBuilder;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.MessageEmbed.Field;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

/**
 *
 * @author MC_2018 <mc2018.git@gmail.com>
 */
public class CreateCommand extends GitManagerCommandBase {

    private String[] KEYWORDS = {
        bot.getPrefix() + "create",
        bot.getPrefix() + "c"
    };
    
    public CreateCommand(GitManager bot) {
        super(bot);
    }

    @Override
    public void run(Event genericEvent) {
        GuildMessageReceivedEvent event;
        EmbedBuilder eb = new EmbedBuilder();
        TaskBuilder tb = new TaskBuilder(bot);
        Message taskMessage;
        Task task;
        String input;

        boolean found = false;

        if (!(genericEvent instanceof GuildMessageReceivedEvent)) {
            return;
        }

        event = (GuildMessageReceivedEvent) genericEvent;
        input = event.getMessage().getContentRaw();
        
        for (String keyword : KEYWORDS) {
            if (input.toLowerCase().startsWith(keyword + " ")) {
                input = input.substring(keyword.length() + 1, input.length());
                found = true;
                break;
            } else if (input.toLowerCase().replaceAll(" ", "").equals(keyword)) {
                JDAUtils.sendPrivateMessage(event.getAuthor(), getFailureEmbed());
            }
        }

        if (!found) {
            return;
        } else if (!bot.isTaskChannel(event.getChannel())) {
            event.getMessage().delete().queue();
        }
        
        
        tb.setName(input);
        tb.setGuildID(event.getGuild().getIdLong());
        
        try {
            task = tb.build();
        } catch (Exception e) {
            JDAUtils.sendPrivateMessage(event.getAuthor(), getFailureEmbed());
            return;
        }
        
        eb.addField("Task Created", "Task '" + input + "' was created.", true);
        taskMessage = JDAUtils.sendGuildMessageReturn(
                bot.getTaskChannel(event.getGuild().getIdLong(), StatusType.TO_DO),
                Task.generateTaskEmbed(task, bot)
        );
        
        GitManager.addTaskReactions(taskMessage, StatusType.TO_DO);
        JDAUtils.sendPrivateMessage(event.getAuthor(), eb.build());
        task.setChannelID(taskMessage.getChannel().getIdLong());
        task.setMessageID(taskMessage.getIdLong());
        bot.writeTask(task);
    }

    
    @Override
    public Field info() {
        return new Field("Creating a Task", "```" + KEYWORDS[0] + " Title```", false);
    }
    
    @Override
    public MessageEmbed getFailureEmbed() {
        EmbedBuilder eb = new EmbedBuilder();
        
        eb.addField(
                "Command Failed",
                "Please use proper syntax:\n"
                        + KEYWORDS[0] + " Title",
                true);
        
        return eb.build();
    }

    
}
