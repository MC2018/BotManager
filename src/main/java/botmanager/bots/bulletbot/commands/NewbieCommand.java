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

import net.dv8tion.jda.api.entities.Role;
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
        Date lastTwoDays = Date.from(Instant.now().minusSeconds(60 * 60 * 24 * 2));
        Date lastWeek = Date.from(Instant.now().minusSeconds(60 * 60 * 24 * 7));
        Date lastMonth = Date.from(Instant.now().minusSeconds(60 * 60 * 24 * 30));
        SimpleDateFormat sdf = new SimpleDateFormat("MMMMM d, yyyy");
        
        if (!(genericEvent instanceof GuildMemberJoinEvent)) {
            return;
        }
        
        event = (GuildMemberJoinEvent) genericEvent;
        userCreationDate = Date.from(event.getUser().getTimeCreated().toInstant());

        if (userCreationDate.after(lastTwoDays)) {
            event.getMember().ban(0).reason("Logging in with a fresh account").queue();
            JDAUtils.sendGuildMessage(event.getGuild().getTextChannelsByName("bulletbot-logs", true).get(0),
                    "The user " + event.getUser().getAsMention() + " was made within the last two days " +
                            "and was subsequently banned.");
        } else if (userCreationDate.after(lastWeek)) {
            Role role = JDAUtils.findRole(event.getGuild(),"New Account");

            if (role != null) {
                event.getGuild().addRoleToMember(event.getMember(), JDAUtils.findRole(event.getGuild(),"New Account")).complete();
                JDAUtils.sendGuildMessage(event.getGuild().getTextChannelsByName("bulletbot-logs", true).get(0),
                        "The user " + event.getMember().getAsMention() + " was made on: " + sdf.format(userCreationDate) + ". " +
                                "They have been added to the new account waiting room.");
            } else {
                JDAUtils.sendGuildMessage(event.getGuild().getTextChannelsByName("bulletbot-logs", true).get(0),
                        "The user " + event.getMember().getAsMention() + " was made on: " + sdf.format(userCreationDate) + ". " +
                                "There was an issue and they were not added to the new account waiting room.");
            }
        } else if (userCreationDate.after(lastMonth)) {
            JDAUtils.sendGuildMessage(event.getGuild().getTextChannelsByName("bulletbot-logs", true).get(0),
                    "The user " + event.getMember().getAsMention() + " was made on: " + sdf.format(userCreationDate) + ". Watch out for em!");
        }
    }

    @Override
    public String info() {
        return null;
    }
}
