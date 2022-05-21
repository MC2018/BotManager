package botmanager.bots.masspoll.commands.creation;

import botmanager.bots.masspoll.MassPoll;
import botmanager.bots.masspoll.generic.MassPollCommandBase;
import botmanager.bots.masspoll.objects.ButtonSelectionType;
import botmanager.bots.masspoll.objects.Poll;
import botmanager.generic.commands.IButtonClickCommand;
import botmanager.utils.IOUtils;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;

public class SelectRolesCommand extends MassPollCommandBase implements IButtonClickCommand {

    public SelectRolesCommand(MassPoll bot) {
        super(bot);
    }

    @Override
    public void runOnButtonClick(ButtonClickEvent event) {
        String[] buttonIdentifiers;
        String uuid, typeString;
        Message message;
        long pollID;
        int index;
        boolean turnToAdd;

        if (event.isFromGuild()) {
            return;
        }

        buttonIdentifiers = event.getButton().getId().split("_");

        if (buttonIdentifiers.length != 4) {
            return;
        }

        pollID = Long.parseLong(buttonIdentifiers[0]);
        uuid = buttonIdentifiers[1];
        typeString = buttonIdentifiers[2];
        index = Integer.parseInt(buttonIdentifiers[3]);
        turnToAdd = event.getButton().getStyle().name().equals("SECONDARY");
        message = event.getMessage();

        if (!ButtonSelectionType.RoleSelection.name().equals(typeString)) {
            return;
        }

        try {
            Poll poll = bot.POLLS_BEING_CREATED.get(event.getUser().getId());

            if (!poll.getUUID().equals(uuid)) {
                return;
            }

            poll.updateRolesToMention(index, turnToAdd);
            IOUtils.writeGson(Poll.getFileLocation(bot, pollID), poll, true);
            message.editMessageComponents(poll.generateActionRows(ButtonSelectionType.RoleSelection)).queue();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
