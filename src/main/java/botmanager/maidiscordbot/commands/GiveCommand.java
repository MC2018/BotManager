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
public class GiveCommand extends MaiDiscordBotCommandBase {
    
    public GiveCommand(BotBase bot) {
        super(bot);
    }
    
    @Override
    public void run(Event genericEvent) {
        GuildMessageReceivedEvent event;
        String[] words;
        String message;
        String id;
        String name;
        int balance;
        int amount;
        
        if (!(genericEvent instanceof GuildMessageReceivedEvent)) {
            return;
        }
        
        event = (GuildMessageReceivedEvent) genericEvent;
        message = event.getMessage().getContentRaw();
        balance = bot.getUserBalance(event.getMember());
        
        if (message.startsWith(bot.getPrefix() + "give ")) {
            message = message.replace(bot.getPrefix() + "give ", "");
        } else {
            return;
        }
        
        words = message.split(" ");
        
        if (words.length < 2) {
            JDAUtils.sendGuildMessage(event.getChannel(), "Proper format: " + "**" + bot.getPrefix() + "give USER AMOUNT**");
            return;
        }
        
        try {
            amount = Integer.parseInt(words[words.length - 1]);
            
            if (balance < amount) {
                JDAUtils.sendGuildMessage(event.getChannel(), "Amount too high, you only have $" + balance + ".");
                return;
            } else if (amount < 1) {
                JDAUtils.sendGuildMessage(event.getChannel(), "Amount too low, it needs to be greater than $0.");
                return;
            }
        } catch (NumberFormatException e) {
            JDAUtils.sendGuildMessage(event.getChannel(), "Proper format: " + "**" + bot.getPrefix() + "give USER AMOUNT**");
            return;
        }
        
        name = combineArrayStopAtIndex(words, words.length - 1);
        id = JDAUtils.findUserId(event.getGuild(), name);
        
        if (id == null) {
            JDAUtils.sendGuildMessage(event.getChannel(), "I could not find the user'" + name + "'.");
            return;
        }
        
        bot.addUserBalance(event.getMember(), amount * -1);
        bot.addUserBalance(event.getGuild().getMemberById(id), amount);
        
        JDAUtils.sendGuildMessage(event.getChannel(),
                event.getMember().getEffectiveName() + " gave "
                + event.getGuild().getMemberById(id).getEffectiveName()
                + " $" + amount + ".");
    }

    @Override
    public String info() {
        return "**" + bot.getPrefix() + "give USER AMOUNT** - gives your money to someone else (don't @ them)";
    }

    public String combineArrayStopAtIndex(String[] array, int index) {
        String result = "";
        
        for (int i = 0; i < index; i++) {
            result += array[i];
            
            if (i + 1 != index) {
                result += " ";
            }
        }
        
        return result;
    }
    
}
