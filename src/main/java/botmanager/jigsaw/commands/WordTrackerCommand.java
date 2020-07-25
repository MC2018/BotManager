package botmanager.jigsaw.commands;

import botmanager.JDAUtils;
import botmanager.jigsaw.Jigsaw;
import botmanager.jigsaw.generic.JigsawCommandBase;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

/**
 *
 * @author MC_2018 <mc2018.git@gmail.com>
 */

public class WordTrackerCommand extends JigsawCommandBase {

    String[][] characterReplacements = {
        {"1", "i"},
        {"l", "i"},
        {"3", "e"},
        {"#", "h"},
        {"4", "a"},
        {"0", "o"}
    };
    
    public WordTrackerCommand(Jigsaw bot) {
        super(bot);
    }

    @Override
    public void run(Event genericEvent) {
        GuildMessageReceivedEvent event;
        String message;
        boolean found = false;
        
        if (!(genericEvent instanceof GuildMessageReceivedEvent)) {
            return;
        }

        event = (GuildMessageReceivedEvent) genericEvent;
        message = event.getMessage().getContentRaw().toLowerCase();
        
        for (String[] characterReplacement : characterReplacements) {
            message = message.replaceAll(characterReplacement[0], characterReplacement[1]);
        }
        
        for (String dirtyWord : getBot().getDirtyWords()) {
            if (message.contains(dirtyWord)) {
                found = true;
                break;
            }
        }
        
        if (!found || JDAUtils.hasRole(event.getMember(), "Mod")) {
            return;
        }
        
        bot.incrementUserDirtyWords(event.getGuild(), event.getAuthor());
    }

    
    @Override
    public String info() {
        return null;
    }

}
