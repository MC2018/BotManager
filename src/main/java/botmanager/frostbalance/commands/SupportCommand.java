package botmanager.frostbalance.commands;

import botmanager.Utilities;
import botmanager.frostbalance.generic.FrostbalanceCommandBase;
import botmanager.generic.BotBase;
import botmanager.maidiscordbot.generic.MaiDiscordBotCommandBase;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

/**
 *
 * @author MC_2018 <mc2018.git@gmail.com>
 */
public class SupportCommand extends FrostbalanceCommandBase {
    
    public SupportCommand(BotBase bot) {
        super(bot);
    }
    
    @Override
    public void run(Event genericEvent) {
        GuildMessageReceivedEvent event;
        String[] words;
        String message;
        String id;
        String name;
        double balance, amount;
        
        if (!(genericEvent instanceof GuildMessageReceivedEvent)) {
            return;
        }
        
        event = (GuildMessageReceivedEvent) genericEvent;
        message = event.getMessage().getContentRaw();
        balance = bot.getUserInfluence(event.getMember());
        
        if (message.startsWith(bot.getPrefix() + "support ")) {
            message = message.replace(bot.getPrefix() + "support ", "");
        } else {
            return;
        }
        
        words = message.split(" ");
        
        if (words.length < 2) {
            Utilities.sendGuildMessage(event.getChannel(), "Proper format: " + "**" + bot.getPrefix() + "support USER AMOUNT**");
            return;
        }
        
        try {
            amount = Double.parseDouble(words[words.length - 1]);
            
            if (balance < amount) {
                Utilities.sendGuildMessage(event.getChannel(), "You can't offer that much support. You will instead offer all of your support.");
                amount = balance;
            } else if (amount <= 0) {
                Utilities.sendGuildMessage(event.getChannel(), "You have to give *some* support if you're running this command.");
                return;
            }
        } catch (NumberFormatException e) {
            Utilities.sendGuildMessage(event.getChannel(), "Proper format: " + "**" + bot.getPrefix() + "support USER AMOUNT**");
            return;
        }
        
        name = combineArrayStopAtIndex(words, words.length - 1);
        id = Utilities.findUserId(event.getGuild(), name);
        
        if (id == null) {
            Utilities.sendGuildMessage(event.getChannel(), "Couldn't find user'" + name + "'.");
            return;
        }
        
        bot.changeUserInfluence(event.getMember(), -amount);
        bot.changeUserInfluence(event.getGuild().getMemberById(id), amount);

        event.getMessage().delete();
        
        Utilities.sendGuildMessage(event.getChannel(),
                event.getMember().getEffectiveName() + " has supported "
                + event.getGuild().getMemberById(id).getEffectiveName()
                + ", giving them some influence.");

        Utilities.sendPrivateMessage(event.getGuild().getMemberById(id).getUser(),
                event.getMember().getEffectiveName() + " has supported you, giving you " + amount + " influence.");
    }

    @Override
    public String info() {
        return "**" + bot.getPrefix() + "support USER AMOUNT** - gives your influence to someone else (don't @ them)";
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
