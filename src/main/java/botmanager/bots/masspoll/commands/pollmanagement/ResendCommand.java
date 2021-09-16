package botmanager.bots.masspoll.commands.pollmanagement;

import botmanager.bots.masspoll.MassPoll;
import botmanager.bots.masspoll.generic.MassPollCommandBase;
import botmanager.bots.masspoll.objects.ButtonSelectionType;
import botmanager.bots.masspoll.objects.Poll;
import botmanager.generic.commands.IPrivateMessageReceivedCommand;
import botmanager.utils.IOUtils;
import botmanager.utils.JDAUtils;
import botmanager.utils.Utils;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;

import java.util.ArrayList;
import java.util.Date;

public class ResendCommand extends MassPollCommandBase implements IPrivateMessageReceivedCommand {

    public final String[] KEYWORDS = {
            "resend"
    };

    public ResendCommand(MassPoll bot) {
        super(bot);
    }

    @Override
    public void runOnPrivateMessage(PrivateMessageReceivedEvent event) {
        String message = Utils.startsWithReplace(event.getMessage().getContentRaw(), KEYWORDS);
        String nonMessageableList = "", failureList = "";
        ArrayList<Poll.PollUserData> pollUserData;
        MessageEmbed embed;
        Poll poll;
        long pollID;

        if (message == null) {
            return;
        }

        try {
            pollID = Long.parseLong(message);
        } catch (NumberFormatException e) {
            return;
        }

        try {
            poll = IOUtils.readGson(Poll.getFileLocation(bot, pollID), Poll.class);

            if (Date.from(poll.getTimeLastPolled().toInstant().plusSeconds(60 * 60 * 3)).after(new Date())) {
                JDAUtils.sendPrivateMessage(event.getAuthor(), "Please wait 3 hours before resending a poll.");
                return;
            }

            pollUserData = poll.getUserDataCopy();
            embed = poll.generatePollEmbed(event.getJDA().getGuildById(poll.getGuildID()));
            JDAUtils.sendPrivateMessage(event.getAuthor(), "Re-notifying and/or resending polls out to people.");

            for (Poll.PollUserData userData : pollUserData) {
                if (userData.votes != 0 || !userData.comments.isEmpty()) {
                    continue;
                }

                try {
                    if (userData.messageable) {
                        User user = bot.getJDA().retrieveUserById(userData.userID).complete();
                        MessageChannel channel = user.openPrivateChannel().complete();
                        channel.retrieveMessageById(userData.messageID).queue(pollMessage -> {
                            pollMessage.reply("Please respond to this poll when you have the time.").queue();
                        });
                    } else {
                        Member memberToMessage = bot.getJDA().getGuildById(poll.getGuildID()).retrieveMemberById(userData.userID).complete();
                        Message returnMessage = null;
                        String messageID = "0";
                        boolean messageable = true;

                        try {
                            MessageChannel memberChannel = memberToMessage.getUser().openPrivateChannel().complete();
                            returnMessage = memberChannel.sendMessageEmbeds(embed).setActionRows(poll.generateActionRows(0, poll.getOptionsSize(), ButtonSelectionType.PollSelection)).complete();
                        } catch (ErrorResponseException e) {
                            if (e.getErrorCode() == 50007) {
                                nonMessageableList += memberToMessage.getEffectiveName() + " (ID " + memberToMessage.getId() + ")\n";
                                System.out.println("Someone getting DM'd (ID " + memberToMessage.getId() + ") has DMs closed.");
                                messageable = false;
                            } else {
                                failureList += memberToMessage.getEffectiveName() + " (ID " + memberToMessage.getId() + ")\n";
                                System.out.println("Someone getting DM'd (ID " + memberToMessage.getId() + ") failed out.");
                                e.printStackTrace();
                            }
                        } catch (Exception e) {
                            System.out.println("Unexpected error");
                            e.printStackTrace();
                        }

                        if (returnMessage != null) {
                            messageID = returnMessage.getId();
                        }

                        poll.setUserMessageable(memberToMessage.getId(), messageID, messageable);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            poll.setTimeLastPolled(new Date());
            IOUtils.writeGson(Poll.getFileLocation(bot, pollID), poll, true);
        } catch (Exception e) {
            e.printStackTrace();
        }

        bot.pollsInProcess.remove(pollID);

        if (!nonMessageableList.isEmpty() || !failureList.isEmpty()) {
            String messageToSend = "";

            if (!nonMessageableList.isEmpty()) {
                messageToSend += "Because these members' DMs are closed on the server, they will not be able to vote on this poll.\n"
                        + nonMessageableList + "\n"
                        + "Have them allow direct messages from members of this server.\n"
                        + "The next time you run **resend**, it will attempt to send the poll to them again (as well as notifying others).\n\n\n";
            }

            if (!failureList.isEmpty()) {
                messageToSend += "These members didn't receive the poll, and we're not too sure why.\n"
                        + failureList + "\n"
                        + "Please let MC_2018#9481 know about this so he can try to figure it out.";
            }

            JDAUtils.sendPrivateMessage(event.getAuthor(), messageToSend);
        }
    }

}
