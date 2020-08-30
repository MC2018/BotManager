package botmanager.bots.maidiscordbot.commands.gambling;

import botmanager.utils.JDAUtils;
import botmanager.generic.BotBase;
import botmanager.bots.maidiscordbot.generic.MaiDiscordBotCommandBase;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import java.io.File;

public class HarvestCommand extends MaiDiscordBotCommandBase {

    public final String[] KEYWORDS = {
            bot.getPrefix() + "harvest",
            bot.getPrefix() + "h"
    };

    public HarvestCommand(BotBase bot) {
        super(bot);
    }

    @Override
    public void run(Event genericEvent) {
        GuildMessageReceivedEvent event;
        String message;
        String result = "";
        double chanceOfSuccess;
        int userExistingPlantAmount, totalPlantAmount;
        boolean found = false;

        if (!(genericEvent instanceof GuildMessageReceivedEvent)) {
            return;
        }

        event = (GuildMessageReceivedEvent) genericEvent;
        message = event.getMessage().getContentRaw();

        for (String keyword : KEYWORDS) {
            if (message.startsWith(keyword + " ")) {
                message = message.replaceFirst(keyword + " ", "");
                found = true;
                break;
            } else if (message.startsWith(keyword) && message.endsWith(keyword)) {
                message = message.replaceFirst(keyword, "");
                found = true;
                break;
            }
        }

        if (!found) {
            return;
        }

        if (bot.isHarvesting(event.getGuild())) {
            JDAUtils.sendGuildMessage(event.getChannel(), "Someone else beat you to it!");
            return;
        }

        userExistingPlantAmount = bot.getUserPlant(event.getMember());
        totalPlantAmount = bot.getTotalPlant(event.getGuild());

        if (message.equalsIgnoreCase("info")) {
            result = "$" + totalPlantAmount + " is planted right now.\n";
            result += "\n" + getNameOutput(event.getGuild());

            JDAUtils.sendGuildMessage(event.getChannel(), result);

            return;
        } else if (totalPlantAmount == 0) {
            result += "Nothing's planted, you bottomfeeder.";
        } else {
            chanceOfSuccess = chance(event.getMember(), event.getGuild());
            bot.setHarvesting(event.getGuild(), true);
            
            if (Math.random() * 2 < 1 + userExistingPlantAmount / totalPlantAmount) {
                result += event.getMember().getEffectiveName() + " harvested $" + totalPlantAmount + " at a " + clean(chanceOfSuccess) + " chance of success!";
                bot.addUserBalance(event.getMember(), totalPlantAmount);
            } else {
                result += event.getMember().getEffectiveName() + " botched a harvest of $" + totalPlantAmount + " with a " + clean(chanceOfSuccess) + " chance of success...";
            }
        }

        JDAUtils.sendGuildMessage(event.getChannel(), result);

        bot.resetPlanters(event.getGuild());
        bot.updatePlant(event.getGuild(), 0);
        bot.setHarvesting(event.getGuild(),false);

    }

    private double chance(Member member, Guild guild) {
        return 0.5 + 0.5 * bot.getUserPlant(member) / bot.getTotalPlant(guild);
    }

    private String clean(double chanceOfSuccess) {
        return String.format("%.1f", 100 * chanceOfSuccess) + "%";
    }

    public String getNameOutput(Guild guild) {
        File[] files = new File("data/" + bot.getName() + "/guilds/" + guild.getId() + "/members/").listFiles();
        String result = "";

        for (File file : files) {
            String fileName = file.getName().replace(".csv", "");
            Member member;
            User user;
            String name;
            int plantAmount;

            try {
                member = guild.getMemberById(fileName);
                user = bot.getJDA().getUserById(fileName);
            } catch (NumberFormatException e) {
                continue;
            }

            if (user == null) {
                continue;
            } else if (member != null) {
                name = member.getEffectiveName();
            } else {
                name = user.getName();
            }

            plantAmount = bot.getUserPlant(guild, user);

            if (plantAmount != 0) {
                result += name + ": $" + plantAmount + " (" + clean(chance(member, guild)) + ")\n";
            }
        }

        result += "*Others: 50%*";

        return result;
    }

    @Override
    public String info() {
        return ""
                + "**" + bot.getPrefix() + "harvest** - take the money that's been planted (you have a chance of losing it all!)\n"
                + "**" + bot.getPrefix() + "harvest info** - see the amount there and the odds of you removing it";
    }
}
