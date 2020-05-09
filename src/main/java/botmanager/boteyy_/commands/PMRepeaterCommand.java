package botmanager.boteyy_.commands;

import botmanager.generic.BotBase;
import botmanager.Utilities;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import botmanager.boteyy_.generic.Boteyy_CommandBase;

/**
 *
 * @author MC_2018 <mc2018.git@gmail.com>
 */

public class PMRepeaterCommand extends Boteyy_CommandBase {

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
        
        if (event.getAuthor().isBot()) {
            return;
        }
        
        message = "Sent by " + event.getAuthor().getName() + "#" + event.getAuthor().getDiscriminator() + "\n" + event.getMessage().getContentRaw();
        Utilities.sendPrivateMessage(bot.getJDA().getUserById("106949500500738048"), message);
    }

    @Override
    public String info() {
        return null;
    }

}
