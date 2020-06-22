package botmanager.frostbalance.commands;

import botmanager.Utilities;
import botmanager.frostbalance.generic.FrostbalanceCommandBase;
import botmanager.generic.BotBase;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.io.File;

public class TransferCommand extends FrostbalanceCommandBase {

    final String[] KEYWORDS = {
            bot.getPrefix() + "transfer"
    };

    public TransferCommand(BotBase bot) {
        super(bot);
    }

    @Override
    public void run(Event genericEvent) {

        GuildMessageReceivedEvent event;
        String message;
        String id;
        String result;
        Member currentOwner;
        boolean found = false;

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

        if (!member.equals(currentOwner)) {
            result = "You have to be the owner to transfer ownership of the server peaceably.";

            Utilities.sendGuildMessage(event.getChannel(), result);
            return;
        }


        id = Utilities.findUserId(event.getGuild(), message);

        if (id == null) {
            Utilities.sendGuildMessage(event.getChannel(), "Couldn't find user'" + message + "'.");
            return;
        }

        if (currentOwner != null) {
            event.getGuild().removeRoleFromMember(currentOwner, bot.getOwnerRole(event.getGuild())).complete();
        }
        Member newOwner = event.getGuild().getMemberById(id);
        event.getGuild().addRoleToMember(newOwner, bot.getOwnerRole(event.getGuild())).complete();
        bot.updateOwner(event.getGuild(), newOwner.getUser());

        result = "**" + currentOwner.getEffectiveName() + "** has transferred ownership to " +
                newOwner.getAsMention() + " for this server.";
        Utilities.sendGuildMessage(event.getChannel(), result);

    }

    @Override
    public String info() {
        return ""
                + "**" + bot.getPrefix() + "transfer USER** - makes someone else server owner.";
    }

}
