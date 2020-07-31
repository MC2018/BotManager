package botmanager.gitmanager;

import botmanager.JDAUtils;
import botmanager.IOUtils;
import botmanager.Utils;
import botmanager.generic.BotBase;
import botmanager.generic.ICommand;
import botmanager.generic.commands.PMForwarderCommand;
import botmanager.generic.commands.PMRepeaterCommand;
import botmanager.gitmanager.commands.AssignCommand;
import botmanager.gitmanager.commands.ChannelCleanupCommand;
import botmanager.gitmanager.commands.CreateCommand;
import botmanager.gitmanager.commands.DescriptionCommand;
import botmanager.gitmanager.commands.HelpCommand;
import botmanager.gitmanager.commands.ServerCommand;
import botmanager.gitmanager.commands.TaskMoverCommand;
import botmanager.gitmanager.commands.TitleCommand;
import botmanager.gitmanager.objects.GuildSettings;
import botmanager.gitmanager.objects.Task;
import java.io.File;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import org.eclipse.egit.github.core.PullRequest;
import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.service.IssueService;
import org.eclipse.egit.github.core.service.PullRequestService;
import org.eclipse.egit.github.core.service.RepositoryService;

/**
 *
 * @author MC_2018 <mc2018.git@gmail.com>
 */
public class GitManager extends BotBase {

    private HashMap<Long, GitHubClient> ghClients = new HashMap();
    private TimerTask prTimerTask;
    private Timer prTimer = new Timer();
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
            new ServerCommand(this),
            new PMForwarderCommand(this),
            new PMRepeaterCommand(this),
            new ChannelCleanupCommand(this)
        });
        
        initializeGitHubClients();
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
        File file = new File("data/" + getName() + "/guilds/" + event.getGuild().getId() + "/settings.json");
        
        if (!file.exists()) {
            IOUtils.writeGson(file, new GuildSettings(event.getGuild().getIdLong()), true);
        }
    }
    
    private void initializeGitHubClients() {
        try {
            Thread.sleep(1000);
        } catch (Exception e) {
        }
        
        for (Guild guild : getJDA().getGuilds()) {
            File file = new File("data/" + getName() + "/guilds/" + guild.getId() + "/settings.json");
            GitHubClient client;
            GuildSettings guildSettings;
            
            if (!file.exists()) {
                IOUtils.writeGson(file, new GuildSettings(guild.getIdLong()), true);
                continue;
            }
            
            guildSettings = IOUtils.readGson(file, GuildSettings.class);
            client = new GitHubClient();
            client.setOAuth2Token(guildSettings.getOAuthToken());
            ghClients.put(guild.getIdLong(), client);
        }
        
        prTimerTask = new TimerTask() {
            @Override
            public void run() {
                checkNewPRs();
            }
        };
        
        prTimer.schedule(prTimerTask, 3000, 60000);
    }
    
    private void checkNewPRs() {
        for (Guild guild : getJDA().getGuilds()) {
            if (!ghClients.containsKey(guild.getIdLong())) {
                continue;
            }
            
            try {
                GuildSettings gs = readGuildSettings(guild.getIdLong());
                GitHubClient client = ghClients.get(guild.getIdLong());
                
                if (Utils.isNullOrEmpty(gs.getOAuthToken())) {
                    continue;
                }
                
                RepositoryService rs = new RepositoryService(client);
                Repository repo = rs.getRepository(gs.getRepoOwnerName(), gs.getRepoName());
                List<PullRequest> prs = new PullRequestService(client).getPullRequests(repo, IssueService.STATE_OPEN);
                File file = new File("data/" + getName() + "/guilds/" + guild.getId() + "/repos/" + repo.getName() + "/old_pr_ids.json");
                ArrayList<String> oldPRs;
                boolean newPRs = false;
                
                if (file.exists()) {
                    oldPRs = IOUtils.readGson(file, ArrayList.class);
                } else {
                    oldPRs = new ArrayList();
                }
                
                for (PullRequest pr : prs) {
                    if (!oldPRs.contains(String.valueOf(pr.getId()))) {
                        if (!Utils.isNullOrEmpty(gs.getPrUpdateChannel())) {
                            sendPRUpdateMessage(gs, pr);
                        }
                        
                        oldPRs.add(String.valueOf(pr.getId()));
                        newPRs = true;
                    }
                }
                
                if (newPRs) {
                    IOUtils.writeGson(file, oldPRs);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    private void sendPRUpdateMessage(GuildSettings gs, PullRequest pr) {
        EmbedBuilder eb = new EmbedBuilder();
        Guild guild = getJDA().getGuildById(gs.getID());
        Member member = null;
        Task task;
        String prName = pr.getHead().getRef();
        int taskID = -1;
        int beginningIndex = -1;
        
        if (Utils.isNullOrEmpty(gs.getPrUpdateChannel())) {
            return;
        }
        
        for (int i = 0; i < prName.length(); i++) {
            if ('0' <= prName.charAt(i) && prName.charAt(i) <= '9' && beginningIndex == -1) {
                beginningIndex = i;
            } else if ((prName.charAt(i) < '0' ||  '9' < prName.charAt(i)) && beginningIndex != -1) {
                taskID = Integer.parseInt(prName.substring(beginningIndex, i));
                break;
            }
        }
        
        if (beginningIndex != -1 && taskID == -1) {
            taskID = Integer.parseInt(prName.substring(beginningIndex, prName.length()));
        }
        
        if (taskID != -1) {
            task = readTask(guild.getIdLong(), taskID);
            eb.setTitle("PR Uploaded: " + task.getTitle(), pr.getHtmlUrl());
            
            if (task.getAssignee() != -1) {
                member = guild.getMemberById(task.getAssignee());
            }
        } else {
            eb.setTitle("PR Uploaded: " + prName, pr.getIssueUrl());
        }
        
        if (member != null) {
            eb.addField("Author", member.getAsMention(), false);
            eb.setThumbnail(member.getUser().getEffectiveAvatarUrl());
        } else {
            eb.addField("Author", pr.getUser().getLogin() + " (GitHub)", false);
            eb.setThumbnail(pr.getUser().getAvatarUrl());
        }
        
        eb.addField(prName, pr.getTitle(), false);
        JDAUtils.sendGuildMessage(JDAUtils.findTextChannel(guild, gs.getPrUpdateChannel()), eb.build());
    }
    
    private List<String> getTaskChannelNames(long guildID) {
        GuildSettings gs = readGuildSettings(guildID);
        return gs.getTaskChannelNames();
    }
    
    public boolean isTaskChannel(TextChannel channel) {
        List<String> taskChannels = getTaskChannelNames(channel.getGuild().getIdLong());
        return taskChannels.contains(channel.getName());
    }
    
    public String getPrefix() {
        return prefix;
    }
    
    public TextChannel getTaskChannel(long guildID, int statusType) {
        return JDAUtils.findTextChannel(getJDA().getGuildById(guildID), getTaskChannelNames(guildID).get(statusType));
    }
    
    public static void addTaskReactions(Message message, GuildSettings gs, int selectedIndex) {
        ArrayList<String> taskReactionNames = gs.getTaskReactionNames();
        taskReactionNames.set(selectedIndex, null);
        
        for (String reaction : taskReactionNames) {
            if (!Utils.isNullOrEmpty(reaction)) {
                JDAUtils.addReaction(message, reaction);
            }
        }
        
        JDAUtils.addReaction(message, "red_circle");
    }
    
    public Task readTask(long guildID, int taskID) {
        File file = new File("data/" + getName() + "/guilds/" + guildID + "/tasks/" + taskID + ".json");
        return IOUtils.readGson(file, Task.class);
    }
    
    public void writeTask(Task task) {
        File file = new File("data/" + getName() + "/guilds/" + task.getGuildID() + "/tasks/" + task.getID() + ".json");
        IOUtils.writeGson(file, task);
    }
    
    public GuildSettings readGuildSettings(long guildID) {
        File file = new File("data/" + getName() + "/guilds/" + guildID + "/settings.json");
        return IOUtils.readGson(file, GuildSettings.class);
    }
    
    public void writeGuildSettings(GuildSettings guildSettings) {
        File file = new File("data/" + getName() + "/guilds/" + guildSettings.getID() + ".json");
        IOUtils.writeGson(file, guildSettings, true);
    }
    
    public MessageEmbed generateTaskEmbed(Task task) {
        GuildSettings gs = readGuildSettings(task.getGuildID());
        EmbedBuilder eb = new EmbedBuilder();
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        
        eb.setTitle("Task #" + task.getID() + ": " + task.getTitle());
        eb.addField("Description", task.getDescription(), false);
        
        if (task.getAssignee() <= 0) {
            eb.addField("Assignee", "TBD \u200B \u200B \u200B \u200B \u200B \u200B \u200B \u200B \u200B \u200B "
                    + "Click :red_circle: to be assigned/unassigned", false);
        } else {
            User user = getJDA().getUserById(task.getAssignee());
            eb.addField("Assignee", user.getAsMention(), false);
            eb.setThumbnail(user.getEffectiveAvatarUrl());
        }
        
        eb.appendDescription("Date Created " + sdf.format(new Date(task.getEpochMilli()))
                + "\nLast Updated " + sdf.format(new Date(Instant.now().toEpochMilli())));
        return eb.build();
    }
    
}
