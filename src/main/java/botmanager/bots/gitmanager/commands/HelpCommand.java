package botmanager.bots.gitmanager.commands;

import botmanager.utils.JDAUtils;
import botmanager.generic.ICommand;
import botmanager.bots.gitmanager.GitManager;
import botmanager.bots.gitmanager.generic.GitManagerCommandBase;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.MessageEmbed.Field;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;

/**
 *
 * @author MC_2018 <mc2018.git@gmail.com>
 */
public class HelpCommand extends GitManagerCommandBase {

    public HelpCommand(GitManager bot) {
        super(bot);
    }

    @Override
    public void run(Event genericEvent) {
        EmbedBuilder eb = new EmbedBuilder();
        User user;
        String[] words;
        
        if (genericEvent instanceof GuildMessageReceivedEvent) {
            words = ((GuildMessageReceivedEvent) genericEvent).getMessage().getContentRaw().split(" ");
            user = ((GuildMessageReceivedEvent) genericEvent).getAuthor();
        } else if (genericEvent instanceof PrivateMessageReceivedEvent) {
            words = ((PrivateMessageReceivedEvent) genericEvent).getMessage().getContentRaw().split(" ");
            user = ((PrivateMessageReceivedEvent) genericEvent).getAuthor();
        } else {
            return;
        }
        
        eb.setTitle(bot.getName() + " Commands");
        
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
        
        JDAUtils.sendPrivateMessage(user, eb.build());
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
