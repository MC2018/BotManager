package botmanager.maidiscordbot.commands;

import botmanager.JDAUtils;
import botmanager.generic.BotBase;
import java.text.SimpleDateFormat;
import java.util.Date;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import botmanager.maidiscordbot.generic.MaiDiscordBotCommandBase;

/**
 *
 * @author MC_2018 <mc2018.git@gmail.com>
 */

public class DailyRewardCommand extends MaiDiscordBotCommandBase {

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
            int reward = (int) (Math.random() * 150) + 100;
            bot.addUserBalance(event.getMember(), reward);
            bot.setUserDaily(event.getMember(), date);
            JDAUtils.sendGuildMessage(event.getChannel(), event.getMember().getEffectiveName() + ", here is $" + reward + "!");
        } else {
            int hrsDelay = (24 - Integer.parseInt(hours.format(new Date())));
            JDAUtils.sendGuildMessage(event.getChannel(), event.getMember().getEffectiveName() + ", try again at midnight EST "
                    + "(around " + hrsDelay + " hour" + (hrsDelay > 1 ? "s" : "") + ").");
        }
    }

    @Override
    public String info() {
        return "**" + bot.getPrefix() + "daily** - gives you $ once a day";
    }

}
