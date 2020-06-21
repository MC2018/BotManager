package botmanager.maidiscordbot;

import botmanager.maidiscordbot.commands.*;
import botmanager.generic.BotBase;
import botmanager.Utilities;
import java.io.File;
import java.util.HashSet;
import java.util.Set;

import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import botmanager.generic.ICommand;
import botmanager.maidiscordbot.generic.MaiDiscordBotCommandBase;

//idea: encrypter(s) built in?
/**
 *
 * @author MC_2018 <mc2018.git@gmail.com>
 */
public class MaiDiscordBot extends BotBase {

    public MaiDiscordBot(String botToken, String name) {
        super(botToken, name);
        getJDA().getPresence().setActivity(Activity.watching(" you lose money :)"));

        setPrefix("~");
        setCommands(new ICommand[]{
            new MoneyCommand(this),
            new HelpCommand(this),
            new BalanceCommand(this),
            new GiveCommand(this),
            new BalanceTopCommand(this),
            new DailyRewardCommand(this),
            new GambleCommand(this),
            new CoinflipCommand(this),
            new JackpotCommand(this),
            new DeadCommand(this),
            new PMRepeaterCommand(this),
            new HarvestCommand(this),
            new PlantCommand(this),
            new AlltimeBaltopCommand(this)
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
        updateUserBaltop(guild, user, amount);
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

    public void updateJackpot(Guild guild, int jackpotCap, int jackpotBalance) {
        Utilities.write(new File("data/" + getName() + "/" + guild.getId() + "/jackpot.csv"), jackpotCap + "," + jackpotBalance);
    }

    public int getUserPlant(Guild guild, User user) {
        try {
            return Integer.parseInt(getUserCSVAtIndex(guild, user, 4));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public Set<Member> planters = new HashSet<>();

    public int getUserPlant(Member member) {
        return getUserPlant(member.getGuild(), member.getUser());
    }

    public void setUserPlant(Guild guild, User user, int amount) {
        setUserCSVAtIndex(guild, user, 4, String.valueOf(amount));
    }

    public void setUserPlant(Member member, int amount) {
        setUserPlant(member.getGuild(), member.getUser(), amount);
    }

    public void updatePlant(Guild guild, int plantBalance) {
        Utilities.write(new File("data/" + getName() + "/" + guild.getId() + "/plant.csv"), String.valueOf(plantBalance));
    }

    public int getTotalPlant(Guild guild) {

        try {
            String info = Utilities.read(new File("data/" + getName() + "/" + guild.getId() + "/plant.csv"));
            return Integer.parseInt(Utilities.getCSVValueAtIndex(info, 0));
        } catch (NumberFormatException e) {
            updatePlant(guild, 0);
            return 0;
        }

    }

    public void growPlants(Guild guild) {

        int total = 0;

        for (Member planter : planters) {
            int planterPlantAmount = (int) Math.ceil(getUserPlant(planter) * 1.01);
            setUserPlant(planter, planterPlantAmount);
            total += planterPlantAmount;
        }

        updatePlant(guild, total);

    }

    public void resetPlanters(Guild guild) {
        for (Member planter : planters) {
            if (planter.getGuild().equals(guild)) {
                setUserPlant(planter, 0);
            }
        }
        planters.removeAll(guild.getMembers());
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
    
    public int getUserBaltop(Guild guild, User user) {
        try {
            return Integer.parseInt(getUserCSVAtIndex(guild, user, 3));
        } catch (NumberFormatException e) {
            int userBalance = getUserBalance(guild, user);
            setUserBaltop(guild, user, userBalance);
            return userBalance;
        }
    }

    public int getUserBaltop(Member member) {
        return getUserBaltop(member.getGuild(), member.getUser());
    }

    private void setUserBaltop(Guild guild, User user, int amount) {
        setUserCSVAtIndex(guild, user, 3, String.valueOf(amount));
    }

    private void setUserBaltop(Member member, int amount) {
        setUserBaltop(member.getGuild(), member.getUser(), amount);
    }
    
    public void updateUserBaltop(Guild guild, User user, int amount) {
        if (getUserBaltop(guild, user) < amount) {
            setUserBaltop(guild, user, amount);
        }
    }
    
    /*public void updateUserBaltop(Member member, int amount) {
        if (getUserBaltop(member) < amount) {
            setUserBaltop(member, amount);
        }
    }*/
    
    @Override
    public MaiDiscordBotCommandBase[] getCommands() {
        ICommand[] commands = super.getCommands();
        MaiDiscordBotCommandBase[] newCommands = new MaiDiscordBotCommandBase[commands.length];
        
        for (int i = 0; i < commands.length; i++) {
            newCommands[i] = (MaiDiscordBotCommandBase) commands[i];
        }
        
        return newCommands;
    }
}
