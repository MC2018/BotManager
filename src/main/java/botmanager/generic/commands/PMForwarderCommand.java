package botmanager.generic.commands;

import botmanager.utils.JDAUtils;
import botmanager.generic.BotBase;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

/**
 *
 * @author MC_2018 <mc2018.git@gmail.com>
 */
public class PMForwarderCommand implements IMessageReceivedCommand {

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
    public void runOnMessage(MessageReceivedEvent event) {
        String input;
        String userID;
        boolean found = false;

        if (event.isFromGuild()) {
            return;
        }

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
