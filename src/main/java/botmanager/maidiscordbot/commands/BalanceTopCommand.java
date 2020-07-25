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
public class BalanceTopCommand extends MaiDiscordBotCommandBase {

    final String[] KEYWORDS = {
        "baltop",
        "balancetop",
        "leaderboard",
        "lb",
        "top"
    };

    public BalanceTopCommand(BotBase bot) {
        super(bot);
    }

    @Override
    public void run(Event genericEvent) {
        GuildMessageReceivedEvent event;
        Guild guild;
        File[] files;
        String message = null;
        String result = "";
        int size = 5;
        boolean found = false;
        
        if (!(genericEvent instanceof GuildMessageReceivedEvent)) {
            return;
        }

        event = (GuildMessageReceivedEvent) genericEvent;

        for (String keyword : KEYWORDS) {
            if (event.getMessage().getContentRaw().startsWith(bot.getPrefix() + keyword)) {
                message = event.getMessage().getContentRaw().replace(bot.getPrefix() + keyword, "").replaceAll(" ", "");
                found = true;
                break;
            }
        }

        if (!found) {
            return;
        }

        if (!message.isEmpty()) {
            try {
                size = Integer.parseInt(message);
                
                if (size > 20) {
                    result += "Limiting the search to the top 20 members.\n\n";
                    size = 20;
                }
            } catch (Exception e) {
            }
        }
        
        guild = event.getGuild();
        files = new File("data/" + bot.getName() + "/guilds/" + event.getGuild().getId() + "/members/").listFiles();

        int[] baltop = new int[size];
        String[] baltopNames = new String[size];
        
        for (int i = 0; i < baltop.length; i++) {
            baltop[i] = 0;
        }
        
        for (File file : files) {
            try {
                Member member = guild.getMemberById(file.getName().replace(".csv", ""));
                String memberName;
                int balance;

                if (member == null) {
                    continue;
                }
                
                memberName = member.getEffectiveName();
                balance = bot.getUserBalance(member);
                
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
        
        result += "__**Balance Top:**__\n";
        
        for (int i = 0; i < baltop.length; i++) {
            if (baltopNames[i] == null) {
                i = baltop.length;
            } else {
                result += getNumericalSuffix(i) + ": " + baltopNames[i] + " with $" + baltop[i] + "\n";
            }
        }
        
        JDAUtils.sendGuildMessage(event.getChannel(), result);
    }

    @Override
    public String info() {
        return ""
                + "**" + bot.getPrefix() + "baltop** - shows the richest people on the server\n"
                + "**" + bot.getPrefix() + "baltop AMOUNT** - shows a set number of the richest people on the server";
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
