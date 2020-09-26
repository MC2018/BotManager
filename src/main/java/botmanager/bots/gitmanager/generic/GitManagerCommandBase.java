package botmanager.bots.gitmanager.generic;

import botmanager.generic.ICommand;
import botmanager.bots.gitmanager.GitManager;
import botmanager.generic.commands.IMessageReceivedCommand;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.MessageEmbed.Field;

/**
 *
 * @author MC_2018 <mc2018.git@gmail.com>
 */

public abstract class GitManagerCommandBase {

    protected GitManager bot;
    
    public GitManagerCommandBase(GitManager bot) {
        this.bot = bot;
    }
    
    public abstract Field info();
    
    public abstract MessageEmbed getFailureEmbed();
    
    public GitManager getBot() {
        return bot;
    }
    
}
