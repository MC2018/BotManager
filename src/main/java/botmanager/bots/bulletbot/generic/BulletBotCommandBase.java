package botmanager.bots.bulletbot.generic;

import botmanager.bots.bulletbot.BulletBot;
import botmanager.generic.ICommand;

/**
 *
 * @author MC_2018 <mc2018.git@gmail.com>
 */

public abstract class BulletBotCommandBase {

    protected BulletBot bot;
    
    public BulletBotCommandBase(BulletBot bot) {
        this.bot = bot;
    }

    public abstract String info();
    
    public BulletBot getBot() {
        return bot;
    }
    
}
