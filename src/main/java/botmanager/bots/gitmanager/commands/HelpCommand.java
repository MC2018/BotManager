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

        if (words.length > 0 && words[0].equals(bot.prefix + "help")) {
            boolean taskHelp = words.length > 1 && words[1].contains("task");
            boolean meetingHelp = !taskHelp && words.length > 1 && words[1].contains("meeting");

            if (taskHelp) {
                eb.setTitle(bot.getName() + " Task Commands");
            } else if (meetingHelp) {
                eb.setTitle(bot.getName() + " Meeting Commands");
            } else {
                eb.setTitle(bot.getName() + " Commands");
                eb.addField("Task Commands", "```" + bot.prefix + "help tasks```", false);
                eb.addField("Meeting Commands", "```" + bot.prefix + "help meetings```", false);
            }

            for (ICommand icommand : bot.getCommands()) {
                if (icommand instanceof GitManagerCommandBase) {
                    GitManagerCommandBase command = (GitManagerCommandBase) icommand;
                    MessageEmbed.Field field = command.info();
                    String className = icommand.getClass().getName();

                    if (field != null && ((!taskHelp && !meetingHelp && !className.contains("meetings") && !className.contains("tasks"))
                            || (meetingHelp && className.contains("meetings"))
                            || (taskHelp && className.contains("tasks")))) {
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
