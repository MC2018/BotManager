package botmanager.gitmanager.commands;

import botmanager.JDAUtils;
import botmanager.Utils;
import botmanager.gitmanager.GitManager;
import botmanager.gitmanager.generic.GitManagerCommandBase;
import botmanager.gitmanager.objects.MeetingManager;
import botmanager.gitmanager.objects.Task;
import java.util.Date;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;

/**
 *
 * @author MC_2018 <mc2018.git@gmail.com>
 */

public class MeetingCreateCommand extends GitManagerCommandBase {

    private String[] KEYWORDS = {
        bot.getPrefix() + "meeting create",
        bot.getPrefix() + "create meeting"
    };
    
    public MeetingCreateCommand(GitManager bot) {
        super(bot);
    }

    @Override
    public void run(Event genericEvent) {
        GuildMessageReceivedEvent guildEvent = null;
        PrivateMessageReceivedEvent privateEvent = null;
        MeetingManager meetingManager;
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
            } else if (input.toLowerCase().replaceAll(" ", "").equals(keyword)) {
                JDAUtils.sendPrivateMessage(user, getFailureEmbed());
            }
        }

        if (!found) {
            return;
        } else if (guildEvent != null && !bot.isTaskChannel(guildEvent.getChannel())) {
            guildEvent.getMessage().delete().queue();
        }
        
        try {
            Date date;
            meetingManager = bot.readMeetingManager(guildID);
            date = Utils.parseDate(input, meetingManager.getDateFormats());
            meetingManager.addMeeting(date);
            bot.writeMeetingManager(meetingManager);
        } catch (Exception e) {
            if (guildID == -1) {
                JDAUtils.sendPrivateMessage(user, getFailureEmbed());
            } else {
                JDAUtils.sendPrivateMessage(user, getFailureEmbed(guildID));
            }
        }
    }
    
    @Override
    public MessageEmbed.Field info() {
        return null;
    }

    @Override
    public MessageEmbed getFailureEmbed() {
        EmbedBuilder eb = new EmbedBuilder();
        
        eb.addField(
                "Command Failed",
                "Please use proper syntax:\n"
                        + "```" + KEYWORDS[0] + " TIME```",
                false);
        
        return eb.build();
    }
    
    public MessageEmbed getFailureEmbed(long guildID) {
        EmbedBuilder eb = new EmbedBuilder();
        MeetingManager meetingManager = bot.readMeetingManager(guildID);
        StringBuilder formats = new StringBuilder();
        Date date = new Date();
        
        meetingManager.getDateFormats().forEach(x -> formats.append(Utils.formatDate(date, x)).append("\n"));
        eb.addField(
                "Command Failed",
                "Please use proper syntax:\n"
                        + "```" + KEYWORDS[0] + " TIME```",
                false);
        eb.addField("Formats Allowed", "```" + formats.toString().trim() + "```", false);
        
        return eb.build();
    }

}
