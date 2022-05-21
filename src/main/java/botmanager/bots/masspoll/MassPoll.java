package botmanager.bots.masspoll;

import botmanager.bots.masspoll.commands.*;
import botmanager.bots.masspoll.commands.creation.*;
import botmanager.bots.masspoll.commands.polling.CommentCommand;
import botmanager.bots.masspoll.commands.polling.VoteCommand;
import botmanager.bots.masspoll.commands.pollmanagement.PollDataCommand;
import botmanager.bots.masspoll.commands.pollmanagement.ResendCommand;
import botmanager.bots.masspoll.objects.Poll;
import botmanager.generic.BotBase;
import botmanager.generic.ICommand;
import botmanager.utils.IOUtils;
import botmanager.utils.JDAUtils;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.NoSuchElementException;

public class MassPoll extends BotBase {

    final public HashMap<String, Poll> pollsBeingCreated = new HashMap<>();
    final public ArrayList<Long> pollsInProcess = new ArrayList<>();

    public MassPoll(String botToken, String name) {
        super(botToken, name);
        getJDA().getPresence().setActivity(Activity.playing("send 'masspoll' in DM"));

        setCommands(new ICommand[] {
                // Creation
                new AddMemberCommand(this),
                new AddOptionCommand(this),
                new CancelCommand(this),
                new EditOptionCommand(this),
                new MassPollCommand(this),
                new QuestionCommand(this),
                new RemoveMemberCommand(this),
                new RemoveOptionCommand(this),
                new ReorderOptionCommand(this),
                new SelectRolesCommand(this),
                new SendCommand(this),

                // Polling
                new CommentCommand(this),
                new VoteCommand(this),

                // Poll Management
                new PollDataCommand(this),
                new ResendCommand(this),

                // Other
                new GuildJoinCommand(this),
        });
    }

    @Override
    public void onButtonClick(ButtonClickEvent event) {
        event.deferEdit().queue();

        for (ICommand command : getCommands()) {
            command.run(event);
        }
    }

    @Override
    public void onPrivateMessageReceived(PrivateMessageReceivedEvent event) {
        for (ICommand command : getCommands()) {
            if (event.getAuthor().getIdLong() != getJDA().getSelfUser().getIdLong()) {
                command.run(event);
            }
        }
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        for (ICommand command : getCommands()) {
            if (event.getAuthor().getIdLong() != getJDA().getSelfUser().getIdLong()) {
                command.run(event);
            }
        }
    }

    @Override
    public void onMessageReactionAdd(MessageReactionAddEvent event) {
        for (ICommand command : getCommands()) {
            command.run(event);
        }
    }

    @Override
    public void onGuildJoin(GuildJoinEvent event) {
        for (ICommand command : getCommands()) {
            command.run(event);
        }
    }

}
