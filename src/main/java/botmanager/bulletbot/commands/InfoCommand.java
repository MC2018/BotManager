package botmanager.bulletbot.commands;

import botmanager.bulletbot.BulletBot;
import botmanager.bulletbot.generic.BulletBotCommandBase;
import botmanager.Utilities;
import java.text.SimpleDateFormat;
import java.util.List;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

/**
 *
 * @author MC_2018 <mc2018.git@gmail.com>
 */

public class InfoCommand extends BulletBotCommandBase {

    final String[] KEYWORDS = {
        bot.getPrefix() + "info",
        bot.getPrefix() + "i",
    };
    
    public InfoCommand(BulletBot bot) {
        super(bot);
    }

    @Override
    public void run(Event genericEvent) {
        GuildMessageReceivedEvent event;
        String message;
        String result = "";
        String userID;
        SimpleDateFormat sdf = new SimpleDateFormat("MMMMM d, yyyy");
        boolean found = false;
        List<Member> mentionedMembers;
        
        if (!(genericEvent instanceof GuildMessageReceivedEvent)) {
            return;
        }
        
        event = (GuildMessageReceivedEvent) genericEvent;
        
        if (!Utilities.hasRole(event.getMember(), "Mod")) {
            return;
        }
        
        message = event.getMessage().getContentRaw();
        
        for (String keyword : KEYWORDS) {
            if (message.startsWith(keyword + " ")) {
                message = message.replace(keyword + " ", "");
                found = true;
                break;
            } else if (message.startsWith(keyword)) {
                message = message.replace(keyword, "");
                found = true;
                break;
            }
        }
        
        if (!found) {
            return;
        }
        
        mentionedMembers = event.getMessage().getMentionedMembers();
        
        if (!mentionedMembers.isEmpty()) {
            for (Member member : mentionedMembers) {
                result += "\nThe account '" + member.getUser().getName() + "' was made on "
                        + Utilities.getFormattedUserTimeCreated(member.getUser(), "MMMMM d, yyyy") + ".";
                result += "\nThe account '" + member.getUser().getName() + "' joined on "
                        + Utilities.getFormattedUserTimeJoined(member, "MMMMM d, yyyy") + ".";
            }
        } else {
            userID = Utilities.findUserId(event.getGuild(), message);

            if (userID != null) {
                result += "\nThe account '" + event.getJDA().getUserById(userID).getName()
                        + "' was made on " + Utilities.getFormattedUserTimeCreated(event.getJDA().getUserById(userID), "MMMMM d, yyyy") + ".";
                result += "\nThe account '" + event.getJDA().getUserById(userID).getName()
                        + "' joined on " + Utilities.getFormattedUserTimeJoined(event.getGuild().getMemberById(userID), "MMMMM d, yyyy") + ".";
            } else {
                result += "Use proper syntax please.\n"
                        + bot.getPrefix() + "info @Username\n"
                        + bot.getPrefix() + "info Nickname\n"
                        + bot.getPrefix() + "info 1234567890123456";
            }
        }
        
        Utilities.sendGuildMessage(event.getChannel(), result);
    }
    
    @Override
    public String info() {
        return null;
    }

}
