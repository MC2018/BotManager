package botmanager.bulletbot.generic;

import botmanager.bulletbot.BulletBot;
import botmanager.generic.ICommand;

/**
 *
 * @author MC_2018 <mc2018.git@gmail.com>
 */

public abstract class BulletBotCommandBase implements ICommand {

    protected BulletBot bot;
    
    public BulletBotCommandBase(BulletBot bot) {
        this.bot = (BulletBot) bot;
    }
    
    public abstract String info();
    
    public BulletBot getBot() {
        return bot;
    }
    
}
