package botmanager.bulletbot;

import botmanager.IOUtils;
import botmanager.bulletbot.commands.BirthDateCommand;
import botmanager.bulletbot.commands.InfoCommand;
import botmanager.bulletbot.commands.JoinDateCommand;
import botmanager.bulletbot.commands.NewbieCommand;
import botmanager.bulletbot.commands.WordTrackerCommand;
import botmanager.generic.BotBase;
import botmanager.generic.ICommand;
import botmanager.generic.commands.PMForwarderCommand;
import botmanager.generic.commands.PMRepeaterCommand;
import java.io.File;
import java.util.List;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

/**
 *
 * @author MC_2018 <mc2018.git@gmail.com>
 */
public class BulletBot extends BotBase {

    private List<String> dirtyWords;
    private String prefix;

    public BulletBot(String botToken, String name) {
        super(botToken, name);
        getJDA().getPresence().setActivity(Activity.playing("Abuse"));
        prefix = "!";

        setCommands(new ICommand[] {
            new NewbieCommand(this),
            new BirthDateCommand(this),
            new JoinDateCommand(this),
            new InfoCommand(this),
            new WordTrackerCommand(this),
            new PMRepeaterCommand(this),
            new PMForwarderCommand(this)
        });
        
        loadDirtyWords();
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
    
    public void loadDirtyWords() {
        File file = new File("data/" + getName() + "/dirty_words.txt");
        
        if (!file.exists()) {
            IOUtils.write(file, "");
        }
        
        dirtyWords = IOUtils.readLines(file);
    }

    public String getPrefix() {
        return prefix;
    }

    public Iterable<String> getDirtyWords() {
        return dirtyWords;
    }
    
}
