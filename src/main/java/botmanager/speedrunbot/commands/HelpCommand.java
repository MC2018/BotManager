package botmanager.speedrunbot.commands;

import botmanager.JDAUtils;
import botmanager.speedrunbot.generic.SpeedrunBotCommandBase;
import botmanager.generic.BotBase;
import botmanager.generic.ICommand;
import botmanager.speedrunbot.SpeedrunBot;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed.Field;

/**
 *
 * @author MC_2018 <mc2018.git@gmail.com>
 */
public class HelpCommand extends SpeedrunBotCommandBase {

    public HelpCommand(BotBase bot) {
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
                if (icommand instanceof SpeedrunBotCommandBase) {
                    SpeedrunBotCommandBase command = (SpeedrunBotCommandBase) icommand;
                    Field field = command.info();
                    
                    if (field != null) {
                        eb.addField(field);
                    }
                }
            }
            
            eb.addField("", "If you have any questions/comments/concerns,\nplease DM me and I will get back to you :ok_hand:.", false);
        } else {
            return;
        }
        
        eb.setColor(SpeedrunBot.getEmbedColor());
        JDAUtils.sendGuildMessage(event.getChannel(), eb.build());
    }

    @Override
    public Field info() {
        return null;
    }
    
}
