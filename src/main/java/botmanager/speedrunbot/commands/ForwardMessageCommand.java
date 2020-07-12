package botmanager.speedrunbot.commands;

import botmanager.generic.BotBase;
import botmanager.Utilities;
import net.dv8tion.jda.api.events.Event;
import botmanager.speedrunbot.generic.SpeedrunBotCommandBase;
import net.dv8tion.jda.api.entities.MessageEmbed.Field;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;

/**
 *
 * @author MC_2018 <mc2018.git@gmail.com>
 */
public class ForwardMessageCommand extends SpeedrunBotCommandBase {

    final String[] KEYWORDS = {
        bot.getPrefix() + "forward",
        bot.getPrefix() + "message",
        bot.getPrefix() + "f",
        bot.getPrefix() + "m",
    };

    public ForwardMessageCommand(BotBase bot) {
        super(bot);
    }

    @Override
    public void run(Event genericEvent) {
        PrivateMessageReceivedEvent event;
        String input;
        String userID;

        boolean found = false;

        if (!(genericEvent instanceof PrivateMessageReceivedEvent)) {
            return;
        }

        event = (PrivateMessageReceivedEvent) genericEvent;
        input = event.getMessage().getContentRaw();

        for (String keyword : KEYWORDS) {
            if (input.toLowerCase().startsWith(keyword + " ")) {
                input = input.toLowerCase().replace(keyword + " ", "");
                found = true;
                break;
            }
        }

        if (!found) {
            return;
        }

        userID = input.split(" ")[0];
        
        
        if (!event.getAuthor().getId().equals("106949500500738048")) {
            return;
        }
        
        Utilities.sendPrivateMessage(bot.getJDA().getUserById(userID), input.replaceFirst(userID, ""));
    }

    @Override
    public Field info() {
        return null;
    }

}
