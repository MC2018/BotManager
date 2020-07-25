package botmanager.maidiscordbot.commands;

import botmanager.JDAUtils;
import botmanager.generic.BotBase;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import botmanager.maidiscordbot.generic.MaiDiscordBotCommandBase;

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
    
    @Override
    public void run(Event genericEvent) {
        GuildMessageReceivedEvent event;
        String message;
        String result = "";
        int[] weightedOdds = {1, 10, 100, 270, 0};
        int[] rewardMultipliers = {100, 5, 3, 2, 0};
        int noRewardOdds = 0;
        int rewardMultiplier = 0;
        int bet;
        int balance;
        int reward;
        int random = 1000;
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
            JDAUtils.sendGuildMessage(event.getChannel(), info());
        }
        
        try {
            bet = Integer.parseInt(message);
        } catch (NumberFormatException e) {
            JDAUtils.sendGuildMessage(event.getChannel(), "'" + message + "' is not a valid number.");
            return;
        }
        
        if (balance < bet) {
            JDAUtils.sendGuildMessage(event.getChannel(), "You only have $" + balance + ", ntnt.");
            return;
        } else if (bet <= 0) {
            JDAUtils.sendGuildMessage(event.getChannel(), bet + " is too small of a number.");
            return;
        } else if (bet >= 1000000) {
            JDAUtils.sendGuildMessage(event.getChannel(), bet + " is too large of a number (> 1000000).");
            return;
        }
        
        for (int i = 0; i < weightedOdds.length; i++) {
            noRewardOdds += weightedOdds[i];
        }
        
        weightedOdds[weightedOdds.length - 1] = random - noRewardOdds;
        random = (int) (Math.random() * random) + 1;
        
        for (int i = 0; i < weightedOdds.length; i++) {
            int odds = 0;
            
            for (int j = 0; j <= i; j++) {
                odds += weightedOdds[j];
            }
            
            if (odds >= random) {
                rewardMultiplier = rewardMultipliers[i];
                break;
            }
        }
        
        if (rewardMultipliers.length != 5) {
            result = "There seemed to be a problem with the gamble command, send me or my developer a message to have it fixed.";
        } else {
            if (rewardMultiplier == rewardMultipliers[0]) {
                result = "**-===========-\n"
                        + "\n"
                        + rewardMultiplier + "x Multiplier\n"
                        + "\n"
                        + "-============-**\n";
            } else if (rewardMultiplier == rewardMultipliers[1]) {
                result += "**" + rewardMultiplier + "x Multiplier**\n";
            } else if (rewardMultiplier == rewardMultipliers[2]) {
                result += "__" + rewardMultiplier + "x Multiplier__\n";
            } else if (rewardMultiplier == rewardMultipliers[3]) {
                result += "*" + rewardMultiplier + "x Multiplier*\n";
            } else if (rewardMultiplier == rewardMultipliers[4]) {
                result += "*Bad luck, no turnout this time.*\n";
            }
            
            reward = bet * rewardMultiplier;
            result += event.getMember().getEffectiveName() + " bet $" + bet;

            if (rewardMultiplier != 0) {
                result += " and got back $" + reward + ".";
            } else {
                result += " and lost it all.";
            }

            reward -= bet;
            bot.addUserBalance(event.getMember(), reward);
        }
        
        JDAUtils.sendGuildMessage(event.getChannel(), result);
    }

    @Override
    public String info() {
        return "**" + bot.getPrefix() + "gamble AMOUNT** - lets you waste your money";
    }

}
