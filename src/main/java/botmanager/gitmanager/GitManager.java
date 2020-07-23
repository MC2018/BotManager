package botmanager.gitmanager;

import botmanager.Utilities;
import botmanager.generic.BotBase;
import botmanager.generic.ICommand;
import botmanager.generic.commands.PMForwarderCommand;
import botmanager.generic.commands.PMRepeaterCommand;
import botmanager.gitmanager.commands.CreateCommand;
import botmanager.gitmanager.commands.HelpCommand;
import botmanager.gitmanager.tasks.Task;
import com.google.gson.Gson;
import java.io.File;
import java.util.List;
import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
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
            new PMForwarderCommand(this),
            new PMRepeaterCommand(this)
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
    public void onGuildJoin(GuildJoinEvent event) {
        File file = new File("data/" + getName() + "/guilds/" + event.getGuild().getId() + "/task_channels.txt");
        
        if (!file.exists()) {
            Utilities.write(file, "to-do\nin-progress\nin-pr\ncompleted");
        }
    }
    
    private List<String> getTaskChannelIDs(long guildID) {
        File file = new File("data/" + getName() + "/guilds/" + guildID + "/task_channels.txt");
        List<String> data = Utilities.readLines(file);
        
        if (data == null) {
            Utilities.write(file, "to-do\nin-progress\nin-pr\ncompleted");
            data = Utilities.readLines(file);
        }
        
        return data;
    }
    
    public String getPrefix() {
        return prefix;
    }
    
    public TextChannel getTaskChannel(long guildID, int statusType) {
        List<String> taskChannelIDs = getTaskChannelIDs(guildID);
        List<GuildChannel> guildChannels = getJDA().getGuildById(guildID).getChannels();
        
        for (GuildChannel guildChannel : guildChannels) {
            if (guildChannel instanceof TextChannel) {
                TextChannel textChannel = (TextChannel) guildChannel;
                String textChannelName = textChannel.getName();
                
                if (textChannel.getName().equalsIgnoreCase(taskChannelIDs.get(statusType))) {
                    return textChannel;
                }
            }
        }
        
        return null;
    }
    
    public static void addTaskReactions(Message message, int statusType) {
        String[] reactions = {"one", "two", "three", "four"};
        reactions[statusType] = null;
        
        for (String reaction : reactions) {
            if (reaction != null) {
                Utilities.addReaction(message, reaction);
            }
        }
    }
    
    public Task readTask(long guildID, int taskID) {
        File file = new File("data/" + getName() + "/guilds/" + guildID + "/tasks/" + taskID + ".json");
        Gson gson = new Gson();
        
        return gson.fromJson(Utilities.read(file), Task.class);
    }
    
    public void writeTask(Task task) {
        File file = new File("data/" + getName() + "/guilds/" + task.getGuildID() + "/tasks/" + task.getID() + ".json");
        Gson gson = new Gson();
        
        Utilities.write(file, gson.toJson(task, Task.class));
    }
    
}
