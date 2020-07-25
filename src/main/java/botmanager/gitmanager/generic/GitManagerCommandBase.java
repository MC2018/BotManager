package botmanager.gitmanager.generic;

import botmanager.generic.ICommand;
import botmanager.gitmanager.GitManager;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.MessageEmbed.Field;

/**
 *
 * @author MC_2018 <mc2018.git@gmail.com>
 */

public abstract class GitManagerCommandBase implements ICommand {

    protected GitManager bot;
    
    public GitManagerCommandBase(GitManager bot) {
        this.bot = (GitManager) bot;
    }
    
    public abstract Field info();
    
    public abstract MessageEmbed getFailureEmbed();
    
    public GitManager getBot() {
        return bot;
    }
    
}
