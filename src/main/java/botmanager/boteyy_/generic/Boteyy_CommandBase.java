package botmanager.boteyy_.generic;

import botmanager.boteyy_.Boteyy_;
import botmanager.generic.BotBase;
import botmanager.generic.ICommand;

/**
 *
 * @author MC_2018 <mc2018.git@gmail.com>
 */

public abstract class Boteyy_CommandBase implements ICommand {

    protected Boteyy_ bot;
    
    public Boteyy_CommandBase(BotBase bot) {
        this.bot = (Boteyy_) bot;
    }
    
    public abstract String info();

}
