package botmanager.bots.maidiscordbot;

import botmanager.utils.IOUtils;
import botmanager.utils.Utils;
import botmanager.generic.BotBase;
import botmanager.generic.ICommand;
import botmanager.generic.commands.PMForwarderCommand;
import botmanager.generic.commands.PMRepeaterCommand;
import botmanager.bots.maidiscordbot.commands.money.AlltimeBaltopCommand;
import botmanager.bots.maidiscordbot.commands.money.BalanceCommand;
import botmanager.bots.maidiscordbot.commands.money.BalanceTopCommand;
import botmanager.bots.maidiscordbot.commands.gambling.CoinflipCommand;
import botmanager.bots.maidiscordbot.commands.money.DailyRewardCommand;
import botmanager.bots.maidiscordbot.commands.DeadCommand;
import botmanager.bots.maidiscordbot.commands.gambling.GambleCommand;
import botmanager.bots.maidiscordbot.commands.money.GiveCommand;
import botmanager.bots.maidiscordbot.commands.gambling.HarvestCommand;
import botmanager.bots.maidiscordbot.commands.HelpCommand;
import botmanager.bots.maidiscordbot.commands.gambling.JackpotCommand;
import botmanager.bots.maidiscordbot.commands.money.MoneyCommand;
import botmanager.bots.maidiscordbot.commands.gambling.PlantCommand;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;

//idea: encryptor(s) built in?
/**
 *
 * @author MC_2018 <mc2018.git@gmail.com>
 */
public class MaiDiscordBot extends BotBase {

    private TimerTask timerTask;
    private Timer timer = new Timer();
    private HashMap<Guild, Boolean> harvesting = new HashMap<>();
    private Set<Member> planters = new HashSet<>();
    private String prefix;
    private static final int PLANT_GROWTH_MAX = 500000;

    public MaiDiscordBot(String botToken, String name) {
        super(botToken, name);
        getJDA().getPresence().setActivity(Activity.watching(" you lose money :)"));
        prefix = "~";

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
            new DeadCommand(this),
            new AlltimeBaltopCommand(this),
            new PlantCommand(this),
            new HarvestCommand(this),
            new PMRepeaterCommand(this),
            new PMForwarderCommand(this)
        });
    }

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        for (ICommand command : getCommands()) {
            command.run(event);
        }
    }

    @Override
    public void onPrivateMessageReceived(PrivateMessageReceivedEvent event) {
        for (ICommand command : getCommands()) {
            command.run(event);
        }
    }
    
    public String getPrefix() {
        return prefix;
    }
    
    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }
    
    public Set<Member> getPlanters() {
        return planters;
    }
    
    public void addPlanter(Member member) {
        planters.add(member);
    }
    
    public String getUserCSVAtIndex(Guild guild, User user, int index) {
        File file = new File("data/" + getName() + "/guilds/" + guild.getId() + "/members/" + user.getId() + ".csv");

        if (!file.exists()) {
            return "";
        }

        try {
            return Utils.getCSVValueAtIndex(IOUtils.read(file), index);
        } catch (IOException e) {
            return "";
        }
    }

    public void setUserCSVAtIndex(Guild guild, User user, int index, String newValue) {
        File file = new File("data/" + getName() + "/guilds/" + guild.getId() + "/members/" + user.getId() + ".csv");
        String data;
        String[] originalValues;
        String[] newValues;

        try {
            data = IOUtils.read(file);
        } catch (IOException e) {
            data = "";
        }

        originalValues = data.split(",");

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

        try {
            IOUtils.write(file, Utils.buildCSV(newValues));
        } catch (IOException e) {
            e.printStackTrace();
        }
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
        try {
            IOUtils.write(new File("data/" + getName() + "/guilds/" + guild.getId() + "/jackpot.csv"), jackpotCap + "," + jackpotBalance);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public File[] getGuildFolders() {
        File[] dataFiles = new File("data/guilds/" + getName()).listFiles();

        List<File> guildFolders = new ArrayList();
        File[] array;

        for (File dataFile : dataFiles) {
            if (dataFile.isDirectory()) {

                String folderName = dataFile.getName();

                try {
                    Long.parseLong(folderName);
                    guildFolders.add(dataFile);
                } catch (NumberFormatException e) {
                }
            }
        }

        array = new File[guildFolders.size()];
        guildFolders.toArray(array);
        return array;
    }

    public void generatePlantTimer() {
        ScheduledThreadPoolExecutor exec = new ScheduledThreadPoolExecutor(1);
        timerTask = new TimerTask() {

            @Override
            public void run() {
                growPlants();
            }
        };

        timer.schedule(timerTask, 180000, 180000);
        exec.schedule(new Runnable() {
            @Override
            public void run() {
                generatePlanterCache();
            }
        }, 1, TimeUnit.SECONDS);
    }
    
    private void generatePlanterCache() {
        File[] guildFolders = getGuildFolders();

        for (File guildFolder : guildFolders) {
            File[] userFiles = guildFolder.listFiles();
            String guildId = guildFolder.getName();
            Guild guild = getJDA().getGuildById(Long.parseLong(guildId));
            
            for (File userFile : userFiles) {
                String userId = IOUtils.getTrueFileName(userFile);

                try {
                    Long.parseLong(userId);
                    Member member = guild.getMemberById(userId);
                    
                    if (member != null) {
                        planters.add(member);
                    }
                } catch (NumberFormatException e) {
                }
            }
        }
    }

    public int getUserPlant(Guild guild, User user) {
        try {
            return Integer.parseInt(getUserCSVAtIndex(guild, user, 4));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

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
        try {
            IOUtils.write(new File("data/" + getName() + "/guilds/" + guild.getId() + "/plant.csv"), String.valueOf(plantBalance));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getTotalPlant(Guild guild) {
        try {
            String info = IOUtils.read(new File("data/" + getName() + "/guilds/" + guild.getId() + "/plant.csv"));
            return Integer.parseInt(Utils.getCSVValueAtIndex(info, 0));
        } catch (Exception e) {
            updatePlant(guild, 0);
            return 0;
        }
    }

    public void growPlants() {
        HashMap<Guild, Integer> totals = new HashMap<>();
		
        for (Member planter : planters) {
            int planterPlantAmount = getUserPlant(planter);
            boolean roundingChance = (planterPlantAmount % 100) > (Math.random() * 100);
            
            planterPlantAmount = (int) (planterPlantAmount * 1.01) + (roundingChance ? 1 : 0);
            
            if (isHarvesting(planter.getGuild())) {
                continue;
            } else if (planterPlantAmount > PLANT_GROWTH_MAX && getUserPlant(planter) <= PLANT_GROWTH_MAX) {
                planterPlantAmount = PLANT_GROWTH_MAX;
            }
            
            setUserPlant(planter, planterPlantAmount);
            totals.put(planter.getGuild(), totals.getOrDefault(planter, 0) + planterPlantAmount);
        }

        for (Guild guild : totals.keySet()) {
            updatePlant(guild, totals.get(guild));
        }
    }

    public void resetPlanters(Guild guild) {
        for (Member planter : planters) {
            if (planter.getGuild().equals(guild)) {
                setUserPlant(planter, 0);
            }
        }
        
        planters.removeAll(guild.getMembers());
    }

    public boolean isHarvesting(Guild guild) {
        return harvesting.getOrDefault(guild, false);
    }

    public void setHarvesting(Guild guild, boolean harvesting) {
        this.harvesting.put(guild, harvesting);
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
    
}
