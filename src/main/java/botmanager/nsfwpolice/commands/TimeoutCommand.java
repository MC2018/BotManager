package botmanager.nsfwpolice.commands;

import botmanager.JDAUtils;
import botmanager.nsfwpolice.NSFWPolice;
import botmanager.nsfwpolice.generic.NSFWPoliceCommandBase;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRoleAddEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRoleRemoveEvent;

/**
 *
 * @author MC_2018 <mc2018.git@gmail.com>
 */

public class TimeoutCommand extends NSFWPoliceCommandBase {

    public TimeoutCommand(NSFWPolice bot) {
        super(bot);
    }

    @Override
    public void run(Event genericEvent) {
        if (genericEvent instanceof GuildMemberRoleAddEvent) {
            runRoleAdd((GuildMemberRoleAddEvent) genericEvent);
        } else if (genericEvent instanceof GuildMemberRoleRemoveEvent) {
            runRoleRemove((GuildMemberRoleRemoveEvent) genericEvent);
        }
    }

    public void runRoleAdd(GuildMemberRoleAddEvent event) {
        Member member = event.getMember();
        Role role = JDAUtils.getRole(event, "Member");
        boolean hasTimeout = JDAUtils.hasRole(member, "Timeout");
        boolean hasMember = JDAUtils.hasRole(member, "Member");
        
        if (hasMember && hasTimeout) {
            try {
                event.getGuild().removeRoleFromMember(member, role).complete();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    public void runRoleRemove(GuildMemberRoleRemoveEvent event) {
        Member member = event.getMember();
        Role role = JDAUtils.getRole(event, "Member");
        boolean hasTimeout = JDAUtils.hasRole(member, "Timeout");
        boolean hasMember = JDAUtils.hasRole(member, "Member");
        
        if (!hasMember && !hasTimeout) {
            try {
                event.getGuild().addRoleToMember(member, role).complete();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    @Override
    public String info() {
        return null;
    }
    
}
