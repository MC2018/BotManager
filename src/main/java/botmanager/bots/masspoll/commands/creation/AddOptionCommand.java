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
        Poll poll = bot.POLLS_BEING_CREATED.get(event.getAuthor().getId());
        String message = Utils.startsWithReplace(event.getMessage().getContentRaw(), KEYWORDS);
        MessageChannel channel;
        Guild guild;

        if (Utils.isNullOrEmpty(message) || poll == null) {
            return;
        } else if (poll.getOptionsLength() + message.length() + 1 > MessageEmbed.VALUE_MAX_LENGTH) {
            JDAUtils.sendPrivateMessage(event.getAuthor(), "You are using too many characters amongst all of your options.");
            return;
        }

        try {
            poll.addOption(message);
        } catch (IndexOutOfBoundsException e) {
            JDAUtils.sendPrivateMessage(event.getAuthor(), e.getMessage());
        }

        guild = event.getJDA().getGuildById(poll.getGuildID());
        channel = event.getChannel();
        poll.sendTestPollMessage(guild, channel);
    }
}
