package botmanager.frostbalance.commands;

import botmanager.Utilities;
import botmanager.frostbalance.generic.FrostbalanceCommandBase;
import botmanager.generic.BotBase;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author MC_2018 <mc2018.git@gmail.com>
 */

public class DailyRewardCommand extends FrostbalanceCommandBase {

    SimpleDateFormat sdf = new SimpleDateFormat("MMddyyyy");
    SimpleDateFormat hours = new SimpleDateFormat("HH");
    
    public DailyRewardCommand(BotBase bot) {
        super(bot);
    }

    @Override
    public void run(Event genericEvent) {
        GuildMessageReceivedEvent event;
        String message;
        int date;
        
        if (!(genericEvent instanceof GuildMessageReceivedEvent)) {
            return;
        }
        
        event = (GuildMessageReceivedEvent) genericEvent;
        message = event.getMessage().getContentRaw();
        
        if (!message.equalsIgnoreCase(bot.getPrefix() + "daily")) {
            return;
        }
        
        date = Integer.parseInt(sdf.format(new Date()));
        
        if (bot.getUserDaily(event.getMember()) != date) {
            bot.setUserDaily(event.getMember(), date);
            bot.changeUserInfluence(event.getMember(), 1);
            Utilities.sendGuildMessage(event.getChannel(), event.getMember().getEffectiveName() + ", your influence increases.");
        } else {
            int hrsDelay = (24 - Integer.parseInt(hours.format(new Date())));
            Utilities.sendGuildMessage(event.getChannel(), event.getMember().getEffectiveName() + ", try again at midnight EST "
                    + "(around " + hrsDelay + " hour" + (hrsDelay > 1 ? "s" : "") + ").");
        }
    }

    @Override
    public String info() {
        return "**" + bot.getPrefix() + "daily** - gives you influence once a day";
    }

}
