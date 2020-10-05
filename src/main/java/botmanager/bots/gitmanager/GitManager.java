package botmanager.bots.gitmanager;

import botmanager.bots.gitmanager.commands.logs.LogCategorizationCommand;
import botmanager.bots.gitmanager.commands.logs.LogCommand;
import botmanager.utils.*;
import botmanager.generic.*;
import botmanager.generic.commands.*;
import botmanager.bots.gitmanager.commands.tasks.*;
import botmanager.bots.gitmanager.commands.*;
import botmanager.bots.gitmanager.commands.meetings.*;
import botmanager.bots.gitmanager.objects.*;
import java.io.File;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.*;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import org.eclipse.egit.github.core.*;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.service.*;

/**
 *
 * @author MC_2018 <mc2018.git@gmail.com>
 */
public class GitManager extends BotBase {

    private HashMap<Long, GitHubClient> ghClients = new HashMap();
    private HashMap<Long, GuildSettings> guildSettingsList = new HashMap();
    private HashMap<Long, UserSettings> userSettingsList = new HashMap();
    private TimerTask timerTask;
    private Timer timer = new Timer();
    final public String prefix;
    
    public GitManager(String botToken, String name, String prefix) {
        super(botToken, name);
        this.prefix = prefix;
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
            new LogCommand(this),
            new LogCategorizationCommand(this),
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
        
        initializeGuildSettings();
        initializeGitHubClients();
        startTimer();
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        for (ICommand command : getCommands()) {
            if (event.getAuthor().getIdLong() != getJDA().getSelfUser().getIdLong()) {
                command.run(event);
            }
        }
    }
    
    @Override
    public void onMessageReactionAdd(MessageReactionAddEvent event) {
        for (ICommand command : getCommands()) {
            command.run(event);
        }
    }
    
    @Override
    public void onGuildJoin(GuildJoinEvent event) {
        initializeGuildSettings(event.getGuild().getIdLong());
    }

    private void initializeGuildSettings() {
        for (Guild guild : getJDA().getGuilds()) {
            initializeGuildSettings(guild.getIdLong());
        }
    }

    private void initializeGuildSettings(long guildID) {
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
    }

    private void startTimer() {
        timerTask = new TimerTask() {
            @Override
            public void run() {
                checkMeetingTimes();
                checkNewPRs();
            }
        };

        timer.schedule(timerTask, 5000, 60000);
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
                    
                    if (Utils.formatDate(twoDaysPrior, dateFormat).equals(Utils.formatDate(currentDate, dateFormat))) {
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
                    writeGuildSettings(guildSettings);
                }
            }
        }
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
                List<PullRequest> prs = new PullRequestService(client).getPullRequests(repo, IssueService.STATE_CLOSED);
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
        long taskID = -1;
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
            
            task = getTask(guild.getIdLong(), taskID);
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
        return guildSettingsList.get(guildID).getTaskChannelNames();
    }

    public boolean isBotChannel(TextChannel channel) {
        List<String> botChannels = getTaskChannelNames(channel.getGuild().getIdLong());
        String logChannel = guildSettingsList.get(channel.getGuild().getIdLong()).getLogChannel();

        if (!Utils.isNullOrEmpty(logChannel)) {
            botChannels.add(logChannel);
        }

        return botChannels.contains(channel.getName());
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

    public Log getLog(long guildID, long logID) {
        File file = Log.getFile(this, guildID, logID);

        if (file.exists()) {
            return readLog(guildID, logID);
        } else {
            return null;
        }
    }

    private Log readLog(long guildID, long logID) {
        File file = Log.getFile(this, guildID, logID);
        Log log = IOUtils.readGson(file, Log.class);
        return log;
    }

    public void writeLog(Log log) {
        File file = Log.getFile(this, log.getGuildID(), log.getID());
        IOUtils.writeGson(file, log, true);
    }

    public static void addLogReactions(Message message, GuildSettings gs) {
        String logChannel = gs.getLogChannel();

        if (Utils.isNullOrEmpty(logChannel)) {
            return;
        }

        for (LogType logType : LogType.values()) {
            JDAUtils.addReaction(message, logType.getEmoteName());
        }
    }

    public MessageEmbed generateLogEmbed(Log log) {
        EmbedBuilder eb = new EmbedBuilder();
        User user = getJDA().getUserById(log.getAuthor());
        String description = "User: " + user.getAsMention() + "\n";

        if (log.getMinutes() >= 60) {
            description += log.getMinutes() / 60 + (log.getMinutes() / 60 == 1 ? (" Hour ") : (" Hours "));
        }

        if (log.getMinutes() % 60 != 0) {
            description += log.getMinutes() % 60 + (log.getMinutes() % 60 == 1 ? (" Minute") : (" Minutes"));
        }

        description = description.trim();
        eb.setTitle("Work Log #" + log.getID() + ": " + log.getType().getName());
        eb.setDescription(description);
        eb.setThumbnail(user.getEffectiveAvatarUrl());
        eb.setTimestamp(Instant.now());

        return eb.build();
    }

    public Task getTask(long guildID, long taskID) {
        File file = Task.getFile(this, guildID, taskID);

        if (file.exists()) {
            return readTask(guildID, taskID);
        } else {
            return null;
        }
    }

    private Task readTask(long guildID, long taskID) {
        File file = Task.getFile(this, guildID, taskID);
        return IOUtils.readGson(file, Task.class);
    }
    
    public void writeTask(Task task) {
        File file = Task.getFile(this, task.getGuildID(), task.getID());
        IOUtils.writeGson(file, task, true);
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

    private UserSettings generateUserSettings(User user) {
        File file = UserSettings.getFile(this, user.getIdLong());
        ArrayList<Long> guildIDs = new ArrayList();
        UserSettings userSettings;

        user.getMutualGuilds().forEach(x -> guildIDs.add(x.getIdLong()));
        userSettings = new UserSettings(user.getIdLong(), guildIDs.size() > 0 ? guildIDs.get(0) : -1);
        writeUserSettings(userSettings);
        return userSettings;
    }

    public UserSettings getUserSettings(long userID) {
        File file = UserSettings.getFile(this, userID);
        UserSettings userSettings;

        if (userSettingsList.containsKey(userID)) {
            userSettings =  userSettingsList.get(userID);
        } else if (file.exists()) {
            userSettings = readUserSettings(userID);
        } else {
            userSettings = generateUserSettings(getJDA().getUserById(userID));
        }

        if (!userSettingsList.containsValue(userID)) {
            userSettingsList.put(userID, userSettings);
        }

        return userSettings;
    }

    private UserSettings readUserSettings(long userID) {
        File file = UserSettings.getFile(this, userID);

        if (!file.exists()) {
            return generateUserSettings(getJDA().getUserById(userID));
        }

        return IOUtils.readGson(file, UserSettings.class);
    }
    
    public void writeUserSettings(UserSettings userSettings) {
        File file = UserSettings.getFile(this, userSettings.getID());
        IOUtils.writeGson(file, userSettings, true);
    }
    
}
