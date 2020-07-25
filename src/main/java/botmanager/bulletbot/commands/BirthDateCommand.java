package botmanager.bulletbot.commands;

import botmanager.JDAUtils;
import botmanager.bulletbot.BulletBot;
import botmanager.bulletbot.generic.BulletBotCommandBase;
import java.text.SimpleDateFormat;
import java.util.List;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

/**
 *
 * @author MC_2018 <mc2018.git@gmail.com>
 */

public class BirthDateCommand extends BulletBotCommandBase {

    final String[] KEYWORDS = {
        bot.getPrefix() + "bd",
        bot.getPrefix() + "birthdate",
        bot.getPrefix() + "birth",
        bot.getPrefix() + "cd",
        bot.getPrefix() + "createddate",
        bot.getPrefix() + "created"
    };
    
    public BirthDateCommand(BulletBot bot) {
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
        List<User> mentionedUsers;
        
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
            } else if (message.equalsIgnoreCase(keyword)) {
                message = message.replace(keyword, "");
                found = true;
                break;
            }
        }
        
        if (!found) {
            return;
        }
        
        mentionedUsers = event.getMessage().getMentionedUsers();
        
        if (!mentionedUsers.isEmpty()) {
            for (User user : mentionedUsers) {
                result += "\nThe account '" + user.getName() + "' was made on " + JDAUtils.getFormattedUserTimeCreated(user, "MMMMM d, yyyy") + ".";
            }
        } else {
            userID = JDAUtils.findUserId(event.getGuild(), message);

            if (userID != null) {
                result = "The account '" + event.getJDA().getUserById(userID).getName()
                        + "' was made on " + JDAUtils.getFormattedUserTimeCreated(event.getJDA().getUserById(userID), "MMMMM d, yyyy") + ".";
            } else {
                result = "Use proper syntax please.\n"
                        + bot.getPrefix() + "bd @Username\n"
                        + bot.getPrefix() + "bd Nickname\n"
                        + bot.getPrefix() + "bd 1234567890123456";
            }
        }
        
        JDAUtils.sendGuildMessage(event.getChannel(), result);
    }
    
    @Override
    public String info() {
        return null;
    }

}
