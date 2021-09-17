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

        if (message == null || ((event.isFromGuild() && !message.equals("")))) {
            return;
        }

        try {
            guildNumber = Integer.parseInt(message);
        } catch (NumberFormatException e) {
        }

        if (event.isFromGuild()) {
            guild = event.getGuild();
            member = event.getMember();
            event.getMessage().delete().queue();
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

        if (settings.mentionableRoles.isEmpty()) {
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
            ArrayList<Role> roles = new ArrayList<>(member.getGuild().getRoles());

            for (int i = 0; i < roles.size(); i++) {
                if (!settings.mentionableRoles.contains(roles.get(i).getId())) {
                    roles.remove(i);
                    i--;
                }
            }

            if (roles.isEmpty()) {
                JDAUtils.sendPrivateMessage(member.getUser(), "There are no roles to mention found for this guild.");
            }

            channel.sendMessageEmbeds(generateCommandsEmbed()).queue();
            poll.setRolesToChooseFrom(settings.mentionableRoles);
            poll.sendRoleSelectorMessage(member, roles, channel);
            poll.sendTestPollMessage(guild, channel);
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
        builder.addField("Set the Question", "Usage: `set question Should we get pizza?`", false);
        builder.addField("Add or Remove an Option", "Usage: `add option Yes!`\n"
                + "\u200B \u200B \u200B \u200B \u200B \u200B \u200B \u200B \u200B \u200B \u200B \u200B \u200B \u200B `remove option 1`", false);
        builder.addField("Add or Remove an Individual Member", "Works w/an ID or partial username/nickname\n"
                + "Usage: `add member TedNugent_420`\n"
                + "\u200B \u200B \u200B \u200B \u200B \u200B \u200B \u200B \u200B \u200B \u200B \u200B \u200B \u200B `remove member GunterFan1`", false);
        builder.addField("Cancel the Poll", "Usage: `cancel`", false);
        builder.addField("Send the Poll", "Usage: `send`", false);
        builder.addField("", "**Things to be aware of:**\n"
                + "Add a way to abstain so everyone will reply.\n"
                + "Once sent, you can't cancel/modify the poll.\n"
                + "People can comment by replying to the poll's message.", false);

        return builder.build();
    }

}
