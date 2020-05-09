package botmanager.boteyy_;

import botmanager.boteyy_.commands.BalanceTopCommand;
import botmanager.boteyy_.commands.GambleCommand;
import botmanager.boteyy_.commands.PMRepeaterCommand;
import botmanager.boteyy_.commands.BalanceCommand;
import botmanager.boteyy_.commands.MoneyCommand;
import botmanager.boteyy_.commands.DailyRewardCommand;
import botmanager.boteyy_.commands.JackpotCommand;
import botmanager.boteyy_.commands.CoinflipCommand;
import botmanager.boteyy_.commands.GiveCommand;
import botmanager.boteyy_.commands.HelpCommand;
import botmanager.generic.BotBase;
import botmanager.Utilities;
import botmanager.boteyy_.generic.Boteyy_CommandBase;
import java.io.File;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import botmanager.generic.ICommand;

//idea: encrypter(s) built in?
/**
 *
 * @author MC_2018 <mc2018.git@gmail.com>
 */
public class Boteyy_ extends BotBase {

    public Boteyy_(String botToken, String name) {
        super(botToken, name);
        getJDA().getPresence().setActivity(Activity.playing(">help for commands!"));

        setPrefix(">");
        setCommands(new ICommand[] {
            new MoneyCommand(this),
            new HelpCommand(this),
            new BalanceCommand(this),
            new GiveCommand(this),
            new BalanceTopCommand(this),
            new DailyRewardCommand(this),
            new JackpotCommand(this),
            new GambleCommand(this),
            new CoinflipCommand(this),
            new PMRepeaterCommand(this)
        });
    }

    @Override
    public void onGuildMessageReceived​(GuildMessageReceivedEvent event) {
        for (ICommand command : getCommands()) {
            command.run(event);
        }
    }
    
    @Override
    public void onPrivateMessageReceived​(PrivateMessageReceivedEvent event) {
        for (ICommand command : getCommands()) {
            command.run(event);
        }
    }
    
    public String getUserCSVAtIndex(Guild guild, User user, int index) {
        File file = new File("data/" + getName() + "/" + guild.getId() + "/" + user.getId() + ".csv");

        if (!file.exists()) {
            return "";
        }

        return Utilities.getCSVValueAtIndex(Utilities.read(file), index);
    }

    public void setUserCSVAtIndex(Guild guild, User user, int index, String newValue) {
        File file = new File("data/" + getName() + "/" + guild.getId() + "/" + user.getId() + ".csv");
        String data = Utilities.read(file);
        String[] originalValues = data.split(",");
        String[] newValues;

        if (originalValues.length > index) {
            newValues = data.split(",");
        } else {
            newValues = new String[index + 1];
            System.arraycopy(originalValues, 0, newValues, 0, originalValues.length);

            for (int i = originalValues.length; i < newValues.length; i++) {
                newValues[i] = "";
            }
        }
        
        newValues[index] = newValue;
        Utilities.write(file, Utilities.buildCSV(newValues));
    }

    public int getUserBalance(Guild guild, User user) {
        try {
            return Integer.parseInt(getUserCSVAtIndex(guild, user, 0));
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    public int getUserBalance(Member member) {
        return getUserBalance(member.getGuild(), member.getUser());
    }

    public void setUserBalance(Guild guild, User user, int amount) {
        setUserCSVAtIndex(guild, user, 0, String.valueOf(amount));
    }

    public void setUserBalance(Member member, int amount) {
        setUserBalance(member.getGuild(), member.getUser(), amount);
    }

    public void addUserBalance(Guild guild, User user, int amount) {
        setUserBalance(guild, user, getUserBalance(guild, user) + amount);
    }
    
    public void addUserBalance(Member member, int amount) {
        addUserBalance(member.getGuild(), member.getUser(), amount);
    }
    
    public int getUserJackpot(Guild guild, User user) {
        try {
            return Integer.parseInt(getUserCSVAtIndex(guild, user, 1));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public int getUserJackpot(Member member) {
        return getUserJackpot(member.getGuild(), member.getUser());
    }

    public void setUserJackpot(Guild guild, User user, int amount) {
        setUserCSVAtIndex(guild, user, 1, String.valueOf(amount));
    }
    
    public void setUserJackpot(Member member, int amount) {
        setUserJackpot(member.getGuild(), member.getUser(), amount);
    }

    public void addUserJackpot(Guild guild, User user, int amount) {
        setUserCSVAtIndex(guild, user, 1, String.valueOf(amount + getUserJackpot(guild, user)));
    }

    public void addUserJackpot(Member member, int amount) {
        addUserJackpot(member.getGuild(), member.getUser(), amount);
    }

    public void updateJackpot(Guild guild, int jackpotCap, int jackpotBalance) {
        Utilities.write(new File("data/" + getName() + "/" + guild.getId() + "/jackpot.csv"), jackpotCap + "," + jackpotBalance);
    }

    public int getUserDaily(Guild guild, User user) {
        try {
            return Integer.parseInt(getUserCSVAtIndex(guild, user, 2));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public int getUserDaily(Member member) {
        return getUserDaily(member.getGuild(), member.getUser());
    }

    public void setUserDaily(Guild guild, User user, int date) {
        setUserCSVAtIndex(guild, user, 2, String.valueOf(date));
    }
    
    public void setUserDaily(Member member, int date) {
        setUserDaily(member.getGuild(), member.getUser(), date);
    }

    @Override
    public Boteyy_CommandBase[] getCommands() {
        return (Boteyy_CommandBase[]) super.getCommands();
    }
    
}
