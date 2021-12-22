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

public class EditOptionCommand extends MassPollCommandBase implements IPrivateMessageReceivedCommand {

    final String[] KEYWORDS = {
            "edit option",
            "editoption",
            "change option",
            "changeoption",
            "set option",
            "setoption"
    };

    public EditOptionCommand(MassPoll bot) {
        super(bot);
    }

    @Override
    public void runOnPrivateMessage(PrivateMessageReceivedEvent event) {
        Poll poll = bot.pollsBeingCreated.get(event.getAuthor().getId());
        String message = Utils.startsWithReplace(event.getMessage().getContentRaw(), KEYWORDS);
        MessageChannel channel;
        Guild guild;
        int index;

        if (Utils.isNullOrEmpty(message) || poll == null) {
            return;
        }

        try {
            int number = Integer.parseInt(message.split(" ")[0]);

            index = number - 1;
            message = message.replaceFirst(number + " ", "");
        } catch (NumberFormatException e) {
            JDAUtils.sendPrivateMessage(event.getAuthor(), "You used the command incorrectly.");
            return;
        }

        try {
            poll.editOption(index, message);
        } catch (Exception e) {
            JDAUtils.sendPrivateMessage(event.getAuthor(), e.getMessage());
            return;
        }

        guild = event.getJDA().getGuildById(poll.getGuildID());
        channel = event.getChannel();
        poll.sendTestPollMessage(guild, channel);
    }
}
