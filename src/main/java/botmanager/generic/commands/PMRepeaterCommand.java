package botmanager.generic.commands;

import botmanager.utils.JDAUtils;
import botmanager.generic.BotBase;
import java.util.ArrayList;
import java.util.List;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

/**
 *
 * @author MC_2018 <mc2018.git@gmail.com>
 */

public class PMRepeaterCommand implements IMessageReceivedCommand {

    BotBase bot;
    ArrayList<String> messagedUserIDs = new ArrayList();
    
    public PMRepeaterCommand(BotBase bot) {
        this.bot = bot;
    }
    
    @Override
    public void runOnMessage(MessageReceivedEvent event) {
        String message;
        List<Message.Attachment> attachments;
        
        if (event.isFromGuild()) {
            return;
        }
        
        if (event.getAuthor().isBot() || event.getAuthor().getId().equals("106949500500738048")) {
            return;
        }
        
        message = "Sent by " + event.getAuthor().getName() + "#" + event.getAuthor().getDiscriminator() + "\n" + event.getMessage().getContentDisplay();
        attachments = event.getMessage().getAttachments();
        
        if (!attachments.isEmpty()) {
            message += "\n\nAttachments:\n";
            
            for (Message.Attachment attachment : attachments) {
                message += attachment.getUrl() + "\n";
            }
        }
        
        JDAUtils.sendPrivateMessage(bot.getJDA().getUserById("106949500500738048"), message);
    }

}
