package botmanager.nsfwpolice;

import botmanager.nsfwpolice.commands.MuteCommand;
import botmanager.nsfwpolice.commands.NSFWBanCommand;
import botmanager.nsfwpolice.commands.TimeoutCommand;
import botmanager.generic.ICommand;
import botmanager.generic.BotBase;
import botmanager.generic.commands.PMForwarderCommand;
import botmanager.generic.commands.PMRepeaterCommand;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRoleAddEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRoleRemoveEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;

/**
 *
 * @author MC_2018 <mc2018.git@gmail.com>
 */
public class NSFWPolice extends BotBase {

    public class MemberInstanceCounter {
        Member member;
        long time = System.currentTimeMillis();
        
        public MemberInstanceCounter(Member member) {
            this.member = member;
        }
    }
    
    public NSFWPolice(String botToken, String name) {
        super(botToken, name);
        getJDA().getPresence().setActivity(Activity.watching("everyone in NSFW..."));
        
        setCommands(new ICommand[] {
            new NSFWBanCommand(this),
            new MuteCommand(this),
            new TimeoutCommand(this),
            new PMRepeaterCommand(this),
            new PMForwarderCommand(this)
        });
    }
    
    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        for (ICommand command : getCommands()) {
            command.run(event);
        }
    }
    
    @Override
    public void onGuildMemberRoleAdd(GuildMemberRoleAddEvent event) {
        for (ICommand command : getCommands()) {
            command.run(event);
        }
    }
    
    @Override
    public void onGuildMemberRoleRemove(GuildMemberRoleRemoveEvent event) {
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
    
    public String getLastMemberCaught() {
        for (ICommand command : getCommands()) {
            if (command instanceof NSFWBanCommand) {
                return ((NSFWBanCommand) command).getLastMemberCaught();
            }
        }
        
        return null;
    }
    
}
