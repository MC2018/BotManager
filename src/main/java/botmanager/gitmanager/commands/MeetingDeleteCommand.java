package botmanager.gitmanager.commands;

import botmanager.JDAUtils;
import botmanager.Utils;
import botmanager.gitmanager.GitManager;
import botmanager.gitmanager.generic.GitManagerCommandBase;
import botmanager.gitmanager.objects.GuildSettings;
import botmanager.gitmanager.objects.Meeting;
import java.util.Date;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;

/**
 *
 * @author MC_2018 <mc2018.git@gmail.com>
 */

public class MeetingDeleteCommand extends GitManagerCommandBase {

    private String[] KEYWORDS = {
        bot.getPrefix() + "meeting delete",
        bot.getPrefix() + "meetings delete",
        bot.getPrefix() + "delete meeting",
        bot.getPrefix() + "meeting remove",
        bot.getPrefix() + "meetings remove",
        bot.getPrefix() + "remove meeting",
    };
    
    public MeetingDeleteCommand(GitManager bot) {
        super(bot);
    }

    @Override
    public void run(Event genericEvent) {
        GuildMessageReceivedEvent guildEvent = null;
        PrivateMessageReceivedEvent privateEvent = null;
        GuildSettings guildSettings;
        User user;
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
            EmbedBuilder eb = new EmbedBuilder();
            Meeting meeting;
            int index = Integer.parseInt(input);
            guildSettings = bot.getGuildSettings(guildID);
            meeting = guildSettings.getMeetingAtIndex(index - 1);
            guildSettings.removeMeeting(meeting.getDate());
            bot.writeGuildSettings(guildSettings);
            
            eb.setTitle("Meeting Deleted");
            eb.addField(Utils.formatDate(meeting.getDate(), guildSettings.getDateFormats().get(0)), meeting.getDescription(), false);
            JDAUtils.sendPrivateMessage(user, eb.build());
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
        return new MessageEmbed.Field("Deleting a Meeting", "```" + KEYWORDS[0] + " 102```", false);
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
        GuildSettings guildSettings = bot.getGuildSettings(guildID);
        StringBuilder formats = new StringBuilder();
        Date date = new Date();
        
        guildSettings.getDateFormats().forEach(x -> formats.append(Utils.formatDate(date, x)).append("\n"));
        eb.addField(
                "Command Failed",
                "Please use proper syntax:\n"
                        + "```" + KEYWORDS[0] + " INDEX```",
                false);
        
        return eb.build();
    }

}
