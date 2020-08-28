package botmanager.gitmanager.commands.meetings;

import botmanager.JDAUtils;
import botmanager.Utils;
import botmanager.gitmanager.GitManager;
import botmanager.gitmanager.generic.GitManagerCommandBase;
import botmanager.gitmanager.objects.GuildSettings;
import botmanager.gitmanager.objects.Meeting;
import java.util.ArrayList;
import net.dv8tion.jda.api.EmbedBuilder;
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

public class MeetingListCommand extends GitManagerCommandBase {

    private String[] KEYWORDS = {
        bot.getPrefix() + "meeting list",
        bot.getPrefix() + "meetings list"
    };
    
    public MeetingListCommand(GitManager bot) {
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
            if (input.toLowerCase().startsWith(keyword)) {
                found = true;
                break;
            }
        }

        if (!found) {
            return;
        } else if (guildEvent != null && !bot.isTaskChannel(guildEvent.getChannel())) {
            guildEvent.getMessage().delete().queue();
        }
        
        try {
            ArrayList<Meeting> meetings;
            EmbedBuilder eb = new EmbedBuilder();
            
            guildSettings = bot.getGuildSettings(guildID);
            meetings = guildSettings.getMeetings();
            eb.setTitle("Future Meetings");
            
            for (int i = 0; i < meetings.size(); i++) {
                eb.addField(
                        "Index " + (i + 1) + ": " + (meetings.get(i).getDescription() == null ? "No Description" : meetings.get(i).getDescription()),
                        Utils.formatDate(meetings.get(i).getDate(), guildSettings.getDateFormats().get(0)),
                        false);
                
                if (i + 1 < meetings.size()) {
                    eb.addBlankField(false);
                }
            }
            
            JDAUtils.sendPrivateMessage(user, eb.build());
        } catch (Exception e) {
            JDAUtils.sendPrivateMessage(user, getFailureEmbed());
        }
    }
    
    @Override
    public MessageEmbed.Field info() {
        return new Field("Listing all Meetings", "```" + KEYWORDS[0] + "```", false);
    }

    @Override
    public MessageEmbed getFailureEmbed() {
        EmbedBuilder eb = new EmbedBuilder();
        
        eb.addField(
                "Command Failed",
                "There was a problem with the command, if this issue persists please notify the bot manager.",
                false);
        
        return eb.build();
    }

}
