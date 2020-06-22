package botmanager.frostbalance.commands;

import botmanager.Utilities;
import botmanager.frostbalance.generic.FrostbalanceCommandBase;
import botmanager.generic.BotBase;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.io.File;

public class CoupCommand extends FrostbalanceCommandBase {

    final String[] KEYWORDS = {
            bot.getPrefix() + "coup"
    };

    public CoupCommand(BotBase bot) {
        super(bot);
    }

    @Override
    public void run(Event genericEvent) {

        GuildMessageReceivedEvent event;
        String message;
        String id;
        String result, privateResult;
        Member currentOwner;
        boolean found = false, success = false;

        if (!(genericEvent instanceof GuildMessageReceivedEvent)) {
            return;
        }

        event = (GuildMessageReceivedEvent) genericEvent;
        message = event.getMessage().getContentRaw();
        id = event.getAuthor().getId();

        for (String keyword : KEYWORDS) {
            if (message.equalsIgnoreCase(keyword)) {
                message = message.replace(keyword, "");
                found = true;
                break;
            } else if (message.startsWith(keyword + " ")) {
                message = message.replace(keyword + " ", "");
                found = true;
                break;
            }
        }

        if (!found) {
            return;
        }

        try {
            String info = Utilities.read(new File("data/" + bot.getName() + "/" + event.getGuild().getId() + "/owner.csv"));
            currentOwner = event.getGuild().getMember(event.getJDA().getUserById(Utilities.getCSVValueAtIndex(info, 0)));
        } catch (NullPointerException | IllegalArgumentException e) {
            currentOwner = null;
        }

        Member member = event.getGuild().getMemberById(id);

        if (currentOwner == null) {
            result = "**" + event.getMember().getEffectiveName() + "** is the first player to declare themselves leader, " +
                    "and is now leader!";
            privateResult = null;
            success = true;
        } else {
            double influence = bot.getUserInfluence(member);
            double ownerInfluence = bot.getUserInfluence(currentOwner);

            if (influence > ownerInfluence) {
                bot.changeUserInfluence(member, -ownerInfluence);
                bot.changeUserInfluence(currentOwner, -ownerInfluence);
                result = "**" + event.getMember().getEffectiveName() + "** has successfully supplanted **" +
                        currentOwner.getAsMention() + "** as leader, reducing both users' influence and becoming" +
                        " the new leader!";
                privateResult = "*This maneuver has cost you " + ownerInfluence + " influence. " +
                        currentOwner.getEffectiveName() + " has lost **ALL** of their influence.*";
                success = true;
            } else {
                bot.changeUserInfluence(member, -influence);
                bot.changeUserInfluence(currentOwner, -influence);
                result = "**" + event.getMember().getEffectiveName() + "** has attempted a coup on **" +
                        currentOwner.getAsMention() + "**, which has backfired. Both players have lost influence" +
                        " and the leader has not changed.";
                privateResult = "*This maneuver has cost you **ALL** of your influence. " +
                        currentOwner.getEffectiveName() + " has lost " + influence + " of their influence.*";
                success = false;
            }

        }

        if (success) {
            if (currentOwner != null) {
                event.getGuild().removeRoleFromMember(currentOwner, bot.getOwnerRole(event.getGuild())).complete();
            }
            event.getGuild().addRoleToMember(member, bot.getOwnerRole(event.getGuild())).complete();
            bot.updateOwner(event.getGuild(), member.getUser());
        }

        Utilities.sendGuildMessage(event.getChannel(), result);

        if (privateResult != null) {
            Utilities.sendPrivateMessage(event.getAuthor(), privateResult);
        }

    }


    @Override
    public String info() {
        return ""
                + "**" + bot.getPrefix() + "coup** - become server owner; this will drain both your influence and the influence " +
                "of the current owner until one (or both) of you run out. For ties, the existing owner is still owner.";
    }

}
