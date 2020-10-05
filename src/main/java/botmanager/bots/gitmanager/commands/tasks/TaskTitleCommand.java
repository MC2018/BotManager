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
public class TaskTitleCommand extends GitManagerCommandBase implements IMessageReceivedCommand {

    private String[] KEYWORDS = {
        bot.prefix + "task title",
        bot.prefix + "task t"
    };
    
    public TaskTitleCommand(GitManager bot) {
        super(bot);
    }

    @Override
    public void runOnMessage(MessageReceivedEvent event) {
        Task task;
        User user = event.getAuthor();
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

            if (input.split(" ").length < 2) {
                throw new Exception();
            }
        } catch (Exception e) {
            JDAUtils.sendPrivateMessage(user, getFailureEmbed());
            return;
        }
        
        try {
            input = input.substring(input.split(" ")[0].length() + 1);
            task = bot.getTask(guildID, taskNumber);
            task.setTitle(input, user.getIdLong());
            bot.getTaskChannel(task.getGuildID(), task.getStatus()).editMessageById(task.getMessageID(), bot.generateTaskEmbed(task)).queue();
            bot.writeTask(task);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    
    @Override
    public MessageEmbed.Field info() {
        return new MessageEmbed.Field("Changing a Title", "```" + KEYWORDS[0] + " <task id> <task title>```", false);
    }
    
    @Override
    public MessageEmbed getFailureEmbed() {
        EmbedBuilder eb = new EmbedBuilder();
        
        eb.addField(
                "Command Failed",
                "Please use proper syntax:```\n"
                        + KEYWORDS[0] + " <task id> <task title>```",
                true);
        
        return eb.build();
    }

    
}
