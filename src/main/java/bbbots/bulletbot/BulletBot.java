package bbbots.bulletbot;

import bbbots.bulletbot.commands.BirthDateCommand;
import bbbots.bulletbot.commands.JoinDateCommand;
import bbbots.bulletbot.commands.NewbieCommand;
import bbbots.bulletbot.generic.BulletBotCommandBase;
import botmanager.generic.BotBase;
import botmanager.generic.ICommand;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

/**
 *
 * @author MC_2018 <mc2018.git@gmail.com>
 */
public class BulletBot extends BotBase {

    public BulletBot(String botToken, String name) {
        super(botToken, name);
        getJDA().getPresence().setActivity(Activity.playing("Abuse"));
        setPrefix("!");
        setCommands(new BulletBotCommandBase[] {
            new NewbieCommand(this),
            new BirthDateCommand(this),
            new JoinDateCommand(this)
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
    
}
