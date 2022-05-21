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
        Poll poll = bot.POLLS_BEING_CREATED.get(event.getAuthor().getId());
        String message = Utils.startsWithReplace(event.getMessage().getContentRaw(), KEYWORDS);
        MessageChannel channel;
        Guild guild;

        if (message == null || poll == null) {
            return;
        }

        if (message.length() > 2000) {
            JDAUtils.sendPrivateMessage(event.getAuthor(), "You cannot set a question with more than 2000 characters!");
            return;
        }


        guild = event.getJDA().getGuildById(poll.getGuildID());
        channel = event.getChannel();
        poll.setQuestion(message);
        poll.sendTestPollMessage(guild, channel);
    }

}
