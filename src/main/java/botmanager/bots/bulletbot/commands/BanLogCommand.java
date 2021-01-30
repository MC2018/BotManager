package botmanager.bots.bulletbot.commands;

import botmanager.bots.bulletbot.BulletBot;
import botmanager.bots.bulletbot.generic.BulletBotCommandBase;
import botmanager.generic.commands.IGuildBanCommand;
import botmanager.utils.JDAUtils;
import net.dv8tion.jda.api.audit.ActionType;
import net.dv8tion.jda.api.audit.AuditLogEntry;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.guild.GuildBanEvent;
import net.dv8tion.jda.api.requests.restaction.pagination.AuditLogPaginationAction;

public class BanLogCommand extends BulletBotCommandBase implements IGuildBanCommand {

    public BanLogCommand(BulletBot bot) {
        super(bot);
    }

    public void runOnGuildBan(GuildBanEvent event) {
        AuditLogPaginationAction auditLogEntries = event.getGuild().retrieveAuditLogs();
        AuditLogEntry entry = auditLogEntries.getLast();

        if (entry.getType() == ActionType.BAN && !entry.getUser().isBot()) {
            JDAUtils.sendGuildMessage(event.getGuild().getTextChannelsByName("bulletbot-logs", true).get(0),
                    "The user " + event.getUser().getAsMention() + " was banned by "
                    + entry.getUser().getAsMention() + ". Please give the reasoning for the ban below."
            );
        }
    }

    @Override
    public String info() {
        return null;
    }

}
