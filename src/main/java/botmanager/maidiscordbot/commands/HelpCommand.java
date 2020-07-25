package botmanager.maidiscordbot.commands;

import botmanager.JDAUtils;
import botmanager.generic.BotBase;
import botmanager.generic.ICommand;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import botmanager.maidiscordbot.generic.MaiDiscordBotCommandBase;

/**
 *
 * @author MC_2018 <mc2018.git@gmail.com>
 */
public class HelpCommand extends MaiDiscordBotCommandBase {

    public HelpCommand(BotBase bot) {
        super(bot);
    }
    
    @Override
    public void run(Event genericEvent) {
        GuildMessageReceivedEvent event;
        MoneyCommand moneyCommand = null;
        String[] words;
        String result = "__**" + bot.getName() + "**__\n\n";
        
        if (!(genericEvent instanceof GuildMessageReceivedEvent)) {
            return;
        }
        
        event = (GuildMessageReceivedEvent) genericEvent;
        words = event.getMessage().getContentRaw().split(" ");
        
        if (words.length > 0 && words[0].equals(bot.getPrefix() + "help")) {
            for (ICommand icommand : bot.getCommands()) {
                if (icommand instanceof MaiDiscordBotCommandBase) {
                    MaiDiscordBotCommandBase command = (MaiDiscordBotCommandBase) icommand;
                    String info = command.info();
                    
                    if (info != null && command instanceof MoneyCommand) {
                        moneyCommand = (MoneyCommand) command;
                    } else if (info != null) {
                        result += info + "\n";
                    }
                }
            }
            
            if (moneyCommand != null) {
                result += "\n" + moneyCommand.info();
            }
        } else {
            return;
        }
        
        JDAUtils.sendGuildMessage(event.getChannel(), result);
    }

    @Override
    public String info() {
        return "**" + bot.getPrefix() + "help** - gives you this shpeal";
    }
    
}
