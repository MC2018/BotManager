package botmanager.gitmanager;

import botmanager.JDAUtils;
import botmanager.IOUtils;
import botmanager.generic.BotBase;
import botmanager.generic.ICommand;
import botmanager.generic.commands.PMForwarderCommand;
import botmanager.generic.commands.PMRepeaterCommand;
import botmanager.gitmanager.commands.AssignCommand;
import botmanager.gitmanager.commands.ChannelCleanupCommand;
import botmanager.gitmanager.commands.CreateCommand;
import botmanager.gitmanager.commands.DescriptionCommand;
import botmanager.gitmanager.commands.HelpCommand;
import botmanager.gitmanager.commands.TaskMoverCommand;
import botmanager.gitmanager.commands.TitleCommand;
import botmanager.gitmanager.tasks.Task;
import com.google.gson.Gson;
import java.io.File;
import java.util.List;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;

/**
 *
 * @author MC_2018 <mc2018.git@gmail.com>
 */
public class GitManager extends BotBase {

    private String prefix = ".";
    
    public GitManager(String botToken, String name) {
        super(botToken, name);
        
        this.setCommands(new ICommand[] {
            new HelpCommand(this),
            new CreateCommand(this),
            new TitleCommand(this),
            new DescriptionCommand(this),
            new TaskMoverCommand(this),
            new AssignCommand(this),
            new PMForwarderCommand(this),
            new PMRepeaterCommand(this),
            new ChannelCleanupCommand(this)
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
    
    @Override
    public void onGuildMessageReactionAdd(GuildMessageReactionAddEvent event) {
        for (ICommand command : getCommands()) {
            command.run(event);
        }
    }
    
    @Override
    public void onGuildJoin(GuildJoinEvent event) {
        File file = new File("data/" + getName() + "/guilds/" + event.getGuild().getId() + "/task_channels.txt");
        
        if (!file.exists()) {
            IOUtils.write(file, "to-do\nin-progress\nin-pr\ncompleted");
        }
    }
    
    private List<String> getTaskChannelIDs(long guildID) {
        File file = new File("data/" + getName() + "/guilds/" + guildID + "/task_channels.txt");
        List<String> data = IOUtils.readLines(file);
        
        if (data == null) {
            IOUtils.write(file, "to-do\nin-progress\nin-pr\ncompleted");
            data = IOUtils.readLines(file);
        }
        
        return data;
    }
    
    public boolean isTaskChannel(TextChannel channel) {
        File file = new File("data/" + getName() + "/guilds/" + channel.getGuild().getId() + "/task_channels.txt");
        List<String> taskChannels = getTaskChannelIDs(channel.getGuild().getIdLong());
        
        return taskChannels.contains(channel.getName());
    }
    
    public String getPrefix() {
        return prefix;
    }
    
    public TextChannel getTaskChannel(long guildID, int statusType) {
        return JDAUtils.findTextChannel(getJDA().getGuildById(guildID), getTaskChannelIDs(guildID).get(statusType));
    }
    
    public static void addTaskReactions(Message message, int statusType) {
        String[] reactions = {"todo", "inprogress", "inpr", "completed", "red_circle"};
        reactions[statusType] = null;
        
        for (String reaction : reactions) {
            if (reaction != null) {
                JDAUtils.addReaction(message, reaction);
            }
        }
    }
    
    public Task readTask(long guildID, int taskID) {
        File file = new File("data/" + getName() + "/guilds/" + guildID + "/tasks/" + taskID + ".json");
        Gson gson = new Gson();
        
        return gson.fromJson(IOUtils.read(file), Task.class);
    }
    
    public void writeTask(Task task) {
        File file = new File("data/" + getName() + "/guilds/" + task.getGuildID() + "/tasks/" + task.getID() + ".json");
        Gson gson = new Gson();
        
        IOUtils.write(file, gson.toJson(task, Task.class));
    }
    
}
