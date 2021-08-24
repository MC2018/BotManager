package botmanager.bots.masspoll.commands.polling;

import botmanager.bots.masspoll.MassPoll;
import botmanager.bots.masspoll.generic.MassPollCommandBase;
import botmanager.bots.masspoll.objects.Poll;
import botmanager.generic.commands.IMessageReactionAddCommand;
import botmanager.generic.commands.IMessageReactionRemoveCommand;
import botmanager.generic.commands.IPrivateMessageReceivedCommand;
import botmanager.utils.IOUtils;
import botmanager.utils.JDAUtils;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent;
import net.dv8tion.jda.api.interactions.components.Button;

import java.util.List;

public class CommentCommand extends MassPollCommandBase implements IPrivateMessageReceivedCommand {

    public CommentCommand(MassPoll bot) {
        super(bot);
    }

    @Override
    public void runOnPrivateMessage(PrivateMessageReceivedEvent event) {
        String selfBotID, uuid;
        Message message, referencedMessage;
        long pollID = 0;
        int index = -1;

        message = event.getMessage();
        referencedMessage = message.getReferencedMessage();
        selfBotID = bot.getJDA().getSelfUser().getId();

        if (message.getAuthor().getId().equals(selfBotID)
                || referencedMessage == null
                || !referencedMessage.getAuthor().getId().equals(selfBotID)) {
            return;
        }

        try {
            List<Button> buttons = referencedMessage.getButtons();
            String[] buttonIdentifiers;

            if (buttons.size() < 1) {
                return;
            }

            buttonIdentifiers = buttons.get(0).getId().split("_");
            pollID = Long.parseLong(buttonIdentifiers[0]);
            uuid = buttonIdentifiers[1];
        } catch (NumberFormatException e) {
            return;
        } catch (Exception e) {
            return;
        }

        // wait until saving has completed
        // bad, ik, but I want to make sure no data is lost w/the tools I'm using
        while (bot.pollsInProcess.contains(pollID)) {
        }

        bot.pollsInProcess.add(pollID);

        try {
            Poll poll = IOUtils.readGson(Poll.getFileLocation(bot, pollID), Poll.class);
            String messageContent;

            if (!poll.getUUID().equals(uuid)) {
                bot.pollsInProcess.remove(pollID);
                return;
            }

            messageContent = message.getContentRaw();

            if (messageContent == null) {
                messageContent = "";
            }

            if (index < poll.getOptionsSize()) {
                poll.addUserComment(event.getAuthor().getId(), messageContent);
                IOUtils.writeGson(Poll.getFileLocation(bot, pollID), poll, true);
                JDAUtils.sendPrivateMessage(event.getAuthor(), "Comment collected!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        bot.pollsInProcess.remove(pollID);
    }
    
}
