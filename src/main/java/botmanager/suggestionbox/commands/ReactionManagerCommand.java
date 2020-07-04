package botmanager.suggestionbox.commands;

import botmanager.suggestionbox.SuggestionBox;
import botmanager.suggestionbox.generic.SuggestionBoxCommandBase;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;

/**
 *
 * @author MC_2018 <mc2018.git@gmail.com>
 */

public class ReactionManagerCommand extends SuggestionBoxCommandBase {

    public ReactionManagerCommand(SuggestionBox bot) {
        super(bot);
    }

    @Override
    public void run(Event genericEvent) {
        GuildMessageReactionAddEvent event;
        Message message;
        String emoteName;
        
        if (!(genericEvent instanceof GuildMessageReactionAddEvent)) {
            return;
        }
        
        event = (GuildMessageReactionAddEvent) genericEvent;
        emoteName = event.getReactionEmote().getName();
        
        if (!event.getChannel().getName().equalsIgnoreCase("user-suggestions") && !event.getChannel().getName().equalsIgnoreCase("emote-suggestions")) {
            return;
        }
        
        message = event.getChannel().retrieveMessageById(event.getMessageId()).complete();
        String[] messageSplit = message.getContentRaw().split(" ");
        String potentialId = "";
        
        if (messageSplit.length >= 3) {
            potentialId = messageSplit[2];
        }
        
        if (potentialId.contains(event.getMember().getId())) {
            event.getReaction().removeReaction(event.getUser()).queue();
            return;
        }
        
        if (emoteName.equalsIgnoreCase("upvote") || emoteName.equalsIgnoreCase("downvote")) {
            return;
        }
        
        event.getReaction().removeReaction(event.getUser()).queue();
    }

    @Override
    public String info() {
        return null;
    }

}
