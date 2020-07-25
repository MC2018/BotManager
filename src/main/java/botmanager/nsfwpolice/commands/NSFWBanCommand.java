package botmanager.nsfwpolice.commands;

import botmanager.JDAUtils;
import botmanager.nsfwpolice.NSFWPolice;
import botmanager.nsfwpolice.generic.NSFWPoliceCommandBase;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRoleAddEvent;

/**
 *
 * @author MC_2018 <mc2018.git@gmail.com>
 */

public class NSFWBanCommand extends NSFWPoliceCommandBase {

    ArrayList<MemberInstanceCounter> memberCounterList;
    String lastMemberCaught = "No user has been caught since starting up.";
    final String PRIVATE_MESSAGE = "Unfortunately, you are not able to access the NSFW content on the BulletBarry server due to having the NSFW-Ban role.\n"
            + "If you believe this to be in error, and you are of age, please let one of the mods/admins know about the problem.";
    
    public class MemberInstanceCounter {
        Member member;
        long time = System.currentTimeMillis();
        
        public MemberInstanceCounter(Member member) {
            this.member = member;
        }
        
    }
    
    public NSFWBanCommand(NSFWPolice bot) {
        super(bot);
    }

    @Override
    public void run(Event genericEvent) {
        GuildMemberRoleAddEvent event;
        Member member;
        Role role;
        boolean hasNSFWBan;
        boolean hasNSFW;
        
        if (!(genericEvent instanceof GuildMemberRoleAddEvent)) {
            return;
        }
        
        event = (GuildMemberRoleAddEvent) genericEvent;
        
        member = event.getMember();
        role = JDAUtils.getRole(event, "NSFW");
        hasNSFWBan = JDAUtils.hasRole(member, "NSFW-Ban");
        hasNSFW = JDAUtils.hasRole(member, "NSFW");
        
        if (hasNSFW && hasNSFWBan) {
            DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
            
            try {
                event.getGuild().removeRoleFromMember(member, role).complete();
                JDAUtils.sendPrivateMessage(member.getUser(), PRIVATE_MESSAGE);
                lastMemberCaught = member.getUser().getName()
                        + "#" + member.getUser().getDiscriminator()
                        + ", " + dateFormat.format(new Date());
            } catch (Exception e) {
                lastMemberCaught = member.getUser().getName()
                        + "#" + member.getUser().getDiscriminator()
                        + ", " + dateFormat.format(new Date())
                        + " (Could not msg)";
            }
        }
    }

    @Override
    public String info() {
        return null;
    }
    
    public void sendNSFWMessage(Member member) {
        for (int i = 0; i < memberCounterList.size(); i++) {
            if (memberCounterList.get(i).member.getId().equals(member.getId())) {
                if (System.currentTimeMillis() - memberCounterList.get(i).time < (10*60*1000)) {
                    JDAUtils.sendPrivateMessage(member.getUser(), getWittyReply());
                } else {
                    JDAUtils.sendPrivateMessage(member.getUser(), PRIVATE_MESSAGE);
                }
                
                memberCounterList.get(i).time = System.currentTimeMillis();
                return;
            } else if (System.currentTimeMillis() - memberCounterList.get(i).time > (10*60*1000)) {
                memberCounterList.remove(i);
                i--;
            }
        }
        
        memberCounterList.add(new MemberInstanceCounter(member));
        JDAUtils.sendPrivateMessage(member.getUser(), PRIVATE_MESSAGE);
    }
    
    private String getWittyReply() {
        String[] replies = {
            "Stop trying.",
            "Please stop trying.",
            "What else do you want?",
            "Calm down, it's just porn.",
            "Don't worry about it.",
            "You're not missing out on much.",
            "When you're old enough, let us know.",
            "Don't make me tell your parents on you.",
            "Oi, you're persistent. Doesn't help ya by pressing it again.",
            "Just enjoy the rest of the server.",
            "There's other things to look at than porn.",
            "You'll be 18 eventually, wait until then.",
        };
        
        return replies[(int) (Math.random() * replies.length)];
    }
    
    public String getLastMemberCaught() {
        return lastMemberCaught;
    }
    
}
