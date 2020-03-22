package botmanager.boteyy_.generic;

import botmanager.boteyy_.Boteyy_;
import botmanager.generic.BotBase;
import botmanager.generic.ICommand;

/**
 *
 * @author MC_2018 <mc2018.git@gmail.com>
 */

public abstract class IBoteyy_Command implements ICommand {

    protected Boteyy_ bot;
    
    public IBoteyy_Command(BotBase bot) {
        this.bot = (Boteyy_) bot;
    }
    
    public abstract String info();

}
