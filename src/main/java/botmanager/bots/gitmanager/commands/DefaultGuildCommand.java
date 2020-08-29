package botmanager.bots.gitmanager.commands;

import botmanager.utils.JDAUtils;
import botmanager.bots.gitmanager.GitManager;
import botmanager.bots.gitmanager.generic.GitManagerCommandBase;
import botmanager.bots.gitmanager.objects.UserSettings;
import java.io.File;
import java.util.ArrayList;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.MessageEmbed.Field;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;

/**
 *
 * @author MC_2018 <mc2018.git@gmail.com>
 */

public class DefaultGuildCommand extends GitManagerCommandBase {

    private String[] KEYWORDS = {
        bot.getPrefix() + "default"
    };
    
    public DefaultGuildCommand(GitManager bot) {
        super(bot);
    }
    // make it so their default guild gets changed when they leave a guild, DM em
    
    @Override
    public void run(Event genericEvent) {
        UserSettings userSettings;
        User user;
        String input;
        boolean found = false;

        if (genericEvent instanceof PrivateMessageReceivedEvent) {
            PrivateMessageReceivedEvent event = (PrivateMessageReceivedEvent) genericEvent;
            user = event.getAuthor();
            input = event.getMessage().getContentRaw();
        } else if (genericEvent instanceof GuildMessageReceivedEvent) {
            GuildMessageReceivedEvent event = (GuildMessageReceivedEvent) genericEvent;
            user = event.getAuthor();
            input = event.getMessage().getContentRaw();
        } else {
            return;
        }
        
        if (user.getIdLong() == bot.getJDA().getSelfUser().getIdLong()) {
            return;
        }
        
        userSettings = getUserSettings(user);
        userSettings = verifyDefaultGuildValidity(user, userSettings);
        
        for (String keyword : KEYWORDS) {
            if (input.toLowerCase().startsWith(keyword + " ")) {
                input = input.substring(keyword.length() + 1, input.length());
                found = true;
                break;
            } else if (input.toLowerCase().replaceAll(" ", "").equals(keyword)) {
                input = input.substring(keyword.length(), input.length());
                found = true;
                break;
            }
        }
        
        if (!found) {
            return;
        }

        try {
            EmbedBuilder eb = new EmbedBuilder();
            ArrayList<Long> guildIDs = new ArrayList();
            long guildID = Long.parseLong(input);
            user.getMutualGuilds().forEach(x -> guildIDs.add(x.getIdLong()));
            
            if (guildIDs.contains(guildID) || guildID == -1) {
                userSettings.setDefaultGuildID(guildID);
                eb.setTitle("Updated Default Guild");
                eb.setDescription("Your default guild for DM commands is now '"
                        + (guildID == -1 ? "Nothing" : bot.getJDA().getGuildById(guildID).getName()) + "'.");
                bot.writeUserSettings(userSettings);
                JDAUtils.sendPrivateMessage(user, eb.build());
            } else {
                JDAUtils.sendPrivateMessage(user, getFailureEmbed());
            }
        } catch (Exception e) {
            JDAUtils.sendPrivateMessage(user, getFailureEmbed());
        }
    }
    
    public UserSettings getUserSettings(User user) {
        File file = UserSettings.getFile(bot, user.getIdLong());
        
        if (!file.exists()) {
            ArrayList<Long> guildIDs = new ArrayList();
            UserSettings userSettings;
            
            user.getMutualGuilds().forEach(x -> guildIDs.add(x.getIdLong()));
            userSettings = new UserSettings(user.getIdLong(), guildIDs.size() > 0 ? guildIDs.get(0) : -1);
            bot.writeUserSettings(userSettings);
            return userSettings;
        } else {
            return bot.readUserSettings(user.getIdLong());
        }
    }
    
    public UserSettings verifyDefaultGuildValidity(User user, UserSettings userSettings) {
        ArrayList<Long> guildIDs = new ArrayList();
        EmbedBuilder eb = new EmbedBuilder();
        user.getMutualGuilds().forEach(x -> guildIDs.add(x.getIdLong()));
        
        
        if (!guildIDs.contains(userSettings.getDefaultGuildID()) && userSettings.getDefaultGuildID() != -1) {
            eb.setTitle("Default Guild Changed");
            eb.setDescription("Either you left the guild (" + userSettings.getDefaultGuildID() + "), or this bot was removed from the guild.");
            
            if (guildIDs.size() > 0) {
                userSettings.setDefaultGuildID(guildIDs.get(0));
                eb.addField("", "Since you were in more than one guild with this bot, "
                        + "a new default has been selected (" + user.getMutualGuilds().get(0).getName() + ")", false);
                
                if (guildIDs.size() > 1) {
                    eb.addField("", "You may select another default guild with "
                            + "`" + bot.getPrefix() + "default GUILD_ID`, using your guild's ID.", false);
                }
            } else {
                userSettings.setDefaultGuildID(-1);
            }
            
            bot.writeUserSettings(userSettings);
            JDAUtils.sendPrivateMessage(user, eb.build());
        } else if (userSettings.getDefaultGuildID() == -1 && !user.getMutualGuilds().isEmpty()) {
            userSettings.setDefaultGuildID(user.getMutualGuilds().get(0).getIdLong());
            bot.writeUserSettings(userSettings);
        }
        
        return userSettings;
    }
    
    @Override
    public MessageEmbed.Field info() {
        return new Field("Changing Default Guilds for PM Commands", "```" + bot.getPrefix() + "default GUILD_ID```", false);
    }

    @Override
    public MessageEmbed getFailureEmbed() {
        EmbedBuilder eb = new EmbedBuilder();
        eb.addField(
                "Command Failed",
                "Please use proper syntax:\n"
                        + "```" + KEYWORDS[0] + " GUILD_ID```\n"
                        + "Make sure you and this bot are in the guild you are trying to set.",
                true);
        
        return eb.build();
    }

}
