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
public class CoinflipCommand extends MaiDiscordBotCommandBase {

    public final String[] KEYWORDS = {
        bot.getPrefix() + "coinflip",
        bot.getPrefix() + "flip",
        bot.getPrefix() + "f"
    };
    
    public CoinflipCommand(BotBase bot) {
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
        
        if (random < 490) {
            result = event.getMember().getEffectiveName() + " flipped heads and won $" + bet + "!";
            reward = bet;
        } else {
            result = event.getMember().getEffectiveName() + " flipped tails and lost $" + bet + "...";
            reward = bet * -1;
        }
        
        bot.addUserBalance(event.getMember(), reward);
        JDAUtils.sendGuildMessage(event.getChannel(), result);
    }

    @Override
    public String info() {
        return "**" + bot.getPrefix() + "coinflip AMOUNT** - gives you a coinflip's shot of doubling cash";
    }

}
