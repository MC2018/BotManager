package botmanager.frostbalance.commands;

import botmanager.Utilities;
import botmanager.frostbalance.generic.FrostbalanceCommandBase;
import botmanager.generic.BotBase;
import botmanager.maidiscordbot.generic.MaiDiscordBotCommandBase;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

/**
 *
 * @author MC_2018 <mc2018.git@gmail.com>
 */
public class InfluenceCommand extends FrostbalanceCommandBase {

    final String[] KEYWORDS = {
        bot.getPrefix() + "influence"
    };

    public InfluenceCommand(BotBase bot) {
        super(bot);
    }

    @Override
    public void run(Event genericEvent) {
        GuildMessageReceivedEvent event;
        String message;
        String id;
        String result, privateResult;
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
            result = "If you want to find the influence of a different player, you must ask them.\n"
                + "Your influence has been sent to you via PM.";
        } else {
            result = "Your influence has been sent to you via PM.";
        }

        Member member = event.getGuild().getMemberById(id);
        double influence = bot.getUserInfluence(member);

        if (influence <= 0 && message.length() == 0) {
            privateResult = "You have *no* influence in **" + event.getGuild().getName() + "**.";
        } else {
            privateResult = "You have **" + String.format("%.2f", influence) + "** influence in **" + event.getGuild().getName() + "**.";
        }
        
        Utilities.sendGuildMessage(event.getChannel(), result);
        Utilities.sendPrivateMessage(event.getAuthor(), privateResult);
    }

    @Override
    public String info() {
        return ""
                + "**" + bot.getPrefix() + "influence** - sends your influence on this server via PM";
    }
    
}
