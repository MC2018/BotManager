package botmanager.bots.masspoll.commands.polling;

import botmanager.bots.masspoll.MassPoll;
import botmanager.bots.masspoll.generic.MassPollCommandBase;
import botmanager.bots.masspoll.objects.Poll;
import botmanager.bots.masspoll.objects.PollAccessor;
import botmanager.generic.commands.IPrivateMessageReceivedCommand;
import botmanager.utils.IOUtils;
import botmanager.utils.JDAUtils;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
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

        try (PollAccessor pollAccesser = new PollAccessor(bot, pollID, PollAccessor.PollAccessType.UUID, uuid)) {
            Poll poll = pollAccesser.getPoll();
            String messageContent = message.getContentRaw();

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
    }
    
}
