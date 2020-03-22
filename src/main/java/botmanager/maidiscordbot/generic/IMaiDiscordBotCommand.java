package botmanager.maidiscordbot.generic;

import botmanager.generic.BotBase;
import botmanager.generic.ICommand;
import botmanager.maidiscordbot.MaiDiscordBot;

/**
 *
 * @author MC_2018 <mc2018.git@gmail.com>
 */

public abstract class IMaiDiscordBotCommand implements ICommand {

    protected MaiDiscordBot bot;
    
    public IMaiDiscordBotCommand(BotBase bot) {
        this.bot = (MaiDiscordBot) bot;
    }

    public abstract String info();
    
}
