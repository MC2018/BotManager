package botmanager.bots.suggestionbox.commands;

import botmanager.utils.JDAUtils;
import botmanager.bots.suggestionbox.SuggestionBox;
import botmanager.bots.suggestionbox.generic.SuggestionBoxCommandBase;
import java.io.File;
import java.util.List;
import net.dv8tion.jda.api.entities.Message.Attachment;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

/**
 *
 * @author MC_2018 <mc2018.git@gmail.com>
 */
public class SuggestionCommand extends SuggestionBoxCommandBase {

    public SuggestionCommand(SuggestionBox bot) {
        super(bot);
    }

    @Override
    public void run(Event genericEvent) {
        GuildMessageReceivedEvent event;
        TextChannel channel;
        List<Role> roles;
        List<Attachment> attachments;
        String[] reactionNames = {"thumbsup", "thumbsdown"};
        String message;

        if (!(genericEvent instanceof GuildMessageReceivedEvent)) {
            return;
        }

        event = (GuildMessageReceivedEvent) genericEvent;
        message = event.getMessage().getContentStripped();

        if (event.getChannel().getId().equals("555419413235630121")) {
            return;
        }

        if (message.split(" ").length == 1 && message.startsWith(bot.getPrefix() + "suggest")) {
            JDAUtils.sendGuildMessage(event.getChannel(), "Please include a message to go along with the suggestion.");
            return;
        }
        
        if (!message.startsWith(bot.getPrefix() + "suggest ")) {
            return;
        }

        roles = event.getMember().getRoles();

        for (Role role : roles) {
            if (role.getId().equals("664301501367320592")) {
                return;
            }
        }

        message = message.replaceFirst(bot.getPrefix() + "suggest ", "");
        message = "**Suggestion by " + event.getMember().getAsMention() + ":**\n```" + message + "```";
        channel = event.getGuild().getTextChannelById("570661939819315230");

        if (channel == null) {
            return;
        }

        attachments = event.getMessage().getAttachments();

        if (attachments.isEmpty()) {
            JDAUtils.sendGuildMessage(channel, message, reactionNames);
        } else if (attachments.size() > 1) {
            JDAUtils.sendGuildMessage(event.getChannel(), "Send a max of 1 file.");
            return;
        } else {
            try {
                File tempFile = new File("data/" + bot.getName() + "/temp");
                tempFile.mkdirs();
                tempFile = new File(tempFile.getAbsolutePath() + "/" + attachments.get(0).getFileName());
                attachments.get(0).downloadToFile(tempFile).get();
                channel = event.getGuild().getTextChannelById("570661939819315230");

                if (channel == null) {
                    return;
                }

                JDAUtils.sendGuildMessage(channel, message, reactionNames, tempFile);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        JDAUtils.sendGuildMessage(event.getChannel(), "Thank you for the suggestion, " + event.getMember().getAsMention() + ".");
    }

    @Override
    public String info() {
        return "`" + bot.getPrefix() + "suggest MESSAGE` - creates a suggestion for members to vote on";
    }

}
