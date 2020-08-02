package botmanager.gitmanager.commands;

import botmanager.JDAUtils;
import botmanager.gitmanager.GitManager;
import botmanager.gitmanager.generic.GitManagerCommandBase;
import botmanager.gitmanager.objects.GuildSettings;
import botmanager.gitmanager.objects.Task;
import botmanager.gitmanager.objects.TaskBuilder;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.MessageEmbed.Field;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;

/**
 *
 * @author MC_2018 <mc2018.git@gmail.com>
 */
public class TaskCreateCommand extends GitManagerCommandBase {

    private String[] KEYWORDS = {
        bot.getPrefix() + "task create",
        bot.getPrefix() + "task c",
        bot.getPrefix() + "create task",
    };
    
    public TaskCreateCommand(GitManager bot) {
        super(bot);
    }

    @Override
    public void run(Event genericEvent) {
        GuildMessageReceivedEvent guildEvent = null;
        PrivateMessageReceivedEvent privateEvent = null;
        Message taskMessage;
        User user;
        Task task;
        String input;
        long guildID;
        boolean found = false;

        if (genericEvent instanceof GuildMessageReceivedEvent) {
            guildEvent = (GuildMessageReceivedEvent) genericEvent;
            input = guildEvent.getMessage().getContentRaw();
            user = guildEvent.getAuthor();
            guildID = guildEvent.getGuild().getIdLong();
        } else if (genericEvent instanceof PrivateMessageReceivedEvent) {
            privateEvent = (PrivateMessageReceivedEvent) genericEvent;
            input = privateEvent.getMessage().getContentRaw();
            user = privateEvent.getAuthor();
            guildID = bot.readUserSettings(user.getIdLong()).getDefaultGuildID();
        } else {
            return;
        }
        
        for (String keyword : KEYWORDS) {
            if (input.toLowerCase().startsWith(keyword + " ")) {
                input = input.substring(keyword.length() + 1, input.length());
                found = true;
                break;
            } else if (input.toLowerCase().replaceAll(" ", "").equals(keyword.replaceAll(" ", ""))) {
                JDAUtils.sendPrivateMessage(user, getFailureEmbed());
            }
        }

        if (!found) {
            return;
        } else if (guildEvent != null && !bot.isTaskChannel(guildEvent.getChannel())) {
            guildEvent.getMessage().delete().queue();
        }
        
        try {
            task = new TaskBuilder(bot)
                    .setName(input)
                    .setAuthor(user.getIdLong())
                    .setGuildID(guildID)
                    .build();
        } catch (Exception e) {
            JDAUtils.sendPrivateMessage(user, getFailureEmbed());
            return;
        }
        
        GuildSettings gs = bot.readGuildSettings(guildID);
        
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
    public Field info() {
        return new Field("Creating a Task", "```" + KEYWORDS[0] + " Title```", false);
    }
    
    @Override
    public MessageEmbed getFailureEmbed() {
        EmbedBuilder eb = new EmbedBuilder();
        
        eb.addField(
                "Command Failed",
                "Please use proper syntax:\n"
                        + "```" + KEYWORDS[0] + " Title```",
                true);
        
        return eb.build();
    }

    
}
