package botmanager;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.guild.GenericGuildEvent;

/**
 *
 * @author MC_2018 <mc2018.git@gmail.com>
 */

public class JDAUtils {

    public static String findUserId(Guild guild, String potentialName) {
        ArrayList<Member> names = new ArrayList<>();
        User potentialUser;

        names.addAll(guild.getMembersByName(potentialName, true));
        names.addAll(guild.getMembersByEffectiveName(potentialName, true));
        names.addAll(guild.getMembersByNickname(potentialName, true));

        if (names.size() > 0) {
            return names.get(0).getUser().getId();
        }

        try {
            potentialUser = guild.getJDA().getUserById(potentialName);

            if (potentialUser != null) {
                return potentialUser.getId();
            }
        } catch (Exception e) {

        }

        return null;
    }

    public static void sendGuildMessage(TextChannel channel, String message) {
        if (message.length() > 1950) {
            throw new RuntimeException("Message attempted to send too long:\n" + message);
        }

        channel.sendMessage(message).queue();
    }

    public static void sendGuildMessage(TextChannel channel, MessageEmbed me) {
        channel.sendMessage(me).queue();
    }

    public static Message sendGuildMessageReturn(TextChannel channel, String message) {
        if (message.length() >= Message.MAX_CONTENT_LENGTH) {
            throw new RuntimeException("Message attempted to send too long:\n" + message);
        }

        return channel.sendMessage(message).complete();
    }

    public static Message sendGuildMessageReturn(TextChannel channel, MessageEmbed me) {
        return channel.sendMessage(me).complete();
    }

    public static void sendGuildMessageWithReactions(TextChannel channel, String message, String[] reactionNames) {
        Message sentMessage;

        if (message.length() > 1950) {
            throw new RuntimeException("Message attempted to send too long:\n" + message);
        }

        sentMessage = channel.sendMessage(message).complete();

        for (String reactionName : reactionNames) {
            JDAUtils.addReaction(sentMessage, reactionName);
        }
    }

    public static void sendGuildMessageWithReactions(TextChannel channel, String message, String[] reactionNames, File file) {
        Message sentMessage;

        if (message.length() > 1950) {
            throw new RuntimeException("Message attempted to send too long:\n" + message);
        }

        sentMessage = channel.sendMessage(message).addFile(file).complete();

        for (String reactionName : reactionNames) {
            JDAUtils.addReaction(sentMessage, reactionName);
        }
    }

    public static void sendPrivateMessage(User user, String message) {
        if (message.length() > 1950) {
            throw new RuntimeException("Message attempted to send too long:\n" + message);
        }

        user.openPrivateChannel().queue((channel) -> channel.sendMessage(message).queue());
    }

    public static void sendPrivateMessage(User user, MessageEmbed message) {
        user.openPrivateChannel().queue((channel) -> channel.sendMessage(message).queue());
    }

    public static void sendPrivateMessageWithReactions(TextChannel channel, String message, String[] reactionNames) {
        Message sentMessage;

        if (message.length() > 1950) {
            throw new RuntimeException("Message attempted to send too long:\n" + message);
        }

        sentMessage = channel.sendMessage(message).complete();

        for (String reactionName : reactionNames) {
            JDAUtils.addReaction(sentMessage, reactionName);
        }
    }

    public static Role getRole(GenericGuildEvent event, String roleName) {
        List<Role> roles = event.getGuild().getRoles();

        for (Role role : roles) {
            if (role.getName().equals(roleName)) {
                return role;
            }
        }

        return null;
    }

    public static boolean hasRole(Member member, String roleName) {
        try {
            return member.getRoles().stream().anyMatch((role) -> (role.getName().equals(roleName)));
        } catch (Exception e) {
            return false;
        }
    }

    public static GuildChannel findChannelByName(Guild guild, String name) {
        List<GuildChannel> channels = guild.getChannels();

        for (GuildChannel channel : channels) {
            if (channel.getName().equalsIgnoreCase(name)) {
                return channel;
            }
        }

        return null;
    }

    public static String getFormattedUserTimeJoined(Member member, String dateFormat) {
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        Date date = Date.from(member.getTimeJoined().toInstant());
        return sdf.format(date);
    }

    public static String getFormattedUserTimeCreated(User user, String dateFormat) {
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        Date date = Date.from(user.getTimeCreated().toInstant());
        return sdf.format(date);
    }

    public static void addReaction(Message message, String potentialName) {
        List<Emote> emotes = message.getGuild().getEmotesByName(potentialName, true);
        Emote emote;

        if (!emotes.isEmpty()) {
            emote = emotes.get(0);
            message.addReaction(emote).queue();
        } else {
            message.addReaction(Utils.getEmoji(potentialName).getUnicode()).queue();
        }
    }
    
    public static TextChannel findTextChannel(Guild guild, String potentialName) {
        List<GuildChannel> guildChannels = guild.getChannels();
        
        for (GuildChannel guildChannel : guildChannels) {
            if (guildChannel instanceof TextChannel) {
                TextChannel textChannel = (TextChannel) guildChannel;
                
                if (textChannel.getName().equalsIgnoreCase(potentialName)) {
                    return textChannel;
                }
            }
        }
        
        return null;
    }
    
}
