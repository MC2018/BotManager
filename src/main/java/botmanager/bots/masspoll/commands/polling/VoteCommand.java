package botmanager.bots.masspoll.commands.polling;

import botmanager.bots.masspoll.MassPoll;
import botmanager.bots.masspoll.generic.MassPollCommandBase;
import botmanager.bots.masspoll.objects.ButtonSelectionType;
import botmanager.bots.masspoll.objects.Poll;
import botmanager.generic.commands.IButtonClickCommand;
import botmanager.generic.commands.IMessageReactionAddCommand;
import botmanager.generic.commands.IMessageReactionRemoveCommand;
import botmanager.utils.IOUtils;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent;
import net.dv8tion.jda.api.interactions.components.Button;
import net.dv8tion.jda.api.interactions.components.Component;

import java.util.List;

public class VoteCommand extends MassPollCommandBase implements IButtonClickCommand {

    public VoteCommand(MassPoll bot) {
        super(bot);
    }

    @Override
    public void runOnButtonClick(ButtonClickEvent event) {
        Message message;
        String uuid, typeString;
        String[] buttonIdentifiers;
        long pollID = 0;
        int index = -1;
        boolean turnToUpvote;

        if (event.isFromGuild()) {
            return;
        }

        message = event.getMessage();
        buttonIdentifiers = event.getButton().getId().split("_");

        if (buttonIdentifiers.length != 4) {
            return;
        }

        pollID = Long.parseLong(buttonIdentifiers[0]);
        uuid = buttonIdentifiers[1];
        typeString = buttonIdentifiers[2];
        index = Integer.parseInt(buttonIdentifiers[3]);
        turnToUpvote = event.getButton().getStyle().name().equals("SECONDARY");

        if (!ButtonSelectionType.PollSelection.name().equals(typeString)) {
            return;
        }

        // wait until saving has completed
        // bad, ik, but I want to make sure no data is lost w/the tools I'm using
        while (bot.pollsInProcess.contains(pollID)) {
        }

        bot.pollsInProcess.add(pollID);

        try {
            Poll poll = IOUtils.readGson(Poll.getFileLocation(bot, pollID), Poll.class);

            if (!poll.getUUID().equals(uuid)) {
                bot.pollsInProcess.remove(pollID);
                return;
            }

            poll.updateUserVote(event.getUser().getId(), index, turnToUpvote);
            IOUtils.writeGson(Poll.getFileLocation(bot, pollID), poll, true);
            message.editMessageEmbeds(poll.generateMessageEmbed(bot)).setActionRows(poll.generateActionRows(event.getUser().getId(), poll.getOptionsSize(), ButtonSelectionType.PollSelection)).queue();
        } catch (Exception e) {
            e.printStackTrace();
        }

        bot.pollsInProcess.remove(pollID);
    }

}
