package botmanager.bulletbot.commands;

import botmanager.JDAUtils;
import botmanager.bulletbot.BulletBot;
import botmanager.bulletbot.generic.BulletBotCommandBase;
import java.text.SimpleDateFormat;
import java.util.List;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

/**
 *
 * @author MC_2018 <mc2018.git@gmail.com>
 */

public class JoinDateCommand extends BulletBotCommandBase {

    final String[] KEYWORDS = {
        bot.getPrefix() + "jd",
        bot.getPrefix() + "joindate",
        bot.getPrefix() + "join",
    };
    
    public JoinDateCommand(BulletBot bot) {
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
        
        if (!JDAUtils.hasRole(event.getMember(), "Mod")) {
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
                result += "\nThe account '" + member.getUser().getName() + "' joined on "
                        + JDAUtils.getFormattedUserTimeJoined(member, "MMMMM d, yyyy") + ".";
            }
        } else {
            userID = JDAUtils.findUserId(event.getGuild(), message);

            if (userID != null) {
                result = "The account '" + event.getJDA().getUserById(userID).getName()
                        + "' joined on " + JDAUtils.getFormattedUserTimeJoined(event.getGuild().getMemberById(userID), "MMMMM d, yyyy") + ".";
            } else {
                result = "Use proper syntax please.\n"
                        + bot.getPrefix() + "jd @Username\n"
                        + bot.getPrefix() + "jd Nickname\n"
                        + bot.getPrefix() + "jd 1234567890123456";
            }
        }
        
        JDAUtils.sendGuildMessage(event.getChannel(), result);
    }
    
    @Override
    public String info() {
        return null;
    }

}
