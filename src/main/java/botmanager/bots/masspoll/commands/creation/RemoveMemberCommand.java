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

public class RemoveMemberCommand extends MassPollCommandBase implements IPrivateMessageReceivedCommand {

    final String[] KEYWORDS = {
            "remove member",
            "removemember",
            "remove user",
            "removeuser"
    };

    public RemoveMemberCommand(MassPoll bot) {
        super(bot);
    }

    @Override
    public void runOnPrivateMessage(PrivateMessageReceivedEvent event) {
        Poll poll = bot.pollsBeingCreated.get(event.getAuthor().getId());
        String message = Utils.startsWithReplace(event.getMessage().getContentRaw(), KEYWORDS);
        Guild guild;
        Member member, memberToPoll;

        if (message == null || poll == null) {
            return;
        }

        guild = event.getJDA().getGuildById(poll.getGuildID());
        member = guild.getMember(event.getAuthor());
        memberToPoll = JDAUtils.findSimilarMemberWithName(guild, message);

        poll.removeMemberToPoll(memberToPoll.getId());
        poll.sendRoleSelectorMessage(member, JDAUtils.roleIDsToRoles(guild, poll.getRolesToChooseFrom()), event.getChannel());
    }
}
