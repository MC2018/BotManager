package botmanager.bots.boteyy_;

import botmanager.generic.ICommand;
import botmanager.generic.commands.PMForwarderCommand;
import botmanager.generic.commands.PMRepeaterCommand;
import botmanager.bots.maidiscordbot.MaiDiscordBot;
import botmanager.bots.maidiscordbot.commands.AlltimeBaltopCommand;
import botmanager.bots.maidiscordbot.commands.BalanceCommand;
import botmanager.bots.maidiscordbot.commands.BalanceTopCommand;
import botmanager.bots.maidiscordbot.commands.CoinflipCommand;
import botmanager.bots.maidiscordbot.commands.DailyRewardCommand;
import botmanager.bots.maidiscordbot.commands.GambleCommand;
import botmanager.bots.maidiscordbot.commands.GiveCommand;
import botmanager.bots.maidiscordbot.commands.HarvestCommand;
import botmanager.bots.maidiscordbot.commands.HelpCommand;
import botmanager.bots.maidiscordbot.commands.JackpotCommand;
import botmanager.bots.maidiscordbot.commands.MoneyCommand;
import botmanager.bots.maidiscordbot.commands.PlantCommand;
import net.dv8tion.jda.api.entities.Activity;

/**
 *
 * @author MC_2018 <mc2018.git@gmail.com>
 */

public class Boteyy_ extends MaiDiscordBot {

    public Boteyy_(String botToken, String name) {
        super(botToken, name);
        setPrefix(">");
        getJDA().getPresence().setActivity(Activity.of(Activity.ActivityType.DEFAULT, getPrefix() + "help for commands!"));

        generatePlantTimer();
        setCommands(new ICommand[] {
            new MoneyCommand(this),
            new HelpCommand(this),
            new BalanceCommand(this),
            new GiveCommand(this),
            new BalanceTopCommand(this),
            new DailyRewardCommand(this),
            new GambleCommand(this),
            new CoinflipCommand(this),
            new JackpotCommand(this),
            new AlltimeBaltopCommand(this),
            new PlantCommand(this),
            new HarvestCommand(this),
            new PMRepeaterCommand(this),
            new PMForwarderCommand(this)
        });
    }

}
