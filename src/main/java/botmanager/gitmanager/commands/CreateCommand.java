package botmanager.gitmanager.commands;

import botmanager.Utilities;
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
 * @author maxclausius
 */
public class CreateCommand extends GitManagerCommandBase {

    private String[] KEYWORDS = {
        bot.getPrefix() + "create"
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
                input = input.toLowerCase().replace(keyword + " ", "");
                found = true;
                break;
            } else if (input.toLowerCase().replaceAll(" ", "").equals(keyword)) {
                Utilities.sendGuildMessage(event.getChannel(), getSyntaxFailureEmbed());
            }
        }

        if (!found) {
            return;
        }
        
        tb.setName(input);
        tb.setGuildID(event.getGuild().getIdLong());
        
        try {
            task = tb.build();
        } catch (Exception e) {
            Utilities.sendGuildMessage(event.getChannel(), getSyntaxFailureEmbed());
            return;
        }
        
        eb.addField("Task Created", "Task '" + input + "' was created.", true);
        taskMessage = Utilities.sendGuildMessageReturn(
                bot.getTaskChannel(event.getGuild().getIdLong(), StatusType.TO_DO),
                Task.generateMessageEmbed(task)
        );
        
        GitManager.addTaskReactions(taskMessage, StatusType.TO_DO);
        Utilities.sendPrivateMessage(event.getAuthor(), eb.build());
    }

    
    @Override
    public Field info() {
        return null;
    }
    
    public MessageEmbed getSyntaxFailureEmbed() {
        EmbedBuilder eb = new EmbedBuilder();
        
        eb.addField(
                "Command Failed",
                "Please use proper syntax:\n"
                        + KEYWORDS[0] + "Title",
                true);
        
        return eb.build();
    }

    
}
