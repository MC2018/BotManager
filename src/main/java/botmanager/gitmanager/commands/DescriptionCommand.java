package botmanager.gitmanager.commands;

import botmanager.JDAUtils;
import botmanager.gitmanager.GitManager;
import botmanager.gitmanager.generic.GitManagerCommandBase;
import botmanager.gitmanager.tasks.Task;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.MessageEmbed.Field;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

/**
 *
 * @author MC_2018 <mc2018.git@gmail.com>
 */
public class DescriptionCommand extends GitManagerCommandBase {

    private String[] KEYWORDS = {
        bot.getPrefix() + "description",
        bot.getPrefix() + "desc",
        bot.getPrefix() + "d"
    };
    
    public DescriptionCommand(GitManager bot) {
        super(bot);
    }

    @Override
    public void run(Event genericEvent) {
        GuildMessageReceivedEvent event;
        Task task;
        String input;
        int taskNumber;
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
        
        try {
            taskNumber = Integer.parseInt(input.split(" ")[0]);
        } catch (Exception e) {
            JDAUtils.sendPrivateMessage(event.getAuthor(), getFailureEmbed());
            return;
        }
        
        if (input.split(" ").length < 2) {
            JDAUtils.sendPrivateMessage(event.getAuthor(), getFailureEmbed());
            return;
        }
        
        input = input.substring(input.split(" ")[0].length() + 1, input.length());
        task = bot.readTask(event.getGuild().getIdLong(), taskNumber);
        task.setDescription(input, event.getAuthor().getIdLong());
        bot.getTaskChannel(task.getGuildID(), task.getStatus()).editMessageById(task.getMessageID(), Task.generateTaskEmbed(task, bot)).queue();
        bot.writeTask(task);
    }

    
    @Override
    public Field info() {
        return new Field("Changing a Description", "```" + KEYWORDS[0] + " 102 New Description```", false);
    }
    
    @Override
    public MessageEmbed getFailureEmbed() {
        EmbedBuilder eb = new EmbedBuilder();
        
        eb.addField(
                "Command Failed",
                "Please use proper syntax:\n"
                        + KEYWORDS[0] + " TASK_ID NEW_DESCRIPTION",
                true);
        
        return eb.build();
    }

    
}
