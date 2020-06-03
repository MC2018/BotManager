package botmanager.maidiscordbot.commands;

import botmanager.generic.BotBase;
import botmanager.Utilities;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import botmanager.maidiscordbot.generic.MaiDiscordBotCommandBase;
import botmanager.maidiscordbot.MaiDiscordBot;

/**
 *
 * @author MC_2018 <mc2018.git@gmail.com>
 */
public class GambleCommand extends MaiDiscordBotCommandBase {

    public final String[] KEYWORDS = {
        bot.getPrefix() + "gamble",
        bot.getPrefix() + "bet",
        bot.getPrefix() + "b",
        bot.getPrefix() + "g"
    };
    
    public GambleCommand(BotBase bot) {
        super(bot);
    }
//35*2+4*3+5+0.1*100
    //make it slow, ...typing, editing previous message to add tension? make sure gambling value is set before waiting
    @Override
    public void run(Event genericEvent) {
        GuildMessageReceivedEvent event;
        String message;
        String result = "";
        int bet;
        int balance;
        int reward;
        int random = (int) (Math.random() * 1000) + 1;
        boolean found = false;
        
        if (!(genericEvent instanceof GuildMessageReceivedEvent)) {
            return;
        }
        
        event = (GuildMessageReceivedEvent) genericEvent;
        message = event.getMessage().getContentRaw();
        balance = bot.getUserBalance(event.getMember());
        
        for (String keyword : KEYWORDS) {
            if (message.startsWith(keyword + " ")) {
                message = message.replaceFirst(keyword + " ", "");
                found = true;
                break;
            } else if (message.equalsIgnoreCase(keyword)) {
                message = message.replaceFirst(keyword, "");
                found = true;
                break;
            }
        }
        
        if (!found) {
            return;
        } else if (message.equals("")) {
            Utilities.sendGuildMessage(event.getChannel(), info());
        }
        
        try {
            bet = Integer.parseInt(message);
        } catch (NumberFormatException e) {
            Utilities.sendGuildMessage(event.getChannel(), "'" + message + "' is not a valid number.");
            return;
        }
        
        if (balance < bet) {
            Utilities.sendGuildMessage(event.getChannel(), "You only have $" + balance + ", ntnt.");
            return;
        } else if (bet <= 0) {
            Utilities.sendGuildMessage(event.getChannel(), bet + " is too small of a number.");
            return;
        } else if (bet >= 1000000) {
            Utilities.sendGuildMessage(event.getChannel(), bet + " is too large of a number (> 1000000).");
            return;
        }
        
        if (random == 1000) {
            reward = bet * 100;
            result = "**-===========-\n"
                    + "\n"
                    + "100x Multiplier\n"
                    + "\n"
                    + "-============-**\n";
        } else if (random / 10 >= 99) {
            reward = bet * 5;
            result += "**5x Multiplier**\n";
        } else if (random / 10 >= 92) {
            reward = bet * 3;
            result += "__3x Multiplier__\n";
        } else if (random / 10 >= 62) {
            reward = bet * 2;
            result += "*2x Multiplier*\n";
        } else {
            reward = 0;
            result += "*Bad luck, no turnout this time.*\n";
        }
        
        result += event.getMember().getEffectiveName() + " bet $" + bet;
        
        if (random / 10 >= 62) {
            result += " and got back $" + reward + ".";
        } else {
            result += " and lost it all.";
        }
        
        reward -= bet;
        bot.addUserBalance(event.getMember(), reward);
        
        Utilities.sendGuildMessage(event.getChannel(), result);
    }

    @Override
    public String info() {
        return "**" + bot.getPrefix() + "gamble AMOUNT** - lets you waste your money";
    }

}
