package botmanager.speedrunbot.commands;

import botmanager.generic.BotBase;
import botmanager.Utilities;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import botmanager.speedrunbot.generic.SpeedrunBotCommandBase;
import java.util.ArrayList;
import net.dv8tion.jda.api.entities.MessageEmbed.Field;

/**
 *
 * @author MC_2018 <mc2018.git@gmail.com>
 */

public class PMRepeaterCommand extends SpeedrunBotCommandBase {

    ArrayList<String> messagedUserIDs = new ArrayList();
    
    public PMRepeaterCommand(BotBase bot) {
        super(bot);
    }

    @Override
    public void run(Event genericEvent) {
        PrivateMessageReceivedEvent event;
        String message;
        
        if (!(genericEvent instanceof PrivateMessageReceivedEvent)) {
            return;
        }
        
        event = (PrivateMessageReceivedEvent) genericEvent;
        
        if (event.getAuthor().isBot() || event.getAuthor().getId().equals("106949500500738048")) {
            return;
        }
        
        message = "Sent by " + event.getAuthor().getName() + "#" + event.getAuthor().getDiscriminator() + "\n" + event.getMessage().getContentRaw();
        Utilities.sendPrivateMessage(bot.getJDA().getUserById("106949500500738048"), message);
        
        if (event.getMessage().getContentRaw().startsWith(bot.getPrefix()) && !messagedUserIDs.contains(event.getAuthor().getId())) {
            Utilities.sendPrivateMessage(event.getAuthor(), "I prefer my commands be used on servers rather than DMs. "
                    + "If you have any questions or concerns about this bot though, feel free to voice your opinion through this chat, "
                    + "and they will reach to the developer.");
            messagedUserIDs.add(event.getAuthor().getId());
        }
    }

    @Override
    public Field info() {
        return null;
    }

}
