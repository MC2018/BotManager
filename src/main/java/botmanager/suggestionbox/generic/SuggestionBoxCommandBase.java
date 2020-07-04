package botmanager.suggestionbox.generic;

import botmanager.suggestionbox.SuggestionBox;
import botmanager.generic.BotBase;
import botmanager.generic.ICommand;

/**
 *
 * @author MC_2018 <mc2018.git@gmail.com>
 */

public abstract class SuggestionBoxCommandBase implements ICommand {
    
    protected SuggestionBox bot;
    
    public SuggestionBoxCommandBase(BotBase bot) {
        this.bot = (SuggestionBox) bot;
    }

    public abstract String info();
    
}
