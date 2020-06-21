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
import java.util.Timer;
import java.util.TimerTask;

public class PlantCommand extends MaiDiscordBotCommandBase {

    public final String[] KEYWORDS = {
            bot.getPrefix() + "plant",
            bot.getPrefix() + "pl",
            bot.getPrefix() + "p"
    };
    private final TimerTask task;
    private final Timer timer;

    public PlantCommand(BotBase bot) {
        super(bot);

        task = new TimerTask() {
            @Override
            public void run() {
                growPlants();
            }
        };

        timer = new Timer();
        timer.schedule(task, 60000, 60000);
    }

    private void growPlants() {
        bot.growPlants();
    }

    @Override
    public void run(Event genericEvent) {
        GuildMessageReceivedEvent event;
        String message;
        String result = "";
        boolean found = false;

        //variables here
        int balance, userExistingPlantAmount, userNewPlantAmount, totalPlantAmount;

        if (!(genericEvent instanceof GuildMessageReceivedEvent)) {
            return;
        }

        event = (GuildMessageReceivedEvent) genericEvent;
        message = event.getMessage().getContentRaw();
        balance = bot.getUserBalance(event.getMember());

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

        try {
            String info = Utilities.read(new File("data/" + bot.getName() + "/" + event.getGuild().getId() + "/plant.csv"));
            totalPlantAmount = Integer.parseInt(Utilities.getCSVValueAtIndex(info, 0));
        } catch (NumberFormatException e) {
            totalPlantAmount = 0;
            bot.updatePlant(event.getGuild(), totalPlantAmount);
        }

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

        try {
            userNewPlantAmount = Integer.parseInt(message);
        } catch (NumberFormatException e) {
            Utilities.sendGuildMessage(event.getChannel(), "'" + message + "' is not a valid number.");
            return;
        }

        if (balance < userNewPlantAmount) {
            Utilities.sendGuildMessage(event.getChannel(), "You only have $" + balance + ", ntnt.");
            return;
        } else if (userNewPlantAmount <= 0) {
            Utilities.sendGuildMessage(event.getChannel(), userNewPlantAmount + " is too small of a number.");
            return;
        }

        totalPlantAmount += userNewPlantAmount;
        result += "You planted $" + userNewPlantAmount + ", making the harvest worth $" + totalPlantAmount + ".";
        bot.addUserBalance(event.getMember(), -userNewPlantAmount);
        bot.setUserPlant(event.getMember(), userNewPlantAmount + userExistingPlantAmount);

        Utilities.sendGuildMessage(event.getChannel(), result);
        bot.updatePlant(event.getGuild(), totalPlantAmount);
        bot.planters.add(event.getMember());
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
                result += name + ": $" + plantAmount + "\n";
            }
        }

        return result;
    }

    @Override
    public String info() {
        return ""
                + "**" + bot.getPrefix() + "plant** - shows how much money has been planted, and by who\n"
                + "**" + bot.getPrefix() + "plant AMOUNT** - puts your money into a plant, which will grow over time and can be taken by anyone";
    }
}
