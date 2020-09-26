package botmanager.bots.gitmanager.commands;

import botmanager.generic.commands.IMessageReceivedCommand;
import botmanager.utils.JDAUtils;
import botmanager.generic.ICommand;
import botmanager.bots.gitmanager.GitManager;
import botmanager.bots.gitmanager.generic.GitManagerCommandBase;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

/**
 *
 * @author MC_2018 <mc2018.git@gmail.com>
 */
public class HelpCommand extends GitManagerCommandBase implements IMessageReceivedCommand {

    public HelpCommand(GitManager bot) {
        super(bot);
    }

    @Override
    public void runOnMessage(MessageReceivedEvent event) {
        EmbedBuilder eb = new EmbedBuilder();
        User user = event.getAuthor();
        String[] words = event.getMessage().getContentRaw().split(" ");

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
    public MessageEmbed.Field info() {
        return null;
    }

    @Override
    public MessageEmbed getFailureEmbed() {
        return null;
    }
    
}
