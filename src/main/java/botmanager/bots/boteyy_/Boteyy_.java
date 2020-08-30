package botmanager.bots.boteyy_;

import botmanager.generic.ICommand;
import botmanager.generic.commands.PMForwarderCommand;
import botmanager.generic.commands.PMRepeaterCommand;
import botmanager.bots.maidiscordbot.MaiDiscordBot;
import botmanager.bots.maidiscordbot.commands.money.AlltimeBaltopCommand;
import botmanager.bots.maidiscordbot.commands.money.BalanceCommand;
import botmanager.bots.maidiscordbot.commands.money.BalanceTopCommand;
import botmanager.bots.maidiscordbot.commands.gambling.CoinflipCommand;
import botmanager.bots.maidiscordbot.commands.money.DailyRewardCommand;
import botmanager.bots.maidiscordbot.commands.gambling.GambleCommand;
import botmanager.bots.maidiscordbot.commands.money.GiveCommand;
import botmanager.bots.maidiscordbot.commands.gambling.HarvestCommand;
import botmanager.bots.maidiscordbot.commands.HelpCommand;
import botmanager.bots.maidiscordbot.commands.gambling.JackpotCommand;
import botmanager.bots.maidiscordbot.commands.money.MoneyCommand;
import botmanager.bots.maidiscordbot.commands.gambling.PlantCommand;
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
