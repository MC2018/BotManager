package botmanager.bots.gitmanager.commands.meetings;

import botmanager.generic.commands.IMessageReceivedCommand;
import botmanager.utils.*;
import botmanager.bots.gitmanager.GitManager;
import botmanager.bots.gitmanager.generic.GitManagerCommandBase;
import botmanager.bots.gitmanager.objects.GuildSettings;
import java.util.Date;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

/**
 *
 * @author MC_2018 <mc2018.git@gmail.com>
 */

public class MeetingCreateCommand extends GitManagerCommandBase implements IMessageReceivedCommand {

    private String[] KEYWORDS = {
        bot.prefix + "meeting create",
        bot.prefix + "meetings create",
        bot.prefix + "create meeting",
        bot.prefix + "meeting add",
        bot.prefix + "meetings add",
        bot.prefix + "add meeting",
    };
    
    public MeetingCreateCommand(GitManager bot) {
        super(bot);
    }

    @Override
    public void runOnMessage(MessageReceivedEvent event) {
        GuildSettings guildSettings;
        User user = event.getAuthor();
        String input = Utils.startsWithReplace(event.getMessage().getContentRaw(), KEYWORDS);
        long guildID = event.isFromGuild() ? event.getGuild().getIdLong() : bot.getUserSettings(user.getIdLong()).getDefaultGuildID();

        if (input == null || guildID == -1) {
            return;
        } else if (event.isFromGuild() && !bot.isBotChannel(event.getTextChannel())) {
            event.getMessage().delete().queue();
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
            eb.setDescription("Date: " + Utils.formatDate(date, guildSettings.getDateFormats().get(0)));
            eb.addField("Want to set a description?", "```" + bot.prefix + "meeting description " + (guildSettings.getMeetingIndexAtDate(date) + 1) + " New Description```", false);
            JDAUtils.sendPrivateMessage(user, eb.build());
        } catch (Exception e) {
            JDAUtils.sendPrivateMessage(user, getFailureEmbed(guildID));
        }
    }
    
    @Override
    public MessageEmbed.Field info() {
        return new MessageEmbed.Field("Creating a Meeting", "```" + KEYWORDS[0] + " <date and time>```", false);
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
                        + "```" + KEYWORDS[0] + " <date and time>```",
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
                        + "```" + KEYWORDS[0] + " <date and time>```",
                false);
        eb.addField("Formats Allowed", "```" + formats.toString().trim() + "```", false);
        
        return eb.build();
    }

}
