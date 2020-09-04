package botmanager.bots.bulletbot.commands;

import botmanager.bots.bulletbot.BulletBot;
import botmanager.bots.bulletbot.generic.BulletBotCommandBase;
import botmanager.utils.JDAUtils;
import net.dv8tion.jda.api.audit.ActionType;
import net.dv8tion.jda.api.audit.AuditLogEntry;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.guild.GuildBanEvent;
import net.dv8tion.jda.api.requests.restaction.pagination.AuditLogPaginationAction;

public class BanLogCommand extends BulletBotCommandBase {

    public BanLogCommand(BulletBot bot) {
        super(bot);
    }

    @Override
    public void run(Event genericEvent) {
        GuildBanEvent event;
        AuditLogPaginationAction auditLogEntries;
        AuditLogEntry entry;

        if (genericEvent instanceof GuildBanEvent) {
            return;
        }

        event = (GuildBanEvent) genericEvent;
        auditLogEntries = event.getGuild().retrieveAuditLogs();
        entry = auditLogEntries.getLast();

        if (entry.getType() == ActionType.BAN && !entry.getUser().isBot()) {
            JDAUtils.sendGuildMessage(event.getGuild().getTextChannelsByName("action-logs", true).get(0),
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
