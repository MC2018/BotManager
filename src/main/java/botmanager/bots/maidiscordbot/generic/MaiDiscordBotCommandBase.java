package botmanager.bots.maidiscordbot.generic;

import botmanager.generic.BotBase;
import botmanager.generic.ICommand;
import botmanager.bots.maidiscordbot.MaiDiscordBot;

/**
 *
 * @author MC_2018 <mc2018.git@gmail.com>
 */

public abstract class MaiDiscordBotCommandBase implements ICommand {

    protected MaiDiscordBot bot;
    
    public MaiDiscordBotCommandBase(BotBase bot) {
        this.bot = (MaiDiscordBot) bot;
    }

    public abstract String info();
    
}
