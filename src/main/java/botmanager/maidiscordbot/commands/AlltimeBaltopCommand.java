package botmanager.maidiscordbot.commands;

import botmanager.JDAUtils;
import botmanager.generic.BotBase;
import java.io.File;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import botmanager.maidiscordbot.generic.MaiDiscordBotCommandBase;

/**
 *
 * @author MC_2018 <mc2018.git@gmail.com>
 */

public class AlltimeBaltopCommand extends MaiDiscordBotCommandBase {

    final String[] KEYWORDS = {
        "alltimebaltop",
        "alltimebalancetop",
        "alltimeleaderboard",
        "alltimelb",
        "alltimetop"
    };
    
    public AlltimeBaltopCommand(BotBase bot) {
        super(bot);
    }

    @Override
    public void run(Event genericEvent) {
        GuildMessageReceivedEvent event;
        Guild guild;
        File[] files;
        String result;
        boolean found = false;

        if (!(genericEvent instanceof GuildMessageReceivedEvent)) {
            return;
        }

        event = (GuildMessageReceivedEvent) genericEvent;

        for (String keyword : KEYWORDS) {
            if (event.getMessage().getContentRaw().startsWith(bot.getPrefix() + keyword)) {
                found = true;
                break;
            }
        }

        if (!found) {
            return;
        }

        guild = event.getGuild();
        files = new File("data/" + bot.getName() + "/guilds/" + event.getGuild().getId() + "/members/").listFiles();

        int[] baltop = {0, 0, 0, 0, 0};
        String[] baltopNames = new String[5];

        for (File file : files) {
            try {
                Member member = guild.getMemberById(file.getName().replace(".csv", ""));
                String memberName;
                int balance;

                if (member == null) {
                    continue;
                }
                
                memberName = member.getEffectiveName();
                balance = bot.getUserBaltop(member);
                
                if (balance < 0 || member.getUser().isBot()) {
                    continue;
                }

                for (int i = baltop.length - 1; i >= 0; i--) {
                    if (balance > baltop[i]) {
                        if (i != baltop.length - 1) {
                            baltop[i + 1] = baltop[i];
                            baltopNames[i + 1] = baltopNames[i];
                        }
                        
                        baltop[i] = balance;
                        baltopNames[i] = memberName;
                    } else {
                        break;
                    }
                }
            } catch (Exception e) {
                
            }
        }
        
        result = "Alltime Balance Top:\n";
        
        for (int i = 0; i < baltop.length; i++) {
            result += getNumericalSuffix(i) + ": " + baltopNames[i] + " with $" + baltop[i] + "\n";
        }
        
        JDAUtils.sendGuildMessage(event.getChannel(), result);
    }

    @Override
    public String info() {
        return "**" + bot.getPrefix() + "alltimetop** - shows the richest people have ever been at any point on the server";
    }
    
    public String getNumericalSuffix(int index) {
        String result;
        int number = index + 1;
        result = number + "";
        
        if (11 <= number % 100 && number % 100 <= 14) {
            result += "th";
        } else if (number % 10 == 1) {
            result += "st";
        } else if (number % 10 == 2) {
            result += "nd";
        } else if (number % 10 == 3) {
            result += "rd";
        } else {
            result += "th";
        }
        
        return result;
    }
    
}
