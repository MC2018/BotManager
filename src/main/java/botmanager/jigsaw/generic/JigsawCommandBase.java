package botmanager.jigsaw.generic;

import botmanager.jigsaw.Jigsaw;
import botmanager.generic.ICommand;

/**
 *
 * @author MC_2018 <mc2018.git@gmail.com>
 */

public abstract class JigsawCommandBase implements ICommand {

    protected Jigsaw bot;
    
    public JigsawCommandBase(Jigsaw bot) {
        this.bot = (Jigsaw) bot;
    }
    
    public abstract String info();
    
    public Jigsaw getBot() {
        return bot;
    }
    
}
