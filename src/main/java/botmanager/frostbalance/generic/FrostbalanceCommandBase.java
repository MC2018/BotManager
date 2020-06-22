package botmanager.frostbalance.generic;

import botmanager.frostbalance.Frostbalance;
import botmanager.generic.BotBase;
import botmanager.generic.ICommand;

public abstract class FrostbalanceCommandBase implements ICommand {

    protected Frostbalance bot;

    public FrostbalanceCommandBase(BotBase bot) {
        this.bot = (Frostbalance) bot;
    }

    public abstract String info();

}
