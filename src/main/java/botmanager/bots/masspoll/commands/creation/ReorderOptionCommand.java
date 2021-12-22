package botmanager.bots.masspoll.commands.creation;

import botmanager.bots.masspoll.MassPoll;
import botmanager.bots.masspoll.generic.MassPollCommandBase;
import botmanager.bots.masspoll.objects.Poll;
import botmanager.generic.commands.IPrivateMessageReceivedCommand;
import botmanager.utils.JDAUtils;
import botmanager.utils.Utils;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;

public class ReorderOptionCommand extends MassPollCommandBase implements IPrivateMessageReceivedCommand {

    final String[] KEYWORDS = {
            "order option",
            "orderoption",
            "reorder option",
            "reorderoption",
    };

    public ReorderOptionCommand(MassPoll bot) {
        super(bot);
    }

    @Override
    public void runOnPrivateMessage(PrivateMessageReceivedEvent event) {
        Poll poll = bot.pollsBeingCreated.get(event.getAuthor().getId());
        String message = Utils.startsWithReplace(event.getMessage().getContentRaw(), KEYWORDS);
        MessageChannel channel;
        Guild guild;
        int oldIndex, newIndex;

        if (message == null || poll == null) {
            return;
        }

        try {
            oldIndex = Integer.parseInt(message.split(" ")[0]) - 1;
            newIndex = Integer.parseInt(message.split(" ")[1]) - 1;

            poll.reorderOption(oldIndex, newIndex);
        } catch (NumberFormatException | IndexOutOfBoundsException e) {
            JDAUtils.sendPrivateMessage(event.getAuthor(), "You sent an option number that doesn't exist!");
            return;
        }

        guild = event.getJDA().getGuildById(poll.getGuildID());
        channel = event.getChannel();
        poll.sendTestPollMessage(guild, channel);
    }
}
