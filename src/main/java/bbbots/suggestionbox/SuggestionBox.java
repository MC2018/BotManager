package bbbots.suggestionbox;

import bbbots.suggestionbox.commands.PMRepeaterCommand;
import bbbots.suggestionbox.commands.ReactionManagerCommand;
import bbbots.suggestionbox.commands.SuggestionCommand;
import bbbots.suggestionbox.generic.SuggestionBoxCommandBase;
import botmanager.generic.BotBase;
import botmanager.generic.ICommand;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;

/**
 *
 * @author MC_2018 <mc2018.git@gmail.com>
 */
public class SuggestionBox extends BotBase {

    public class MemberInstanceCounter {
        Member member;
        long time = System.currentTimeMillis();
        
        public MemberInstanceCounter(Member member) {
            this.member = member;
        }
    }
    
    public SuggestionBox(String botToken, String name) {
        super(botToken, name);
        getJDA().getPresence().setActivity(Activity.listening("your bad ideas!"));
        setPrefix("~");
        setCommands(new SuggestionBoxCommandBase[] {
            new SuggestionCommand(this),
            new ReactionManagerCommand(this),
            new PMRepeaterCommand(this)
        });
    }
    
    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        for (ICommand command : getCommands()) {
            command.run(event);
        }
    }
    
    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        for (ICommand command : getCommands()) {
            command.run(event);
        }
    }
    
    @Override
    public void onGuildMessageReactionAdd(GuildMessageReactionAddEvent event) {
        for (ICommand command : getCommands()) {
            command.run(event);
        }
    }
    
    @Override
    public void onPrivateMessageReceived​(PrivateMessageReceivedEvent event) {
        for (ICommand command : getCommands()) {
            command.run(event);
        }
    }
    
}
