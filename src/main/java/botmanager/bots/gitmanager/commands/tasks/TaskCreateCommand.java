package botmanager.bots.gitmanager.commands.tasks;

import botmanager.generic.commands.IMessageReceivedCommand;
import botmanager.utils.JDAUtils;
import botmanager.bots.gitmanager.GitManager;
import botmanager.bots.gitmanager.generic.GitManagerCommandBase;
import botmanager.bots.gitmanager.objects.*;
import botmanager.utils.Utils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

/**
 *
 * @author MC_2018 <mc2018.git@gmail.com>
 */
public class TaskCreateCommand extends GitManagerCommandBase implements IMessageReceivedCommand {

    private String[] KEYWORDS = {
        bot.prefix + "task create",
        bot.prefix + "task c",
        bot.prefix + "create task",
    };
    
    public TaskCreateCommand(GitManager bot) {
        super(bot);
    }

    @Override
    public void runOnMessage(MessageReceivedEvent event) {
        GuildSettings gs;
        Message taskMessage;
        User user = event.getAuthor();
        Task task;
        String input = event.getMessage().getContentRaw();
        long guildID = event.isFromGuild() ? event.getGuild().getIdLong() : bot.getUserSettings(user.getIdLong()).getDefaultGuildID();
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
            if (Utils.isNullOrEmpty(input)) {
                throw new Exception();
            }

            task = new TaskBuilder(bot)
                    .setName(input)
                    .setAuthor(user.getIdLong())
                    .setGuildID(guildID)
                    .build();
        } catch (Exception e) {
            JDAUtils.sendPrivateMessage(user, getFailureEmbed());
            return;
        }
        
        gs = bot.getGuildSettings(guildID);
        
        taskMessage = JDAUtils.sendGuildMessageReturn(
                bot.getTaskChannel(guildID, gs.getDefaultTaskChannelIndex()),
                bot.generateTaskEmbed(task)
        );
        
        GitManager.addTaskReactions(taskMessage, gs, gs.getDefaultTaskChannelIndex());
        task.setChannelID(taskMessage.getChannel().getIdLong());
        task.setMessageID(taskMessage.getIdLong());
        bot.writeTask(task);
    }

    
    @Override
    public MessageEmbed.Field info() {
        return new MessageEmbed.Field("Creating a Task", "```" + KEYWORDS[0] + " <title>```", false);
    }
    
    @Override
    public MessageEmbed getFailureEmbed() {
        EmbedBuilder eb = new EmbedBuilder();
        
        eb.addField(
                "Command Failed",
                "Please use proper syntax:\n"
                        + "```" + KEYWORDS[0] + " <title>```",
                true);
        
        return eb.build();
    }

    
}
