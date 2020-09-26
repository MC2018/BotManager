package botmanager.bots.gitmanager.commands.meetings;

import botmanager.generic.commands.IMessageReceivedCommand;
import botmanager.utils.*;
import botmanager.bots.gitmanager.GitManager;
import botmanager.bots.gitmanager.generic.GitManagerCommandBase;
import botmanager.bots.gitmanager.objects.*;
import java.util.ArrayList;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

/**
 *
 * @author MC_2018 <mc2018.git@gmail.com>
 */

public class MeetingListCommand extends GitManagerCommandBase implements IMessageReceivedCommand {

    private String[] KEYWORDS = {
        bot.prefix + "meeting list",
        bot.prefix + "meetings list"
    };
    
    public MeetingListCommand(GitManager bot) {
        super(bot);
    }

    @Override
    public void runOnMessage(MessageReceivedEvent event) {
        GuildSettings guildSettings;
        User user = event.getAuthor();
        String input = event.getMessage().getContentRaw();
        long guildID = event.isFromGuild() ? event.getGuild().getIdLong() : bot.readUserSettings(user.getIdLong()).getDefaultGuildID();
        boolean found = false;
        
        for (String keyword : KEYWORDS) {
            if (input.toLowerCase().startsWith(keyword)) {
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
        return new MessageEmbed.Field("Listing all Meetings", "```" + KEYWORDS[0] + "```", false);
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
