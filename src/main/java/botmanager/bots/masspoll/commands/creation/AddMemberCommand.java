package botmanager.bots.masspoll.commands.creation;

import botmanager.bots.masspoll.MassPoll;
import botmanager.bots.masspoll.generic.MassPollCommandBase;
import botmanager.bots.masspoll.objects.Poll;
import botmanager.generic.commands.IPrivateMessageReceivedCommand;
import botmanager.utils.JDAUtils;
import botmanager.utils.Utils;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;

public class AddMemberCommand extends MassPollCommandBase implements IPrivateMessageReceivedCommand {

    final String[] KEYWORDS = {
            "add member",
            "addmember",
            "add user",
            "adduser"
    };

    public AddMemberCommand(MassPoll bot) {
        super(bot);
    }

    @Override
    public void runOnPrivateMessage(PrivateMessageReceivedEvent event) {
        Poll poll = bot.POLLS_BEING_CREATED.get(event.getAuthor().getId());
        String message = Utils.startsWithReplace(event.getMessage().getContentRaw(), KEYWORDS);
        Guild guild;
        Member member, memberToPoll;

        if (Utils.isNullOrEmpty(message) || poll == null) {
            return;
        }

        guild = event.getJDA().getGuildById(poll.getGuildID());
        member = guild.getMember(event.getAuthor());
        memberToPoll = JDAUtils.findSimilarMemberWithName(guild, message);

        poll.addMemberToPoll(memberToPoll);
        poll.sendRoleSelectorMessage(member, JDAUtils.roleIDsToRoles(guild, poll.getRolesToChooseFrom()), event.getChannel());
    }

}
