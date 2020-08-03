package botmanager.gitmanager.commands;

import botmanager.JDAUtils;
import botmanager.gitmanager.GitManager;
import botmanager.gitmanager.generic.GitManagerCommandBase;
import botmanager.gitmanager.objects.GuildSettings;
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
public class MeetingDescriptionCommand extends GitManagerCommandBase {

    private String[] KEYWORDS = {
        bot.getPrefix() + "meeting description",
        bot.getPrefix() + "meeting desc",
        bot.getPrefix() + "meeting d"
    };
    
    public MeetingDescriptionCommand(GitManager bot) {
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
        int meetingNumber;
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
            meetingNumber = Integer.parseInt(input.split(" ")[0]);
        } catch (Exception e) {
            JDAUtils.sendPrivateMessage(user, getFailureEmbed());
            return;
        }
        
        if (input.split(" ").length < 2) {
            JDAUtils.sendPrivateMessage(user, getFailureEmbed());
            return;
        }
        
        try {
            guildSettings = bot.getGuildSettings(guildID);
            input = input.substring(input.split(" ")[0].length() + 1, input.length());
            guildSettings.getMeetingAtIndex(meetingNumber - 1).setDescription(input);
            bot.writeGuildSettings(guildSettings);
        } catch (Exception e) {
            JDAUtils.sendPrivateMessage(user, getFailureEmbed());
        }
    }

    
    @Override
    public Field info() {
        return new Field("Changing a Meeting Description", "```" + KEYWORDS[0] + " 102 New Description```", false);
    }
    
    @Override
    public MessageEmbed getFailureEmbed() {
        EmbedBuilder eb = new EmbedBuilder();
        
        eb.addField(
                "Command Failed",
                "Please use proper syntax:\n"
                        + "```" + KEYWORDS[0] + " MEETING_ID NEW_DESCRIPTION```",
                true);
        
        return eb.build();
    }

    
}
