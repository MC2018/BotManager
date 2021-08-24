package botmanager.bots.masspoll.commands.creation;

import botmanager.bots.masspoll.MassPoll;
import botmanager.bots.masspoll.generic.MassPollCommandBase;
import botmanager.bots.masspoll.objects.Poll;
import botmanager.generic.commands.IPrivateMessageReceivedCommand;
import botmanager.utils.Utils;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;

public class QuestionCommand extends MassPollCommandBase implements IPrivateMessageReceivedCommand {

    final String[] KEYWORDS = {
            "set question",
            "setquestion",
            "question"
    };

    public QuestionCommand(MassPoll bot) {
        super(bot);
    }

    @Override
    public void runOnPrivateMessage(PrivateMessageReceivedEvent event) {
        Poll poll = bot.pollsBeingCreated.get(event.getAuthor().getId());
        String message = Utils.startsWithReplace(event.getMessage().getContentRaw(), KEYWORDS);

        if (message == null || poll == null) {
            return;
        }

        poll.setQuestion(message);
        poll.sendExampleMessageEmbed(bot, event.getAuthor());
    }

}
