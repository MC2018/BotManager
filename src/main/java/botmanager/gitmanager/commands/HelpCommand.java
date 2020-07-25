package botmanager.gitmanager.commands;

import botmanager.Utilities;
import botmanager.generic.ICommand;
import botmanager.gitmanager.GitManager;
import botmanager.gitmanager.generic.GitManagerCommandBase;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.MessageEmbed.Field;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

/**
 *
 * @author maxclausius
 */
public class HelpCommand extends GitManagerCommandBase {

    public HelpCommand(GitManager bot) {
        super(bot);
    }

    @Override
    public void run(Event genericEvent) {
        GuildMessageReceivedEvent event;
        EmbedBuilder eb = new EmbedBuilder();
        String[] words;
        
        eb.setTitle(bot.getName() + " Commands");
        
        if (!(genericEvent instanceof GuildMessageReceivedEvent)) {
            return;
        }
        
        event = (GuildMessageReceivedEvent) genericEvent;
        words = event.getMessage().getContentRaw().split(" ");
        
        if (words.length > 0 && words[0].equals(bot.getPrefix() + "help")) {
            for (ICommand icommand : bot.getCommands()) {
                if (icommand instanceof GitManagerCommandBase) {
                    GitManagerCommandBase command = (GitManagerCommandBase) icommand;
                    MessageEmbed.Field field = command.info();
                    
                    if (field != null) {
                        eb.addField(field);
                    }
                }
            }
        } else {
            return;
        }
        
        Utilities.sendPrivateMessage(event.getAuthor(), eb.build());
    }

    @Override
    public Field info() {
        return null;
    }

    @Override
    public MessageEmbed getFailureEmbed() {
        return null;
    }
    
}
