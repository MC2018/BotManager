package botmanager.bots.gitmanager.commands.tasks;

import botmanager.generic.commands.IMessageReceivedCommand;
import botmanager.utils.JDAUtils;
import botmanager.bots.gitmanager.GitManager;
import botmanager.bots.gitmanager.generic.GitManagerCommandBase;
import botmanager.bots.gitmanager.objects.Task;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

/**
 *
 * @author MC_2018 <mc2018.git@gmail.com>
 */
public class TaskDescriptionCommand extends GitManagerCommandBase implements IMessageReceivedCommand {

    private String[] KEYWORDS = {
        bot.prefix + "task description",
        bot.prefix + "task desc",
    };
    
    public TaskDescriptionCommand(GitManager bot) {
        super(bot);
    }

    @Override
    public void runOnMessage(MessageReceivedEvent event) {
        User user = event.getAuthor();
        Task task;
        String input = event.getMessage().getContentRaw();
        long guildID = event.isFromGuild() ? event.getGuild().getIdLong() : bot.getUserSettings(user.getIdLong()).getDefaultGuildID();
        int taskNumber;
        boolean found = false;

        for (String keyword : KEYWORDS) {
            if (input.toLowerCase().startsWith(keyword)) {
                input = input.substring(keyword.length()).trim();
                found = true;
                break;
            }
        }

        if (!found) {
            return;
        } else if (event.isFromGuild() && !bot.isBotChannel(event.getTextChannel())) {
            event.getMessage().delete().queue();
        }
        
        try {
            taskNumber = Integer.parseInt(input.split(" ")[0]);
        } catch (Exception e) {
            JDAUtils.sendPrivateMessage(user, getFailureEmbed());
            return;
        }
        
        if (input.split(" ").length < 2) {
            JDAUtils.sendPrivateMessage(user, getFailureEmbed());
            return;
        }
        
        input = input.substring(input.split(" ")[0].length() + 1, input.length());
        task = bot.getTask(guildID, taskNumber);
        task.setDescription(input, user.getIdLong());
        bot.getTaskChannel(task.getGuildID(), task.getStatus()).editMessageById(task.getMessageID(), bot.generateTaskEmbed(task)).queue();
        bot.writeTask(task);
    }

    
    @Override
    public MessageEmbed.Field info() {
        return new MessageEmbed.Field("Changing a Task Description", "```" + KEYWORDS[0] + " <task id> <task description>```", false);
    }
    
    @Override
    public MessageEmbed getFailureEmbed() {
        EmbedBuilder eb = new EmbedBuilder();
        
        eb.addField(
                "Command Failed",
                "Please use proper syntax:\n"
                        + "```" + KEYWORDS[0] + " <task id> <task description>```",
                true);
        
        return eb.build();
    }

    
}
