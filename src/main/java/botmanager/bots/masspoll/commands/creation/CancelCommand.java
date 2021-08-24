package botmanager.bots.masspoll.commands.creation;

import botmanager.bots.masspoll.MassPoll;
import botmanager.bots.masspoll.generic.MassPollCommandBase;
import botmanager.bots.masspoll.objects.Poll;
import botmanager.generic.commands.IPrivateMessageReceivedCommand;
import botmanager.utils.JDAUtils;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;

public class CancelCommand extends MassPollCommandBase implements IPrivateMessageReceivedCommand {

    public CancelCommand(MassPoll bot) {
        super(bot);
    }

    @Override
    public void runOnPrivateMessage(PrivateMessageReceivedEvent event) {
        String message = event.getMessage().getContentRaw();
        MessageChannel channel;
        Poll poll = bot.pollsBeingCreated.get(event.getAuthor().getId());
        User user;

        if (!message.equalsIgnoreCase("cancel") || poll == null) {
            return;
        }

        user = event.getAuthor();
        channel = user.openPrivateChannel().complete();

        if (!poll.getLastCreatorMessageID().equals("0")) {
            channel.deleteMessageById(poll.getLastCreatorMessageID()).complete();
        }

        bot.pollsBeingCreated.remove(event.getAuthor().getId());
        JDAUtils.sendMessage(channel, "The poll has been cancelled.", null, null, null, false);
    }
}