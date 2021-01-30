package botmanager.bots.bulletbot.commands;

import botmanager.generic.commands.IMessageReceivedCommand;
import botmanager.utils.JDAUtils;
import botmanager.bots.bulletbot.BulletBot;
import botmanager.bots.bulletbot.generic.BulletBotCommandBase;
import java.text.SimpleDateFormat;
import java.util.List;

import botmanager.utils.Utils;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

/**
 *
 * @author MC_2018 <mc2018.git@gmail.com>
 */

public class BirthDateCommand extends BulletBotCommandBase implements IMessageReceivedCommand {

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
    public void runOnMessage(MessageReceivedEvent event) {
        String input = Utils.startsWithReplace(event.getMessage().getContentRaw(), KEYWORDS);
        String result = "";
        String userID;
        List<User> mentionedUsers;
        
        if (input == null || !event.isFromGuild() || !JDAUtils.hasRole(event.getMember(), "Mod")) {
            return;
        }

        mentionedUsers = event.getMessage().getMentionedUsers();
        
        if (!mentionedUsers.isEmpty()) {
            for (User user : mentionedUsers) {
                result += "\nThe account '" + user.getName() + "' was made on " + JDAUtils.getFormattedUserTimeCreated(user, "MMMMM d, yyyy") + ".";
            }
        } else {
            userID = JDAUtils.findMemberID(event.getGuild(), input);

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
        
        JDAUtils.sendGuildMessage(event.getTextChannel(), result);
    }
    
    @Override
    public String info() {
        return null;
    }

}
