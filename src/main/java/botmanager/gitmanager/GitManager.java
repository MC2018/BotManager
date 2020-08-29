package botmanager.gitmanager;

import botmanager.utils.JDAUtils;
import botmanager.utils.IOUtils;
import botmanager.utils.Utils;
import botmanager.generic.BotBase;
import botmanager.generic.ICommand;
import botmanager.generic.commands.PMForwarderCommand;
import botmanager.generic.commands.PMRepeaterCommand;
import botmanager.gitmanager.commands.tasks.TaskAssignCommand;
import botmanager.gitmanager.commands.ChannelCleanupCommand;
import botmanager.gitmanager.commands.tasks.TaskCreateCommand;
import botmanager.gitmanager.commands.tasks.TaskDescriptionCommand;
import botmanager.gitmanager.commands.HelpCommand;
import botmanager.gitmanager.commands.tasks.TaskPurgeCommand;
import botmanager.gitmanager.commands.DefaultGuildCommand;
import botmanager.gitmanager.commands.meetings.MeetingCreateCommand;
import botmanager.gitmanager.commands.meetings.MeetingDeleteCommand;
import botmanager.gitmanager.commands.meetings.MeetingDescriptionCommand;
import botmanager.gitmanager.commands.meetings.MeetingListCommand;
import botmanager.gitmanager.commands.tasks.TaskDeleteCommand;
import botmanager.gitmanager.commands.tasks.TaskMoverCommand;
import botmanager.gitmanager.commands.tasks.TaskTitleCommand;
import botmanager.gitmanager.objects.GuildSettings;
import botmanager.gitmanager.objects.Meeting;
import botmanager.gitmanager.objects.Task;
import botmanager.gitmanager.objects.TaskBuilder;
import botmanager.gitmanager.objects.UserSettings;
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
import net.dv8tion.jda.api.entities.Activity;
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
    private HashMap<Long, GuildSettings> guildSettingsList = new HashMap();
    private TimerTask prTimerTask;
    private Timer prTimer = new Timer();
    private TimerTask meetingTimerTask;
    private Timer meetingTimer = new Timer();
    private String prefix = ".";
    
    public GitManager(String botToken, String name) {
        super(botToken, name);
        getJDA().getPresence().setActivity(Activity.watching(prefix + "help in Guild or DM!"));
        
        this.setCommands(new ICommand[] {
            new DefaultGuildCommand(this),
            new HelpCommand(this),
            new TaskCreateCommand(this),
            new TaskTitleCommand(this),
            new TaskDescriptionCommand(this),
            new TaskDeleteCommand(this),
            new TaskPurgeCommand(this),
            new MeetingCreateCommand(this),
            new MeetingListCommand(this),
            new MeetingDescriptionCommand(this),
            new MeetingDeleteCommand(this),
            new TaskMoverCommand(this),
            new TaskAssignCommand(this),
            new PMForwarderCommand(this),
            new PMRepeaterCommand(this),
            new ChannelCleanupCommand(this)
        });
        
        try {
            Thread.sleep(1000);
        } catch (Exception e) {
        }
        
        loadGuildFiles();
        initializeGitHubClients();
    }

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        for (ICommand command : getCommands()) {
            if (event.getAuthor().getIdLong() != getJDA().getSelfUser().getIdLong()) {
                command.run(event);
            }
        }
    }

    @Override
    public void onPrivateMessageReceived(PrivateMessageReceivedEvent event) {
        for (ICommand command : getCommands()) {
            if (event.getAuthor().getIdLong() != getJDA().getSelfUser().getIdLong()) {
                command.run(event);
            }
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
        verifyGuildFilesExist(event.getGuild().getIdLong());
        
    }
    
    private void loadGuildFiles() {
        for (Guild guild : getJDA().getGuilds()) {
            verifyGuildFilesExist(guild.getIdLong());
        }
        
        meetingTimerTask = new TimerTask() {
            @Override
            public void run() {
                checkMeetingTimes();
            }
        };
        
        meetingTimer.schedule(meetingTimerTask, 3000, 60000);
    }
    
    public void checkMeetingTimes() {
        for (GuildSettings guildSettings : guildSettingsList.values()) {
            if (guildSettings.getMeetings() == null) {
                continue;
            }
            
            for (Meeting meeting : guildSettings.getMeetings()) {
                Guild guild = getJDA().getGuildById(guildSettings.getID());
                Date twoDaysPrior = new Date(meeting.getDate().toInstant().minusSeconds(60 * 60 * 2 * 24).toEpochMilli());
                Date twoHoursPrior = new Date(meeting.getDate().toInstant().minusSeconds(60 * 60 * 2).toEpochMilli());
                Date currentDate = new Date();
                String dateFormat = guildSettings.getDateFormats().get(0);
                
                if (Utils.formatDate(twoDaysPrior, dateFormat).equals(Utils.formatDate(currentDate, dateFormat))
                        || Utils.formatDate(twoHoursPrior, dateFormat).equals(Utils.formatDate(currentDate, dateFormat))) {
                    EmbedBuilder eb = new EmbedBuilder();
                    
                    eb.setTitle("Reminder");
                    
                    if (Utils.formatDate(twoHoursPrior, dateFormat).equals(Utils.formatDate(currentDate, dateFormat))) {
                        eb.addField("There is a meeting two days from now!", Utils.formatDate(meeting.getDate(), dateFormat), false);
                    } else {
                        eb.addField("There is a meeting two hours from now!", Utils.formatDate(meeting.getDate(), dateFormat), false);
                    }
                    
                    if (meeting.getDescription() != null) {
                        eb.addField("Description", meeting.getDescription(), false);
                    }
                    
                    JDAUtils.sendGuildMessage(JDAUtils.findTextChannel(guild, guildSettings.getMeetingAnnouncementChannel()), "@everyone");
                    JDAUtils.sendGuildMessage(JDAUtils.findTextChannel(guild, guildSettings.getMeetingAnnouncementChannel()), eb.build());
                } else if (Utils.formatDate(new Date(), dateFormat).equals(Utils.formatDate(meeting.getDate(), dateFormat))) {
                    guildSettings.removeMeeting(meeting.getDate());
                }
            }
        }
    }

    private void verifyGuildFilesExist(long guildID) {
        File guildSettingsFile = GuildSettings.getFile(this, guildID);

        if (!guildSettingsFile.exists()) {
            writeGuildSettings(new GuildSettings(guildID));
        }
        
        if (!guildSettingsList.containsKey(guildID)) {
            GuildSettings guildSettings = readGuildSettings(guildID);
            guildSettingsList.put(guildID, guildSettings);
        }
    }
    
    private void initializeGitHubClients() {
        for (GuildSettings gs : guildSettingsList.values()) {
            File file = GuildSettings.getFile(this, gs.getID());
            GitHubClient client;
            
            if (!Utils.isNullOrEmpty(gs.getOAuthToken())) {
                client = new GitHubClient();
                client.setOAuth2Token(gs.getOAuthToken());
                ghClients.put(gs.getID(), client);
            }
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
            GuildSettings gs = readGuildSettings(guild.getIdLong());
            
            if (!ghClients.containsKey(guild.getIdLong()) || Utils.isNullOrEmpty(gs.getOAuthToken())) {
                continue;
            }
            
            try {
                GitHubClient client = ghClients.get(guild.getIdLong());
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
                        if (!Utils.isNullOrEmpty(gs.getPrAnnouncementChannel())) {
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
        String prName = pr.getHead().getRef();
        Member member = null;
        Task task;
        int taskID = -1;
        int beginningIndex = -1;
        
        if (Utils.isNullOrEmpty(gs.getPrAnnouncementChannel())) {
            return;
        }
        
        try {
            for (int i = 0; i < prName.length(); i++) {
                if ('0' <= prName.charAt(i) && prName.charAt(i) <= '9' && beginningIndex == -1) {
                    beginningIndex = i;
                } else if ((prName.charAt(i) < '0' || '9' < prName.charAt(i)) && beginningIndex != -1) {
                    taskID = Integer.parseInt(prName.substring(beginningIndex, i));
                    break;
                }
            }
            
            if (beginningIndex != -1 && taskID == -1) {
                taskID = Integer.parseInt(prName.substring(beginningIndex, prName.length()));
            }
            
            task = readTask(guild.getIdLong(), taskID);
            eb.setTitle("PR Uploaded: " + task.getTitle(), pr.getHtmlUrl());
            
            if (task.getAssignee() != -1) {
                member = guild.getMemberById(task.getAssignee());
            }
        } catch (Exception e) {
            eb.setTitle("PR Uploaded: " + prName, pr.getHtmlUrl());
        }
        
        if (member != null) {
            eb.addField("Author", member.getAsMention(), false);
            eb.setThumbnail(member.getUser().getEffectiveAvatarUrl());
        } else {
            eb.addField("Author", pr.getUser().getLogin() + " (GitHub)", false);
            eb.setThumbnail(pr.getUser().getAvatarUrl());
        }
        
        eb.addField(prName, pr.getTitle(), false);
        JDAUtils.sendGuildMessage(JDAUtils.findTextChannel(guild, gs.getPrAnnouncementChannel()), eb.build());
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
        
        if (!Utils.isNullOrEmpty(gs.getBumpReactionName())) {
            JDAUtils.addReaction(message, gs.getBumpReactionName());
        }
        
        for (String reaction : taskReactionNames) {
            if (!Utils.isNullOrEmpty(reaction)) {
                JDAUtils.addReaction(message, reaction);
            }
        }
        
        JDAUtils.addReaction(message, "red_circle");
    }
    
    public GuildSettings getGuildSettings(long guildID) {
        return guildSettingsList.get(guildID);
    }
    
    private GuildSettings readGuildSettings(long guildID) {
        File file = GuildSettings.getFile(this, guildID);
        GuildSettings guildSettings = IOUtils.readGson(file, GuildSettings.class);
        
        guildSettings.clean();
        return guildSettings;
    }
    
    public void writeGuildSettings(GuildSettings guildSettings) {
        File file = GuildSettings.getFile(this, guildSettings.getID());
        IOUtils.writeGson(file, guildSettings, true);
    }
    
    public Task readTask(long guildID, int taskID) {
        File file = Task.getFile(this, guildID, taskID);
        return IOUtils.readGson(file, Task.class);
    }
    
    public void writeTask(Task task) {
        File file = Task.getFile(this, task.getGuildID(), task.getID());
        IOUtils.writeGson(file, task, true);
    }
    
    public UserSettings readUserSettings(long userID) {
        File file = UserSettings.getFile(this, userID);
        return IOUtils.readGson(file, UserSettings.class);
    }
    
    public void writeUserSettings(UserSettings userSettings) {
        File file = UserSettings.getFile(this, userSettings.getID());
        IOUtils.writeGson(file, userSettings, true);
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

    public void purgeTasks(long guildID) {
        File taskCounterFile = TaskBuilder.getCounterFile(this, guildID);
        Guild guild = getJDA().getGuildById(guildID);
        Date date = new Date();
        Integer taskCount = IOUtils.readGson(taskCounterFile, Integer.class);
        
        if (taskCount == null) {
            return;
        }
        
        for (int i = 1; i <= taskCount; i++) {
            File taskFile = Task.getFile(this, guildID, i);
            File restoreFile = Task.getRestoreFile(this, date, guildID, i);
            Task task;
            
            if (!taskFile.exists()) {
                continue;
            }
            
            task = readTask(guildID, i);
            
            try {
                guild.getTextChannelById(task.getChannelID()).retrieveMessageById(task.getMessageID()).complete().delete().complete();
            } catch (Exception e) {
            }
            
            IOUtils.writeGson(restoreFile, task);
            taskFile.delete();
        }
        
        taskCounterFile.delete();
    }
    
}
