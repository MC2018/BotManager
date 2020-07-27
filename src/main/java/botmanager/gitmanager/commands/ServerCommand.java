package botmanager.gitmanager.commands;

import botmanager.gitmanager.GitManager;
import botmanager.gitmanager.generic.GitManagerCommandBase;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.Event;

/**
 *
 * @author MC_2018 <mc2018.git@gmail.com>
 */

public class ServerCommand extends GitManagerCommandBase {

    public ServerCommand(GitManager bot) {
        super(bot);
    }

    @Override
    public void run(Event event) {
        
    }
    
    @Override
    public MessageEmbed.Field info() {
        return null;
    }

    @Override
    public MessageEmbed getFailureEmbed() {
        return null;
    }

}
