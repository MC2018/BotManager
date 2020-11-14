package botmanager.generic.commands;

import botmanager.utils.IOUtils;
import botmanager.utils.JDAUtils;
import botmanager.generic.BotBase;

import java.io.File;
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
    List<String> ownerIDs;
    
    public PMRepeaterCommand(BotBase bot) {
        this.bot = bot;

        try {
            ownerIDs = IOUtils.readLines(new File("data/owner_id.txt"));
        } catch (Exception e) {
            ownerIDs = new ArrayList<>();
        }
    }
    
    @Override
    public void runOnMessage(MessageReceivedEvent event) {
        String message;
        List<Message.Attachment> attachments;
        
        if (event.isFromGuild() || event.getAuthor().isBot() || ownerIDs.contains(event.getAuthor().getId())) {
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
