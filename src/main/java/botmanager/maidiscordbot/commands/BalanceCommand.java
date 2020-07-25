package botmanager.maidiscordbot.commands;

import botmanager.JDAUtils;
import botmanager.generic.BotBase;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import botmanager.maidiscordbot.generic.MaiDiscordBotCommandBase;

/**
 *
 * @author MC_2018 <mc2018.git@gmail.com>
 */
public class BalanceCommand extends MaiDiscordBotCommandBase {

    final String[] KEYWORDS = {
        bot.getPrefix() + "balance",
        bot.getPrefix() + "bal",
    };
    
    public BalanceCommand(BotBase bot) {
        super(bot);
    }

    @Override
    public void run(Event genericEvent) {
        GuildMessageReceivedEvent event;
        String message;
        String id;
        String result;
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
        
        if (message.length() > 0) {
            id = JDAUtils.findUserId(event.getGuild(), message);
        }
        
        if (id == null) {
            result = "User '" + message + "' could not be found."; 
        } else {
            Member member = event.getGuild().getMemberById(id);
            int balance = bot.getUserBalance(member);
            
            if (balance == 0 && message.length() == 0) {
                result = member.getEffectiveName() + ", you're broke. Now stop spamming this command.";
            } else {
                result = "Balance of " + member.getEffectiveName() + " is $" + balance + ".";
            }
        }
        
        JDAUtils.sendGuildMessage(event.getChannel(), result);
    }

    @Override
    public String info() {
        return ""
                + "**" + bot.getPrefix() + "bal** - states your balance\n"
                + "**" + bot.getPrefix() + "bal USER** - states someone else's balance (don't @ them)";
    }
    
}
