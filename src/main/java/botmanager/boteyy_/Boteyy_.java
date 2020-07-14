package botmanager.boteyy_;

import botmanager.generic.ICommand;
import botmanager.generic.commands.PMForwarderCommand;
import botmanager.generic.commands.PMRepeaterCommand;
import botmanager.maidiscordbot.MaiDiscordBot;
import botmanager.maidiscordbot.commands.AlltimeBaltopCommand;
import botmanager.maidiscordbot.commands.BalanceCommand;
import botmanager.maidiscordbot.commands.BalanceTopCommand;
import botmanager.maidiscordbot.commands.CoinflipCommand;
import botmanager.maidiscordbot.commands.DailyRewardCommand;
import botmanager.maidiscordbot.commands.GambleCommand;
import botmanager.maidiscordbot.commands.GiveCommand;
import botmanager.maidiscordbot.commands.HarvestCommand;
import botmanager.maidiscordbot.commands.HelpCommand;
import botmanager.maidiscordbot.commands.JackpotCommand;
import botmanager.maidiscordbot.commands.MoneyCommand;
import botmanager.maidiscordbot.commands.PlantCommand;
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
