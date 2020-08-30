package botmanager.bots.bulletbot.commands;

/**
 *
 * @author MC_2018 <mc2018.git@gmail.com>
 */

import botmanager.utils.JDAUtils;
import botmanager.bots.bulletbot.BulletBot;
import botmanager.bots.bulletbot.generic.BulletBotCommandBase;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;

public class NewbieCommand extends BulletBotCommandBase {
    
    public NewbieCommand(BulletBot bot) {
        super(bot);
    }

    @Override
    public void run(Event genericEvent) {
        GuildMemberJoinEvent event;
        Date userCreationDate;
        Date lastMonth = Date.from(Instant.now().minusSeconds(60 * 60 * 24 * 30));
        SimpleDateFormat sdf = new SimpleDateFormat("MMMMM d, yyyy");
        
        if (!(genericEvent instanceof GuildMemberJoinEvent)) {
            return;
        }
        
        event = (GuildMemberJoinEvent) genericEvent;
        userCreationDate = Date.from(event.getUser().getTimeCreated().toInstant());
        
        if (userCreationDate.after(lastMonth)) {
            JDAUtils.sendGuildMessage(event.getGuild().getTextChannelsByName("action-logs", true).get(0),
                    "The user " + event.getMember().getAsMention() + " was made on: " + sdf.format(userCreationDate) + ". Watch out for em!");
        }
    }

    @Override
    public String info() {
        return null;
    }
}
