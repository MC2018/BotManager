package botmanager.bots.masspoll.commands.creation;

import botmanager.bots.masspoll.MassPoll;
import botmanager.bots.masspoll.generic.MassPollCommandBase;
import botmanager.bots.masspoll.objects.Poll;
import botmanager.generic.commands.IPrivateMessageReceivedCommand;
import botmanager.utils.JDAUtils;
import botmanager.utils.Utils;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;

public class RemoveOptionCommand extends MassPollCommandBase implements IPrivateMessageReceivedCommand {

    final String[] KEYWORDS = {
            "remove option",
            "removeoption"
    };

    public RemoveOptionCommand(MassPoll bot) {
        super(bot);
    }

    @Override
    public void runOnPrivateMessage(PrivateMessageReceivedEvent event) {
        Poll poll = bot.POLLS_BEING_CREATED.get(event.getAuthor().getId());
        String message = Utils.startsWithReplace(event.getMessage().getContentRaw(), KEYWORDS);
        MessageChannel channel;
        Guild guild;
        int index;

        if (message == null || poll == null) {
            return;
        }

        try {
            index = Integer.parseInt(message) - 1;
            poll.removeOption(index);
        } catch (NumberFormatException | IndexOutOfBoundsException e) {
            JDAUtils.sendPrivateMessage(event.getAuthor(), "You sent an option number that doesn't exist!");
            return;
        }

        guild = event.getJDA().getGuildById(poll.getGuildID());
        channel = event.getChannel();
        poll.sendTestPollMessage(guild, channel);
    }
}
