package botmanager.bots.gitmanager.commands.meetings;

import botmanager.generic.commands.IMessageReceivedCommand;
import botmanager.utils.*;
import botmanager.bots.gitmanager.GitManager;
import botmanager.bots.gitmanager.generic.GitManagerCommandBase;
import botmanager.bots.gitmanager.objects.GuildSettings;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

/**
 *
 * @author MC_2018 <mc2018.git@gmail.com>
 */
public class MeetingDescriptionCommand extends GitManagerCommandBase implements IMessageReceivedCommand {

    private String[] KEYWORDS = {
        bot.prefix + "meeting description",
        bot.prefix + "meeting desc",
        bot.prefix + "meetings description",
        bot.prefix + "meetings desc",
    };
    
    public MeetingDescriptionCommand(GitManager bot) {
        super(bot);
    }

    @Override
    public void runOnMessage(MessageReceivedEvent event) {
        GuildSettings guildSettings;
        User user = event.getAuthor();
        String input = Utils.startsWithReplace(event.getMessage().getContentRaw(), KEYWORDS);
        long guildID = event.isFromGuild() ? event.getGuild().getIdLong() : bot.getUserSettings(user.getIdLong()).getDefaultGuildID();
        int meetingNumber;

        if (input == null) {
            return;
        } else if (event.isFromGuild() && !bot.isBotChannel(event.getTextChannel())) {
            event.getMessage().delete().queue();
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
            EmbedBuilder eb = new EmbedBuilder();
            guildSettings = bot.getGuildSettings(guildID);
            input = input.substring(input.split(" ")[0].length() + 1, input.length());
            guildSettings.getMeetingAtIndex(meetingNumber - 1).setDescription(input);
            bot.writeGuildSettings(guildSettings);
            
            eb.setTitle("Meeting Description Updated");
            eb.addField(Utils.formatDate(guildSettings.getMeetingAtIndex(meetingNumber - 1).getDate(), guildSettings.getDateFormats().get(0)), input, false);
            JDAUtils.sendPrivateMessage(user, eb.build());
        } catch (Exception e) {
            JDAUtils.sendPrivateMessage(user, getFailureEmbed());
        }
    }

    
    @Override
    public MessageEmbed.Field info() {
        return new MessageEmbed.Field("Changing a Meeting Description", "```" + KEYWORDS[0] + " <meeting id> <meeting description>```", false);
    }
    
    @Override
    public MessageEmbed getFailureEmbed() {
        EmbedBuilder eb = new EmbedBuilder();
        
        eb.addField(
                "Command Failed",
                "Please use proper syntax:\n"
                        + "```" + KEYWORDS[0] + " <meeting id> <meeting description>```",
                true);
        
        return eb.build();
    }

    
}
