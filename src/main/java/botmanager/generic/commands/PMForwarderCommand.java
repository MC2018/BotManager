package botmanager.generic.commands;

import botmanager.JDAUtils;
import botmanager.generic.BotBase;
import botmanager.generic.ICommand;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;

/**
 *
 * @author MC_2018 <mc2018.git@gmail.com>
 */
public class PMForwarderCommand implements ICommand {

    BotBase bot;
    
    final String[] KEYWORDS = {
        "forward",
        "message",
        "f",
        "m",
    };

    public PMForwarderCommand(BotBase bot) {
        this.bot = bot;
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
        
        JDAUtils.sendPrivateMessage(bot.getJDA().getUserById(userID), input.replaceFirst(userID, ""));
    }

}
