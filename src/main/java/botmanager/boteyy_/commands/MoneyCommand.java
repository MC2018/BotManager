package botmanager.boteyy_.commands;

import botmanager.generic.BotBase;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import botmanager.boteyy_.generic.Boteyy_CommandBase;

/**
 *
 * @author MC_2018 <mc2018.git@gmail.com>
 */
public class MoneyCommand extends Boteyy_CommandBase {

    TimerTask task;
    Timer timer;
    SimpleDateFormat sdf = new SimpleDateFormat("M/dd/yyyy hh:mm");
    ArrayList<Member> minuteMembers;
    String date;
    
    public MoneyCommand(BotBase bot) {
        super(bot);
        minuteMembers = new ArrayList<>();
        date = sdf.format(new Date());
        
        task = new TimerTask() {
            @Override
            public void run() {
                rewardVoiceChatMembers();
            }
        };
        
        timer = new Timer();
        timer.schedule(task, 5 * 60000, 5 * 60000);
    }

    @Override
    public void run(Event genericEvent) {
        GuildMessageReceivedEvent event;
        Guild guild;
        Member member;
        String date = sdf.format(new Date());
        
        if (!(genericEvent instanceof GuildMessageReceivedEvent)) {
            return;
        }
        
        event = (GuildMessageReceivedEvent) genericEvent;
        guild = event.getGuild();
        member = event.getMember();
        
        if (!this.date.equals(date)) {
            minuteMembers = new ArrayList<>();
            this.date = date;
        }
        
        for (Member minuteMember : minuteMembers) {
            if (guild.getId().equals(minuteMember.getGuild().getId()) && 
                    member.getId().equals(minuteMember.getId())) {
                return;
            }
        }
        
        bot.addUserBalance(member, (int) (Math.random() * 5 + 5));
        minuteMembers.add(member);
    }

    @Override
    public String info() {
        return "Earn money by being active in text channels and VC.";
    }

    public void rewardVoiceChatMembers() {
        List<VoiceChannel> voiceChannels = bot.getJDA().getVoiceChannels();
        
        for (VoiceChannel vc : voiceChannels) {
            List<Member> members = vc.getMembers();
            
            if (members.size() < 2 || vc.getName().equalsIgnoreCase("AFK")) {
                continue;
            }
            
            for (Member member : members) {
                if (!member.getVoiceState().isMuted() && !member.getVoiceState().isDeafened()) {
                    bot.addUserBalance(member, (int) (Math.random() * 5 + 5));
                    minuteMembers.add(member);
                }
            }
        }
    }
    
}
