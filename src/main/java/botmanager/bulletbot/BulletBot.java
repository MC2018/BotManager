package botmanager.bulletbot;

import botmanager.bulletbot.commands.BirthDateCommand;
import botmanager.bulletbot.commands.InfoCommand;
import botmanager.bulletbot.commands.JoinDateCommand;
import botmanager.bulletbot.commands.NewbieCommand;
import botmanager.bulletbot.generic.BulletBotCommandBase;
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

    private String prefix;

    public BulletBot(String botToken, String name) {
        super(botToken, name);
        getJDA().getPresence().setActivity(Activity.playing("Abuse"));
        prefix = "!";

        setCommands(new BulletBotCommandBase[] {
            new NewbieCommand(this),
            new BirthDateCommand(this),
            new JoinDateCommand(this),
            new InfoCommand(this)
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

    public String getPrefix() {
        return prefix;
    }
    
}
