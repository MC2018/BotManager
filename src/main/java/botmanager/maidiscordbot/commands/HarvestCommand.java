package botmanager.maidiscordbot.commands;

import botmanager.Utilities;
import botmanager.generic.BotBase;
import botmanager.maidiscordbot.generic.MaiDiscordBotCommandBase;
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
        boolean found = false;

        //variables here
        int userExistingPlantAmount, totalPlantAmount;
        double chanceOfSuccess;

        if (!(genericEvent instanceof GuildMessageReceivedEvent)) {
            return;
        }

        event = (GuildMessageReceivedEvent) genericEvent;
        message = event.getMessage().getContentRaw();
        chanceOfSuccess = chance(event.getMember(), event.getGuild());

        for (String keyword : KEYWORDS) {
            if (message.startsWith(keyword + " ")) {
                message = message.replaceFirst(keyword + " ", "");
                found = true;
                break;
            } else if (message.startsWith(keyword)) {
                message = message.replaceFirst(keyword, "");
                found = true;
                break;
            }
        }

        if (!found) {
            return;
        }

        userExistingPlantAmount = bot.getUserPlant(event.getMember());
        totalPlantAmount = bot.getTotalPlant(event.getGuild());

        if (message.equals("") || message.equalsIgnoreCase("info")) {
            result = "$" + totalPlantAmount + " is planted right now.\n";

            if (message.equals("")) {
                result += event.getMember().getEffectiveName() + " has $" + userExistingPlantAmount + " in the pot.";
            } else {
                result += "\n" + getNameOutput(event.getGuild());
            }

            Utilities.sendGuildMessage(event.getChannel(), result);
            return;
        }

        if (Math.random() * 2 < 1 + userExistingPlantAmount / totalPlantAmount) {
            result += event.getMember().getEffectiveName() + " harvested $" + totalPlantAmount + " at a " + clean(chanceOfSuccess) + " chance of success!";
            bot.addUserBalance(event.getMember(), totalPlantAmount);
        } else {
            result += event.getMember().getEffectiveName() + " botched a harvest of $" + totalPlantAmount + " with a " + clean(chanceOfSuccess) + " chance of success...";
        }

        Utilities.sendGuildMessage(event.getChannel(), result);

        bot.removePlanterCache();
        bot.updatePlant(event.getGuild(), 0);

    }

    private double chance(Member member, Guild guild) {
        return 0.5 + 0.5 * bot.getUserPlant(member) / bot.getTotalPlant(guild);
    }

    private String clean(double chanceOfSuccess) {
        return String.format("%.1f", 100 * chanceOfSuccess) + "%";
    }

    public String getNameOutput(Guild guild) {
        File[] files = new File("data/" + bot.getName() + "/" + guild.getId() + "/").listFiles();
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
                result += name + ": $" + plantAmount + "(" + clean(chance(member, guild)) + ")\n";
            }
        }

        return result;
    }

    @Override
    public String info() {
        return ""
                + "**" + bot.getPrefix() + "harvest** - take the money that's been planted (you have a chance of losing it all!)\n"
                + "**" + bot.getPrefix() + "harvest info** - see the amount there and the odds of you removing it";
    }
}
