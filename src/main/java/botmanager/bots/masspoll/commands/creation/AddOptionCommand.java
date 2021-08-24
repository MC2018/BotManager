package botmanager.bots.masspoll.commands.creation;

import botmanager.bots.masspoll.MassPoll;
import botmanager.bots.masspoll.generic.MassPollCommandBase;
import botmanager.bots.masspoll.objects.Poll;
import botmanager.generic.commands.IPrivateMessageReceivedCommand;
import botmanager.utils.JDAUtils;
import botmanager.utils.Utils;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;

public class AddOptionCommand extends MassPollCommandBase implements IPrivateMessageReceivedCommand {

    final String[] KEYWORDS = {
            "add option",
            "addoption"
    };

    public AddOptionCommand(MassPoll bot) {
        super(bot);
    }

    @Override
    public void runOnPrivateMessage(PrivateMessageReceivedEvent event) {
        Poll poll = bot.pollsBeingCreated.get(event.getAuthor().getId());
        String message = Utils.startsWithReplace(event.getMessage().getContentRaw(), KEYWORDS);

        if (message == null || poll == null) {
            return;
        } else if (poll.getOptionsLength() + message.length() + 1 > Message.MAX_CONTENT_LENGTH) {
            JDAUtils.sendPrivateMessage(event.getAuthor(), "You are using too many characters amongst all of your options.");
        } else {
            try {
                poll.addOption(message);
            } catch (IndexOutOfBoundsException e) {
                JDAUtils.sendPrivateMessage(event.getAuthor(), e.getMessage());
            }
        }

        poll.sendExampleMessageEmbed(bot, event.getAuthor());
    }
}
