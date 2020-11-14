package botmanager.bots.gitmanager.commands.tasks;

import botmanager.generic.commands.IMessageReceivedCommand;
import botmanager.utils.JDAUtils;
import botmanager.bots.gitmanager.GitManager;
import botmanager.bots.gitmanager.generic.GitManagerCommandBase;
import java.awt.Color;
import java.util.*;

import botmanager.utils.Utils;
import net.dv8tion.jda.api.*;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

/**
 *
 * @author MC_2018 <mc2018.git@gmail.com>
 */

public class TaskPurgeCommand extends GitManagerCommandBase implements IMessageReceivedCommand {

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
        bot.prefix + "task purge"
    };
    
    public TaskPurgeCommand(GitManager bot) {
        super(bot);
    }

    @Override
    public void runOnMessage(MessageReceivedEvent event) {
        EmbedBuilder eb = new EmbedBuilder();
        GuildResetInfo guildResetInfo;
        Guild guild = event.isFromGuild() ? event.getGuild() : bot.getJDA().getGuildById(bot.getUserSettings(event.getAuthor().getIdLong()).getDefaultGuildID());
        Member member = event.isFromGuild() ? event.getMember() : guild.getMember(event.getAuthor());
        String input = Utils.startsWithReplace(event.getMessage().getContentRaw(), KEYWORDS);
        
        if (input == null || (!member.hasPermission(Permission.ADMINISTRATOR) && !member.getId().equals("106949500500738048"))) {
            return;
        }
        
        if (!guildResetInfos.containsKey(guild.getIdLong())) {
            guildResetInfos.put(guild.getIdLong(), new GuildResetInfo());
        }
        
        guildResetInfo = guildResetInfos.get(guild.getIdLong());
        
        if (!guildResetInfo.recentlyRun && input.equals("")) {
            guildResetInfo.resetRandomNumber();
            eb.setTitle("Confirm Purge");
            eb.setDescription("Type `" + KEYWORDS[0] + " " + guildResetInfo.randomNumber + "` within 15 seconds to purge all tasks.\n\n" +
                    "***Understand that this will delete ALL tasks!***");
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
    public MessageEmbed.Field info() {
        return new MessageEmbed.Field("Purging all Tasks", "```" + KEYWORDS[0] + "```(Only accessible by admins)", false);
    }

    @Override
    public MessageEmbed getFailureEmbed() {
        return null;
    }

}
