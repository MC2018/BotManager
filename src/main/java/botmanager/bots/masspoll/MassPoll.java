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
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;

import java.util.ArrayList;
import java.util.HashMap;

public class MassPoll extends BotBase {

    final public String DEV_NAME = "MC_2018#9481";
    final public HashMap<String, Poll> POLLS_BEING_CREATED = new HashMap<>();
    final private ArrayList<Long> POLLS_IN_PROCESS = new ArrayList<>();

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

                // General
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

    public boolean isPollInProcess(long pollID) {
        return POLLS_IN_PROCESS.contains(pollID);
    }

    public void addPollInProcess(long pollID) {
        POLLS_IN_PROCESS.add(pollID);
    }

    public void removePollInProcess(long pollID) {
        POLLS_IN_PROCESS.remove(pollID);
    }

}
