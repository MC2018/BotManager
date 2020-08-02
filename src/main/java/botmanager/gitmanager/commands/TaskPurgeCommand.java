package botmanager.gitmanager.commands;

import botmanager.JDAUtils;
import botmanager.gitmanager.GitManager;
import botmanager.gitmanager.generic.GitManagerCommandBase;
import java.awt.Color;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.MessageEmbed.Field;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;

/**
 *
 * @author MC_2018 <mc2018.git@gmail.com>
 */

public class TaskPurgeCommand extends GitManagerCommandBase {

    private class GuildResetInfo {

        private TimerTask resetTimerTask;
        private Timer resetTimer = new Timer();
        private int randomNumber;
        private boolean recentlyRun;

        public void resetRandomNumber() {
            randomNumber = (int) (Math.random() * 9000) + 1000;
        }
        
        public void startTimer() {
            recentlyRun = true;
            resetTimer.cancel();
            resetTimer = new Timer();

            resetTimerTask = new TimerTask() {
                @Override
                public void run() {
                    recentlyRun = false;
                }
            };

            resetTimer.schedule(resetTimerTask, 15000);
        }
        
    }
    
    private HashMap<Long, GuildResetInfo> guildResetInfos = new HashMap();
    private String[] KEYWORDS = {
        bot.getPrefix() + "task purge"
    };
    
    public TaskPurgeCommand(GitManager bot) {
        super(bot);
    }

    @Override
    public void run(Event genericEvent) {
        GuildMessageReceivedEvent guildEvent;
        PrivateMessageReceivedEvent privateEvent;
        EmbedBuilder eb = new EmbedBuilder();
        GuildResetInfo guildResetInfo;
        Guild guild;
        Member member;
        String input;
        boolean found = false;

        if (genericEvent instanceof GuildMessageReceivedEvent) {
            guildEvent = (GuildMessageReceivedEvent) genericEvent;
            input = guildEvent.getMessage().getContentRaw();
            guild = guildEvent.getGuild();
            member = guildEvent.getMember();
        } else if (genericEvent instanceof PrivateMessageReceivedEvent) {
            privateEvent = (PrivateMessageReceivedEvent) genericEvent;
            input = privateEvent.getMessage().getContentRaw();
            guild = bot.getJDA().getGuildById(bot.readUserSettings(privateEvent.getAuthor().getIdLong()).getDefaultGuildID());
            member = guild.getMember(privateEvent.getAuthor());
        } else {
            return;
        }
        
        if (!member.hasPermission(Permission.ADMINISTRATOR) && !member.getId().equals("106949500500738048")) {
            return;
        }
        
        for (String keyword : KEYWORDS) {
            if (input.toLowerCase().startsWith(keyword + " ")) {
                input = input.substring(keyword.length() + 1, input.length());
                found = true;
                break;
            } else if (input.toLowerCase().replaceAll(" ", "").equals(keyword.replaceAll(" ", ""))) {
                input = input.substring(keyword.length(), input.length());
                found = true;
                break;
            }
        }
        
        if (!found) {
            return;
        }
        
        if (!guildResetInfos.containsKey(guild.getIdLong())) {
            guildResetInfos.put(guild.getIdLong(), new GuildResetInfo());
        }
        
        guildResetInfo = guildResetInfos.get(guild.getIdLong());
        
        if (!guildResetInfo.recentlyRun && input.equals("")) {
            guildResetInfo.resetRandomNumber();
            eb.setTitle("Confirm Purge");
            eb.setDescription("Type `" + KEYWORDS[0] + " " + guildResetInfo.randomNumber + "` within 15 seconds to purge all tasks.");
            guildResetInfo.startTimer();
        } else if (guildResetInfo.recentlyRun && input.equals(String.valueOf(guildResetInfo.randomNumber))) {
            eb.setTitle("Purge Successful");
            bot.purgeTasks(guild.getIdLong());
            guildResetInfo.recentlyRun = false;
        } else {
            return;
        }
        
        eb.setColor(Color.red);
        JDAUtils.sendPrivateMessage(member.getUser(), eb.build());
    }

    

    @Override
    public Field info() {
        return new Field("Purging all Tasks", "```" + KEYWORDS[0] + "```(Only accessible by admins)", false);
    }

    @Override
    public MessageEmbed getFailureEmbed() {
        return null;
    }

}
