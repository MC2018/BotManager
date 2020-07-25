package botmanager.suggestionbox.commands;

import botmanager.JDAUtils;
import botmanager.suggestionbox.SuggestionBox;
import botmanager.suggestionbox.generic.SuggestionBoxCommandBase;
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
        String[] reactionNames = {"upvote", "downvote"};
        String message;

        if (!(genericEvent instanceof GuildMessageReceivedEvent)) {
            return;
        }

        event = (GuildMessageReceivedEvent) genericEvent;
        message = event.getMessage().getContentStripped();

        if (message.split(" ").length == 1 && message.startsWith(bot.getPrefix() + "suggest") && event.getChannel().getName().equalsIgnoreCase("bot-commands")) {
            JDAUtils.sendGuildMessage(event.getChannel(), "Please include a message to go along with the suggestion.");
            return;
        }
        
        if (!message.startsWith(bot.getPrefix() + "suggest ") || !event.getChannel().getName().equalsIgnoreCase("bot-commands")) {
            return;
        }

        roles = event.getMember().getRoles();

        for (Role role : roles) {
            if (role.getName().equalsIgnoreCase("Suggestion-Ban")) {
                return;
            }
        }

        message = message.replaceFirst(bot.getPrefix() + "suggest ", "");
        message = "**Suggestion by " + event.getMember().getAsMention() + ":**\n```" + message + "```";
        channel = (TextChannel) JDAUtils.findChannelByName(event.getGuild(), "user-suggestions");

        if (channel == null) {
            return;
        }

        attachments = event.getMessage().getAttachments();

        if (attachments.isEmpty()) {
            JDAUtils.sendGuildMessageWithReactions(channel, message, reactionNames);
        } else if (attachments.size() > 1) {
            JDAUtils.sendGuildMessage(event.getChannel(), "Send a max of 1 file.");
            return;
        } else {
            try {
                File tempFile = new File("data/" + bot.getName() + "/temp");
                tempFile.mkdirs();
                tempFile = new File(tempFile.getAbsolutePath() + "/" + attachments.get(0).getFileName());
                attachments.get(0).downloadToFile(tempFile).get();
                channel = (TextChannel) JDAUtils.findChannelByName(event.getGuild(), "emote-suggestions");
                if (channel == null) {
                    return;
                }
                JDAUtils.sendGuildMessageWithReactions(channel, message, reactionNames, tempFile);
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
