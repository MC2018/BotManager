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

public class MeetingDeleteCommand extends GitManagerCommandBase implements IMessageReceivedCommand {

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
    public void runOnMessage(MessageReceivedEvent event) {
        GuildSettings guildSettings;
        User user = event.getAuthor();
        String input = event.getMessage().getContentRaw();
        long guildID = event.isFromGuild() ? event.getGuild().getIdLong() : bot.readUserSettings(user.getIdLong()).getDefaultGuildID();
        boolean found = false;
        
        for (String keyword : KEYWORDS) {
            if (input.toLowerCase().startsWith(keyword + " ")) {
                input = input.substring(keyword.length() + 1, input.length());
                found = true;
                break;
            } else if (input.toLowerCase().replaceAll(" ", "").equals(keyword.replaceAll(" ", ""))) {
                JDAUtils.sendPrivateMessage(user, guildID == -1 ? getFailureEmbed() : getFailureEmbed(guildID));
            }
        }

        if (!found) {
            return;
        } else if (event.isFromGuild() && !bot.isBotChannel(event.getTextChannel())) {
            event.getMessage().delete().queue();
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
            eb.addField(Utils.formatDate(meeting.getDate(), guildSettings.getDateFormats().get(0)), meeting.getDescription(),false);
            JDAUtils.sendPrivateMessage(user, eb.build());
        } catch (Exception e) {
            JDAUtils.sendPrivateMessage(user, guildID == -1 ? getFailureEmbed() : getFailureEmbed(guildID));
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
                        + "```" + KEYWORDS[0] + " INDEX```\n" +
                        "(The index can be found from using the meeting list command)",
                false);
        
        return eb.build();
    }

    public MessageEmbed getFailureEmbed(long guildID) {
        EmbedBuilder eb = new EmbedBuilder();
        GuildSettings guildSettings = bot.getGuildSettings(guildID);
        ArrayList<Meeting> meetings = guildSettings.getMeetings();
        StringBuilder list = new StringBuilder();

        for (int i = 0; i < meetings.size() && list.length() < Message.MAX_CONTENT_LENGTH - 1000; i++) {
            list.append("Index " + (i + 1) + ": " + Utils.formatDate(meetings.get(i).getDate(), guildSettings.getDateFormats().get(0)) + "\n");
            list.append(meetings.get(i).getDescription());

            if (i + 1 < meetings.size()) {
                list.append("\n\n");
            }
        }

        for (MessageEmbed.Field field : getFailureEmbed().getFields()) {
            eb.addField(field);
        }

        eb.addField("List of soonest planned meetings", list.toString(), false);

        return eb.build();
    }

}
