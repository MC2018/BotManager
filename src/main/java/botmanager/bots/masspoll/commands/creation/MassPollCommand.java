package botmanager.bots.masspoll.commands.creation;

import botmanager.bots.masspoll.MassPoll;
import botmanager.bots.masspoll.generic.MassPollCommandBase;
import botmanager.bots.masspoll.objects.ButtonSelectionType;
import botmanager.bots.masspoll.objects.GuildSettings;
import botmanager.bots.masspoll.objects.Poll;
import botmanager.generic.commands.IMessageReceivedCommand;
import botmanager.utils.IOUtils;
import botmanager.utils.JDAUtils;
import botmanager.utils.Utils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MassPollCommand extends MassPollCommandBase implements IMessageReceivedCommand {

    public final String[] KEYWORDS = {
            "masspoll"
    };

    public MassPollCommand(MassPoll bot) {
        super(bot);
    }

    @Override
    public void runOnMessage(MessageReceivedEvent event) {
        Guild guild;
        Member member;
        GuildSettings settings;
        String message = Utils.startsWithReplace(event.getMessage().getContentRaw(), KEYWORDS);
        Poll poll;
        int guildNumber = 0;

        if (message == null) {
            return;
        }

        try {
            guildNumber = Integer.parseInt(message);
        } catch (NumberFormatException e) {
        }

        if (event.isFromGuild()) {
            guild = event.getGuild();
            member = event.getMember();

            try {
                event.getMessage().delete().queue();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            List<Guild> mutualGuilds = event.getAuthor().getMutualGuilds();

            if (mutualGuilds.isEmpty()) {
                return; // TODO: add a way for people to add the bot to a server? default so only admins can send polls
            } else if (mutualGuilds.size() == 1) {
                guild = mutualGuilds.get(0);
                member = guild.getMember(event.getAuthor());
            } else if (0 < guildNumber && guildNumber <= mutualGuilds.size()) {
                guild = mutualGuilds.get(guildNumber - 1);
                member = guild.getMember(event.getAuthor());
            } else {
                EmbedBuilder builder = new EmbedBuilder();
                MessageChannel channel = event.getAuthor().openPrivateChannel().complete();

                builder.setTitle("Select Server");
                builder.setDescription("You share multiple servers with this bot.\nPlease specify which one to send this poll with.");

                for (int i = 0; i < mutualGuilds.size(); i++) {
                    builder.addField(mutualGuilds.get(i).getName(), "**Usage:** masspoll " + (i + 1), false);
                }

                channel.sendMessageEmbeds(builder.build()).queue();
                return;
            }
        }

        try {
            settings = IOUtils.readGson(GuildSettings.getFileLocation(bot, guild.getId()), GuildSettings.class);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        if (settings.rolesToMention.isEmpty()) {
            JDAUtils.sendPrivateMessage(member.getUser(), "Permissions for the server have not been set up yet!");
            return;
        } else if (settings.whitelistedUsers.contains(member.getId()) || member.hasPermission(Permission.ADMINISTRATOR)) {
            // continue
        } else if (settings.blacklistedUsers.contains(member.getId())) {
            JDAUtils.sendPrivateMessage(member.getUser(), "You are blacklisted from sending polls!");
            return;
        } else {
            List<Role> memberRoles = member.getRoles();
            boolean roleFound = false;

            for (Role role : memberRoles) {
                if (settings.rolesToPoll.contains(role.getId())) {
                    roleFound = true;
                    break;
                }
            }

            if (!roleFound) {
                JDAUtils.sendPrivateMessage(member.getUser(), "Your roles don't permit you to make a poll!");
                return;
            }
        }

        if (bot.pollsBeingCreated.get(member.getId()) != null) {
            JDAUtils.sendPrivateMessage(member.getUser(), "Send or cancel your current poll before starting again!");
            return;
        }

        poll = new Poll(bot, member.getId(), guild.getId());
        bot.pollsBeingCreated.put(member.getId(), poll);

        try {
            MessageChannel channel = event.getAuthor().openPrivateChannel().complete();
            ArrayList<Role> rolesToMention = new ArrayList<>(guild.getRoles());

            for (int i = 0; i < rolesToMention.size(); i++) {
                if (!settings.rolesToMention.contains(rolesToMention.get(i).getId())) {
                    rolesToMention.remove(i);
                    i--;
                }
            }

            if (rolesToMention.isEmpty()) {
                return;
            }

            channel.sendMessageEmbeds(generateCommandsEmbed()).queue();
            poll.setRolesToChooseFrom(settings.rolesToMention);

            if (rolesToMention.size() > 1 || member.getVoiceState().inVoiceChannel()) {
                EmbedBuilder builder = new EmbedBuilder();
                String roleList = "";
                int buttonCount = rolesToMention.size();

                builder.setTitle("Select Roles");
                builder.setDescription("Select which roles the poll should send to.");

                for (int i = 0; i < rolesToMention.size(); i++) {
                    roleList += Poll.NUMBER_EMOTES[i] + " " + rolesToMention.get(i).getName() + "\n";
                }

                if (member.getVoiceState().inVoiceChannel()) {
                    roleList += Poll.NUMBER_EMOTES[rolesToMention.size()] + " *Apply only to people in the voice channel*\n";
                    poll.setVoiceRestrictedOption(true);
                    buttonCount++;
                }

                builder.addField("Options", roleList, false);
                channel.sendMessageEmbeds(builder.build())
                        .setActionRows(poll.generateActionRows(0, buttonCount, ButtonSelectionType.RoleSelection))
                        .queue();
            } else {
                poll.updateRolesToMention(0, true);
            }

            poll.sendExampleMessageEmbed(bot, member.getUser());
        } catch (ErrorResponseException e) {
            if (e.getErrorCode() == 50007) {
                System.out.println("Someone (ID " + member.getId() + ") tried to create a poll, but they have DMs closed.");
            } else {
                e.printStackTrace();
            }
        }
    }

    public static MessageEmbed generateCommandsEmbed() {
        EmbedBuilder builder = new EmbedBuilder();

        builder.setTitle("Instructions");
        builder.setDescription("Run a combination of these commands to generate your poll.");
        builder.addField("Set Question", "Sets the question for your poll.\n" +
                "Usage: **set question What should we do?**", false);
        builder.addField("Add Option", "Adds a poll option.\n" +
                "Usage: **add option Get pizza**", false);
        builder.addField("Remove Option", "Removes an option based on its number.\n" +
                "Usage: **remove option 1**", false);
        builder.addField("Cancel", "Cancels the poll.\n" +
                "Usage: **cancel**", false);
        builder.addField("Send", "Sends the poll out to people.\n" +
                "Usage: **send**", false);
        builder.addField("", "**Things to be aware of:**\n" +
                "Once sent, you can't cancel/modify the poll.\n" +
                "Add a way to abstain so everyone will reply.\n" +
                "People can comment by replying to the poll's message.", false);

        return builder.build();
    }

}
