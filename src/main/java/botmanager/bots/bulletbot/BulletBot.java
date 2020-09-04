package botmanager.bots.bulletbot;

import botmanager.bots.bulletbot.commands.*;
import botmanager.utils.IOUtils;
import botmanager.generic.BotBase;
import botmanager.generic.ICommand;
import botmanager.generic.commands.PMForwarderCommand;
import botmanager.generic.commands.PMRepeaterCommand;
import java.io.File;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import botmanager.utils.JDAUtils;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

/**
 *
 * @author MC_2018 <mc2018.git@gmail.com>
 */
public class BulletBot extends BotBase {

    private TimerTask timerTask;
    private Timer timer = new Timer();
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
            new PMForwarderCommand(this),
            new BanLogCommand(this)
        });
        
        loadDirtyWords();
        startTimer();
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

    private void startTimer() {
        timerTask = new TimerTask() {
            @Override
            public void run() {
                checkNewMembers();
            }
        };

        timer.schedule(timerTask, 5000, 60000);
    }

    private void checkNewMembers() {
        String primaryGuildID = "551565232867246080"; // TODO: very malpractice
        Guild guild = getJDA().getGuildById(primaryGuildID);
        Role role = JDAUtils.findRole(guild,"New Account");
        List<Member> members = guild.getMembersWithRoles(role);

        for (Member member : members) {
            SimpleDateFormat sdf = new SimpleDateFormat("MMMMM d, yyyy");
            Date lastWeek = Date.from(Instant.now().minusSeconds(60 * 60 * 24 * 7));
            Date userCreationDate = Date.from(member.getTimeCreated().toInstant());

            if (userCreationDate.before(lastWeek)) {
                guild.removeRoleFromMember(member, role).complete();
                JDAUtils.sendGuildMessage(guild.getTextChannelsByName("action-logs", true).get(0),
                        "The user " + member.getAsMention() + " was made on: " + sdf.format(userCreationDate) + ". " +
                                "They have just been released from the new account waiting room. Watch out for em!");
            }
        }
    }

    public String getPrefix() {
        return prefix;
    }

    public Iterable<String> getDirtyWords() {
        return dirtyWords;
    }
    
}
