package botmanager.bots.bulletbot.commands;

/**
 *
 * @author MC_2018 <mc2018.git@gmail.com>
 */

import botmanager.generic.commands.IGuildMessageReceivedCommand;
import botmanager.generic.commands.IMessageReceivedCommand;
import botmanager.utils.JDAUtils;
import botmanager.bots.bulletbot.BulletBot;
import botmanager.bots.bulletbot.generic.BulletBotCommandBase;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;

import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class NewbieCommand extends BulletBotCommandBase implements IGuildMessageReceivedCommand {
    
    public NewbieCommand(BulletBot bot) {
        super(bot);
    }

    public void runOnGuildMessage(GuildMessageReceivedEvent event) {
        Date userCreationDate;
        Date lastTwoDays = Date.from(Instant.now().minusSeconds(60 * 60 * 24 * 2));
        Date lastFiveDays = Date.from(Instant.now().minusSeconds(60 * 60 * 24 * 5));
        Date lastMonth = Date.from(Instant.now().minusSeconds(60 * 60 * 24 * 30));
        SimpleDateFormat sdf = new SimpleDateFormat("MMMMM d, yyyy");

        userCreationDate = Date.from(event.getAuthor().getTimeCreated().toInstant());

        if (userCreationDate.after(lastFiveDays)) {
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
        } else if (userCreationDate.after(lastMonth) && !event.getAuthor().getName().contains("Milk")) {
            JDAUtils.sendGuildMessage(event.getGuild().getTextChannelsByName("bulletbot-logs", true).get(0),
                    "The user " + event.getMember().getAsMention() + " was made on: " + sdf.format(userCreationDate) + ". Watch out for em!");
        }
    }

    @Override
    public String info() {
        return null;
    }
}
