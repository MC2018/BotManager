package botmanager.bots.masspoll.commands.creation;

import botmanager.bots.masspoll.MassPoll;
import botmanager.bots.masspoll.generic.MassPollCommandBase;
import botmanager.bots.masspoll.objects.GuildSettings;
import botmanager.bots.masspoll.objects.Poll;
import botmanager.generic.commands.IMessageReceivedCommand;
import botmanager.utils.IOUtils;
import botmanager.utils.JDAUtils;
import botmanager.utils.Utils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.api.requests.ErrorResponse;

import java.io.IOException;
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
        List<Role> memberRoles;
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

        if (settings.permissionGroups.isEmpty()) {
            JDAUtils.sendPrivateMessage(member.getUser(), "Permissions for the server have not been set up yet!");
            return;
        } else if (settings.blacklistedUsers.contains(member.getId())) {
            JDAUtils.sendPrivateMessage(member.getUser(), "You are blacklisted from sending polls!");
            return;
        }

        memberRoles = member.getRoles();

        if (!settings.canCreatePoll(memberRoles)) {
            JDAUtils.sendPrivateMessage(member.getUser(), "Your roles don't permit you to make a poll!");
            return;
        }

        if (bot.POLLS_BEING_CREATED.get(member.getId()) != null) {
            JDAUtils.sendPrivateMessage(member.getUser(), "Send or cancel your current poll before starting again!");
            return;
        }

        poll = new Poll(bot, member.getId(), guild.getId());
        bot.POLLS_BEING_CREATED.put(member.getId(), poll);
        poll.setRolesToChooseFrom(settings.findMentionableRoles(memberRoles));

        if (poll.getRolesToChooseFrom().isEmpty()) {
            JDAUtils.sendPrivateMessage(member.getUser(), "There are no roles to mention found for this guild.");
            return;
        }

        try {
            MessageChannel channel = event.getAuthor().openPrivateChannel().complete();

            channel.sendMessageEmbeds(generateCommandsEmbed()).queue();
            poll.sendRoleSelectorMessage(member, JDAUtils.roleIDsToRoles(guild, poll.getRolesToChooseFrom()), channel);
            poll.sendTestPollMessage(guild, channel);
        } catch (ErrorResponseException e) {
            if (e.getErrorResponse().equals(ErrorResponse.CANNOT_SEND_TO_USER)) {
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
        builder.addField("Add/Remove/Edit/Reorder an Option", "Usage: `add option Yes!`\n"
                + "\u200B \u200B \u200B \u200B \u200B \u200B \u200B \u200B \u200B \u200B \u200B \u200B \u200B \u200B `remove option 1`\n"
                + "\u200B \u200B \u200B \u200B \u200B \u200B \u200B \u200B \u200B \u200B \u200B \u200B \u200B \u200B `edit option 1 Of course!`\n"
                + "\u200B \u200B \u200B \u200B \u200B \u200B \u200B \u200B \u200B \u200B \u200B \u200B \u200B \u200B `reorder option 1 2`", false);
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
