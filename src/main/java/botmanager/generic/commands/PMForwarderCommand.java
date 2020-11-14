package botmanager.generic.commands;

import botmanager.utils.IOUtils;
import botmanager.utils.JDAUtils;
import botmanager.generic.BotBase;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author MC_2018 <mc2018.git@gmail.com>
 */
public class PMForwarderCommand implements IMessageReceivedCommand {

    BotBase bot;
    List<String> ownerIDs;

    final String[] KEYWORDS = {
        "forward",
        "message",
        "f",
        "m",
    };

    public PMForwarderCommand(BotBase bot) {
        this.bot = bot;

        try {
            ownerIDs = IOUtils.readLines(new File("data/" + bot.getName() + "/owner_id.txt"));
        } catch (Exception e) {
            ownerIDs = new ArrayList<>();
        }
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
        
        if (ownerIDs.contains(event.getAuthor().getId())) {
            JDAUtils.sendPrivateMessage(bot.getJDA().getUserById(userID), input.replaceFirst(userID, ""));
        }
    }

}
