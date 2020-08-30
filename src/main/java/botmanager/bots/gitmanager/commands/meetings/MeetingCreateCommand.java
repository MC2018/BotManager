package botmanager.bots.gitmanager.commands.meetings;

import botmanager.utils.JDAUtils;
import botmanager.utils.Utils;
import botmanager.bots.gitmanager.GitManager;
import botmanager.bots.gitmanager.generic.GitManagerCommandBase;
import botmanager.bots.gitmanager.objects.GuildSettings;
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

public class MeetingCreateCommand extends GitManagerCommandBase {

    private String[] KEYWORDS = {
        bot.getPrefix() + "meeting create",
        bot.getPrefix() + "meetings create",
        bot.getPrefix() + "create meeting",
        bot.getPrefix() + "meeting add",
        bot.getPrefix() + "meetings add",
        bot.getPrefix() + "add meeting",
    };
    
    public MeetingCreateCommand(GitManager bot) {
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
                JDAUtils.sendPrivateMessage(user, getFailureEmbed(guildID));
            }
        }

        if (!found) {
            return;
        } else if (guildEvent != null && !bot.isTaskChannel(guildEvent.getChannel())) {
            guildEvent.getMessage().delete().queue();
        }
        
        try {
            EmbedBuilder eb = new EmbedBuilder();
            Date date;
            guildSettings = bot.getGuildSettings(guildID);
            date = Utils.parseDate(input, guildSettings.getDateFormats());
            
            if ((new Date()).after(date)) {
                JDAUtils.sendPrivateMessage(user, getEarlyFailureEmbed());
                return;
            }
            
            guildSettings.addMeeting(date);
            bot.writeGuildSettings(guildSettings);
            
            eb.setTitle("Meeting Set (Index " + (guildSettings.getMeetingIndexAtDate(date) + 1) + ")");
            eb.setDescription("Date: " + input);
            eb.addField("Want to set a description?", "```" + bot.getPrefix() + "meeting description " + (guildSettings.getMeetingIndexAtDate(date) + 1) + " New Description```", false);
            JDAUtils.sendPrivateMessage(user, eb.build());
        } catch (Exception e) {
            JDAUtils.sendPrivateMessage(user, getFailureEmbed(guildID));
        }
    }
    
    @Override
    public MessageEmbed.Field info() {
        return new MessageEmbed.Field("Creating a Meeting", "```" + KEYWORDS[0] + " Time```", false);
    }

    public MessageEmbed getEarlyFailureEmbed() {
        EmbedBuilder eb = new EmbedBuilder();
        
        eb.addField("Command Failed", "You submitted a time that has already occurred!", false);
        return eb.build();
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

        if (guildSettings == null) {
            return getFailureEmbed();
        }

        guildSettings.getDateFormats().forEach(x -> formats.append(Utils.formatDate(date, x)).append("\n"));
        eb.addField(
                "Command Failed",
                "Please use proper syntax:\n"
                        + "```" + KEYWORDS[0] + " TIME```",
                false);
        eb.addField("Formats Allowed", "```" + formats.toString().trim() + "```", false);
        
        return eb.build();
    }

}
