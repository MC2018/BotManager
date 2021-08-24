package botmanager.bots.masspoll.commands.creation;

import botmanager.bots.masspoll.MassPoll;
import botmanager.bots.masspoll.generic.MassPollCommandBase;
import botmanager.bots.masspoll.objects.ButtonSelectionType;
import botmanager.bots.masspoll.objects.GuildSettings;
import botmanager.bots.masspoll.objects.Poll;
import botmanager.generic.commands.IPrivateMessageReceivedCommand;
import botmanager.utils.IOUtils;
import botmanager.utils.JDAUtils;
import botmanager.utils.Utils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;

import java.io.IOException;
import java.util.*;

public class SendCommand extends MassPollCommandBase implements IPrivateMessageReceivedCommand {

    public SendCommand(MassPoll bot) {
        super(bot);
    }

    @Override
    public void runOnPrivateMessage(PrivateMessageReceivedEvent event) {
        String message = event.getMessage().getContentRaw();
        MessageChannel channel;
        Poll poll = bot.pollsBeingCreated.get(event.getAuthor().getId());
        boolean isUserPolled = false;
        String nonMessageableList = "", failureList = "";
        Member member;
        User user;

        if (!message.equalsIgnoreCase("send") || poll == null) {
            return;
        } else if (Utils.isNullOrEmpty(poll.getQuestion())) {
            JDAUtils.sendPrivateMessage(event.getAuthor(), "You didn't set the question!");
            return;
        } else if (poll.getOptionsLength() < 1) {
            JDAUtils.sendPrivateMessage(event.getAuthor(), "There is no option to choose from!");
            return;
        }

        user = event.getAuthor();
        member = bot.getJDA().getGuildById(poll.getGuildID()).getMember(user);
        channel = user.openPrivateChannel().complete();

        if (poll.getRolesToMention().isEmpty()) {
            channel.sendMessage("You forgot to select at least one role to include in this poll!").queue();
            return;
        } else if (!member.getVoiceState().inVoiceChannel() && poll.isVoiceRestricted()) {
            channel.sendMessage("You enabled voice restrictions, but left the voice channel!").queue();
            return;
        }

        channel.sendMessage("The poll is being sent.").queue();

        if (!bot.pollsInProcess.contains(poll.getPollID())) {
            bot.pollsInProcess.add(poll.getPollID());
        }

        try {
            Guild guild = event.getJDA().getGuildById(poll.getGuildID());
            ArrayList<String> roleIDs = poll.getRolesToMention();
            HashMap<String, Member> membersToMessage = new HashMap<>();
            MessageEmbed embed = poll.generateMessageEmbed(bot);
            Iterator<Member> memberList;
            Member memberToMessage = null;
            String[] emotes = new String[poll.getOptionsSize()];

            System.arraycopy(Poll.NUMBER_EMOTES, 0, emotes, 0, emotes.length);

            for (String roleID : roleIDs) {
                try {
                    Role role = guild.getRoleById(roleID);
                    List<Member> membersWithRole = guild.getMembersWithRoles(role);
                    ArrayList<String> memberIDsInVoice = new ArrayList<>();

                    if (poll.isVoiceRestricted()) {
                        List<Member> voiceMembers = member.getVoiceState().getChannel().getMembers();

                        for (Member voiceMember : voiceMembers) {
                            memberIDsInVoice.add(voiceMember.getId());
                        }
                    }

                    for (Member memberWithRole : membersWithRole) {
                        String memberID = memberWithRole.getId();

                        if (!membersToMessage.containsKey(memberID)
                            && (!poll.isVoiceRestricted() || memberIDsInVoice.contains(memberWithRole.getId()))) {

                            membersToMessage.put(memberID, memberWithRole);
                        }
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Role " + roleID + " is not a number");
                } catch (NullPointerException e) {
                    System.out.println("Role " + roleID + " no longer exists (if it ever did)");
                }
            }

            memberList = membersToMessage.values().iterator();

            while (memberList.hasNext()) {
                Message messageToSend = null;
                boolean messageable = true;

                memberToMessage = memberList.next();

                if (memberToMessage.getId().equals(user.getId())) {
                    isUserPolled = true;
                    channel.deleteMessageById(poll.getLastCreatorMessageID()).queue();
                    channel.sendMessageEmbeds(generatePollDataCommandEmbed(poll)).queue();
                }

                try {
                    MessageChannel memberChannel = memberToMessage.getUser().openPrivateChannel().complete();
                    messageToSend = memberChannel.sendMessageEmbeds(embed).setActionRows(poll.generateActionRows(0, poll.getOptionsSize(), ButtonSelectionType.PollSelection)).complete();
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

                poll.addNewUser(memberToMessage, messageToSend == null ? "0" : messageToSend.getId(), messageable);
            }

            poll.setTimeFirstPolled(new Date());
            IOUtils.writeGson(Poll.getFileLocation(bot, poll.getPollID()), poll, true);
            bot.pollsBeingCreated.remove(user.getId());
        } catch (IOException e) {
            channel.sendMessage("There was a problem with saving. Please let MC_2018#9481 know of this issue.");
            bot.pollsInProcess.remove(poll.getPollID());
            e.printStackTrace();
            return;
        } catch (Exception e) {
            channel.sendMessage("There was a problem with the execution. Please let MC_2018#9481 know of this issue.");
            bot.pollsInProcess.remove(poll.getPollID());
            e.printStackTrace();
            return;
        }

        bot.pollsInProcess.remove(poll.getPollID());

        if (!isUserPolled) {
            channel.deleteMessageById(poll.getLastCreatorMessageID()).queue();
            channel.sendMessageEmbeds(generatePollDataCommandEmbed(poll)).queue();
        }

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

            channel.sendMessage(messageToSend).queue();
        }
    }

    public static MessageEmbed generatePollDataCommandEmbed(Poll poll) {
        EmbedBuilder builder = new EmbedBuilder();

        builder.setTitle("Poll Sent! Poll ID: " + poll.getPollID());
        builder.addField("The following commands can now be used:", "", false);
        builder.addField("Resend", "Sends a message to people who haven't replied.\n" +
                "Usage: **resend " + poll.getPollID() + "**", false);
        builder.addField("Polldata", "Returns the results of the poll and total responses.\n" +
                "Usage: **polldata " + poll.getPollID() + "**", false);
        builder.addField("Polldata Excel", "Returns an Excel sheet with names and specific responses.\n" +
                "Usage: **polldata excel " + poll.getPollID() + "**", false);

        return builder.build();
    }

}
