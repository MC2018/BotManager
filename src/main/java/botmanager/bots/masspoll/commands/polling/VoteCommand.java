package botmanager.bots.masspoll.commands.polling;

import botmanager.bots.masspoll.MassPoll;
import botmanager.bots.masspoll.generic.MassPollCommandBase;
import botmanager.bots.masspoll.objects.ButtonSelectionType;
import botmanager.bots.masspoll.objects.Poll;
import botmanager.bots.masspoll.objects.PollAccessor;
import botmanager.generic.commands.IButtonClickCommand;
import botmanager.utils.IOUtils;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;

public class VoteCommand extends MassPollCommandBase implements IButtonClickCommand {

    public VoteCommand(MassPoll bot) {
        super(bot);
    }

    @Override
    public void runOnButtonClick(ButtonClickEvent event) {
        Message message;
        String uuid, typeString;
        String[] buttonIdentifiers;
        long pollID;
        int index;
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

        try (PollAccessor pollAccessor = new PollAccessor(bot, pollID, PollAccessor.PollAccessType.UUID, uuid)) {
            Poll poll = pollAccessor.getPoll();

            poll.updateUserVote(event.getUser().getId(), index, turnToUpvote);
            IOUtils.writeGson(Poll.getFileLocation(bot, pollID), poll, true);
            message.editMessageEmbeds(
                    poll.generatePollEmbed(event.getJDA().getGuildById(poll.getGuildID()))).setActionRows(poll.generateActionRows(event.getUser().getId(),
                    poll.getOptionsSize(),
                    ButtonSelectionType.PollSelection)
            ).queue();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
