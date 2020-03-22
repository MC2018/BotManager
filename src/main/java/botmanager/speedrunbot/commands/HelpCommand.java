package botmanager.speedrunbot.commands;

import botmanager.speedrunbot.generic.ISpeedrunBotCommand;
import botmanager.generic.BotBase;
import botmanager.Utilities;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import botmanager.generic.ICommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed.Field;

/**
 *
 * @author MC_2018 <mc2018.git@gmail.com>
 */
public class HelpCommand extends ISpeedrunBotCommand {

    public HelpCommand(BotBase bot) {
        super(bot);
    }
    
    @Override
    public void run(Event genericEvent) {
        GuildMessageReceivedEvent event;
        EmbedBuilder eb = new EmbedBuilder();
        String[] words;
        //String result = "__**" + bot.getName() + "**__\n\n";
        eb.setTitle(bot.getName() + " Commands");
        
        if (!(genericEvent instanceof GuildMessageReceivedEvent)) {
            return;
        }
        
        event = (GuildMessageReceivedEvent) genericEvent;
        words = event.getMessage().getContentRaw().split(" ");
        
        if (words.length > 0 && words[0].equals(bot.getPrefix() + "help")) {
            for (ISpeedrunBotCommand command : bot.getCommands()) {
                Field field = command.info();
                
                if (field != null) {
                    eb.addField(field);
                }
            }
            
            //result += "\nIf you have any questions/comments/concerns, please DM me and I will get back to you :ok_hand:.";
            eb.addField("", "If you have any questions/comments/concerns,\nplease DM me and I will get back to you :ok_hand:.", false);
        } else {
            return;
        }
        
        Utilities.sendGuildMessage(event.getChannel(), eb.build());
    }

    @Override
    public Field info() {
        return null;
    }
    
}
