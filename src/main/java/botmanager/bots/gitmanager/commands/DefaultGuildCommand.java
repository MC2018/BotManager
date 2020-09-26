package botmanager.bots.gitmanager.commands;

import botmanager.generic.commands.IMessageReceivedCommand;
import botmanager.utils.JDAUtils;
import botmanager.bots.gitmanager.GitManager;
import botmanager.bots.gitmanager.generic.GitManagerCommandBase;
import botmanager.bots.gitmanager.objects.UserSettings;
import java.util.ArrayList;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

/**
 *
 * @author MC_2018 <mc2018.git@gmail.com>
 */

public class DefaultGuildCommand extends GitManagerCommandBase implements IMessageReceivedCommand {

    private String[] KEYWORDS = {
        bot.prefix + "default"
    };
    
    public DefaultGuildCommand(GitManager bot) {
        super(bot);
    }
    // make it so their default guild gets changed when they leave a guild, DM em
    
    @Override
    public void runOnMessage(MessageReceivedEvent event) {
        UserSettings userSettings;
        User user = event.getAuthor();
        String input;
        boolean found = false;
        
        if (user.getIdLong() == bot.getJDA().getSelfUser().getIdLong()) {
            return;
        }

        input = event.getMessage().getContentRaw();
        userSettings = verifyDefaultGuildValidity(user, bot.readUserSettings(user.getIdLong()));
        
        for (String keyword : KEYWORDS) {
            if (input.toLowerCase().startsWith(keyword)) {
                input = input.substring(keyword.length() + 1).trim();
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
                            + "`" + bot.prefix + "default GUILD_ID`, using your guild's ID.", false);
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
        return new MessageEmbed.Field("Changing Default Guilds for PM Commands", "```" + bot.prefix + "default GUILD_ID```", false);
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
