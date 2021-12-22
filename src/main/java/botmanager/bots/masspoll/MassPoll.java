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
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MassPoll extends BotBase {

    public HashMap<String, Poll> pollsBeingCreated = new HashMap<>();
    public ArrayList<Long> pollsInProcess = new ArrayList<>();

    public MassPoll(String botToken, String name) {
        super(botToken, name);
        getJDA().getPresence().setActivity(Activity.playing("send 'masspoll' in DM"));

        setCommands(new ICommand[] {
                new GuildJoinCommand(this),
                new MassPollCommand(this),
                new QuestionCommand(this),
                new AddOptionCommand(this),
                new RemoveOptionCommand(this),
                new CancelCommand(this),
                new SendCommand(this),
                new VoteCommand(this),
                new CommentCommand(this),
                new ResendCommand(this),
                new PollDataCommand(this),
                new SelectRolesCommand(this),
                new AddMemberCommand(this),
                new RemoveMemberCommand(this),
                new EditOptionCommand(this),
                new ReorderOptionCommand(this)
        });
    }

    @Override
    public void onButtonClick(ButtonClickEvent event) {
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
