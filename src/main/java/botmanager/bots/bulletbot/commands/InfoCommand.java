package botmanager.bots.bulletbot.commands;

import botmanager.generic.commands.IMessageReceivedCommand;
import botmanager.utils.JDAUtils;
import botmanager.bots.bulletbot.BulletBot;
import botmanager.bots.bulletbot.generic.BulletBotCommandBase;
import java.text.SimpleDateFormat;
import java.util.List;

import botmanager.utils.Utils;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

/**
 *
 * @author MC_2018 <mc2018.git@gmail.com>
 */

public class InfoCommand extends BulletBotCommandBase implements IMessageReceivedCommand {

    final String[] KEYWORDS = {
        bot.getPrefix() + "info",
        bot.getPrefix() + "i",
    };
    
    public InfoCommand(BulletBot bot) {
        super(bot);
    }

    @Override
    public void runOnMessage(MessageReceivedEvent event) {
        String message = Utils.startsWithReplace(event.getMessage().getContentRaw(), KEYWORDS);
        String result = "";
        String userID;
        List<Member> mentionedMembers;
        
        if (message == null || !event.isFromGuild() || !JDAUtils.hasRole(event.getMember(), "Mod")) {
            return;
        }
        
        mentionedMembers = event.getMessage().getMentionedMembers();
        
        if (!mentionedMembers.isEmpty()) {
            for (Member member : mentionedMembers) {
                result += "\nThe account '" + member.getUser().getName() + "' was made on "
                        + JDAUtils.getFormattedUserTimeCreated(member.getUser(), "MMMMM d, yyyy") + ".";
                result += "\nThe account '" + member.getUser().getName() + "' joined on "
                        + JDAUtils.getFormattedUserTimeJoined(member, "MMMMM d, yyyy") + ".";
            }
        } else {
            userID = JDAUtils.findMemberID(event.getGuild(), message);

            if (userID != null) {
                result += "\nThe account '" + event.getJDA().getUserById(userID).getName()
                        + "' was made on " + JDAUtils.getFormattedUserTimeCreated(event.getJDA().getUserById(userID), "MMMMM d, yyyy") + ".";
                result += "\nThe account '" + event.getJDA().getUserById(userID).getName()
                        + "' joined on " + JDAUtils.getFormattedUserTimeJoined(event.getGuild().getMemberById(userID), "MMMMM d, yyyy") + ".";
            } else {
                result += "Use proper syntax please.\n"
                        + bot.getPrefix() + "info @Username\n"
                        + bot.getPrefix() + "info Nickname\n"
                        + bot.getPrefix() + "info 1234567890123456";
            }
        }
        
        JDAUtils.sendGuildMessage(event.getTextChannel(), result);
    }
    
    @Override
    public String info() {
        return null;
    }

}
