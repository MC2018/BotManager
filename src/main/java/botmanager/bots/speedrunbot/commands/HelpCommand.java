package botmanager.bots.speedrunbot.commands;

import botmanager.generic.commands.IMessageReceivedCommand;
import botmanager.utils.JDAUtils;
import botmanager.bots.speedrunbot.generic.SpeedrunBotCommandBase;
import botmanager.generic.BotBase;
import botmanager.generic.ICommand;
import botmanager.bots.speedrunbot.SpeedrunBot;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed.Field;

/**
 *
 * @author MC_2018 <mc2018.git@gmail.com>
 */
public class HelpCommand extends SpeedrunBotCommandBase implements IMessageReceivedCommand {

    public HelpCommand(BotBase bot) {
        super(bot);
    }

    public void runOnMessage(MessageReceivedEvent event) {
        EmbedBuilder eb = new EmbedBuilder();
        String[] words = event.getMessage().getContentRaw().split(" ");
        
        eb.setTitle(bot.getName() + " Commands");
        
        if (words.length > 0 && words[0].equals(bot.getPrefix() + "help")) {
            for (ICommand icommand : bot.getCommands()) {
                if (icommand instanceof SpeedrunBotCommandBase) {
                    SpeedrunBotCommandBase command = (SpeedrunBotCommandBase) icommand;
                    Field field = command.info();
                    
                    if (field != null) {
                        eb.addField(field);
                    }
                }
            }

            eb.addField("", "If you have any questions/comments/concerns,\nplease DM me and I will get back to you :ok_hand:.", false);
            eb.addField("", "Unfortunately, sub-categories are not supported\nin SRC's API, so all times of a category will be\ngrouped together. " +
                    "Sorry for the inconvenience.", false);
        } else {
            return;
        }
        
        eb.setColor(SpeedrunBot.getEmbedColor());
        JDAUtils.sendMessage(event.getChannel(), null, eb.build(), null, null, false);
    }

    @Override
    public Field info() {
        return null;
    }
    
}
