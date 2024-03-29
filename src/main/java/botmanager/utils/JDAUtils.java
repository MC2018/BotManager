package botmanager.utils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.requests.restaction.MessageAction;

/**
 *
 * @author MC_2018 <mc2018.git@gmail.com>
 */

public class JDAUtils {

    public static void sendGuildMessage(TextChannel channel, String message) {
        if (message.length() > Message.MAX_CONTENT_LENGTH) {
            throw new RuntimeException("Message attempted to send too long:\n" + message);
        }

        channel.sendMessage(message).queue();
    }

    public static void sendGuildMessage(TextChannel channel, MessageEmbed me) {
        channel.sendMessage(me).queue();
    }

    public static void sendGuildMessage(TextChannel channel, String message, String[] reactionNames) {
        Message sentMessage;

        if (message.length() > Message.MAX_CONTENT_LENGTH) {
            throw new RuntimeException("Message attempted to send too long:\n" + message);
        }

        sentMessage = channel.sendMessage(message).complete();

        for (String reactionName : reactionNames) {
            JDAUtils.addReaction(sentMessage, reactionName);
        }
    }

    public static void sendGuildMessage(TextChannel channel, String message, String[] reactionNames, File file) {
        Message sentMessage;

        if (message.length() > Message.MAX_CONTENT_LENGTH) {
            throw new RuntimeException("Message attempted to send too long:\n" + message);
        }

        sentMessage = channel.sendMessage(message).addFile(file).complete();

        for (String reactionName : reactionNames) {
            JDAUtils.addReaction(sentMessage, reactionName);
        }
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

    public static void sendPrivateMessage(User user, String message) {
        if (message.length() > Message.MAX_CONTENT_LENGTH) {
            throw new RuntimeException("Message attempted to send too long:\n" + message);
        }

        user.openPrivateChannel().queue((channel) -> channel.sendMessage(message).queue());
    }

    public static void sendPrivateMessage(User user, MessageEmbed message) {
        user.openPrivateChannel().queue((channel) -> {
            channel.sendMessage(message).queue();
        });
    }

    public static Message sendMessage(User user, TextChannel channel, String message, MessageEmbed messageEmbed, File[] files, String[] reactionNames, boolean returnMessage) {
        MessageChannel messageChannel;

        if (channel != null) {
            messageChannel = channel;
        } else {
            messageChannel = user.openPrivateChannel().complete();
        }

        return sendMessage(messageChannel, message, messageEmbed, files, reactionNames, returnMessage);
    }

    public static Message sendMessage(MessageChannel messageChannel, String message, MessageEmbed messageEmbed, File[] files, String[] reactionNames, boolean returnMessage) {
        MessageAction messageAction = null;
        Message sentMessage = null;

        if (message != null) {
            messageAction = messageChannel.sendMessage(message);
        }

        if (messageEmbed != null) {
            if (messageAction != null) {
                messageAction = messageAction.embed(messageEmbed);
            } else {
                messageAction = messageChannel.sendMessage(messageEmbed);
            }
        }

        if (files != null) {
            for (File file : files) {
                messageAction = messageAction.addFile(file);
            }
        }

        if (!returnMessage) {
            messageAction.queue();
        } else if (returnMessage || reactionNames != null) {
            sentMessage = messageAction.complete();
        }

        if (reactionNames != null) {
            for (String reactionName : reactionNames) {
                JDAUtils.addReaction(sentMessage, reactionName);
            }

            if (!returnMessage) {
                sentMessage = null;
            }
        }

        return sentMessage;
    }

    public static String findMemberID(Guild guild, String potentialName) {
        ArrayList<Member> names = new ArrayList<>();

        names.addAll(guild.getMembersByName(potentialName, true));
        names.addAll(guild.getMembersByEffectiveName(potentialName, true));
        names.addAll(guild.getMembersByNickname(potentialName, true));

        if (names.size() > 0) {
            return names.get(0).getUser().getId();
        }

        try {
            return guild.getJDA().getUserById(potentialName).getId();
        } catch (Exception e) {
            return null;
        }
    }

    public static Role findRole(Guild guild, String roleName) {
        List<Role> roles = guild.getRoles();

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

    public static GuildChannel findChannelByName(Guild guild, String potentialName) {
        List<GuildChannel> channels = guild.getChannels();

        for (GuildChannel channel : channels) {
            if (channel.getName().equalsIgnoreCase(potentialName)) {
                return channel;
            }
        }

        return null;
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

    public static Member findSimilarMemberWithName(Guild guild, String nameToSearch) {
        List<Member> members = guild.getMembers();
        double bestSimilarity = -1;
        int bestSimilarityIndex = 0;
        boolean hasDiscriminator = nameToSearch.matches(".*#[0-9]{1,4}$");

        for (int i = 0; i < members.size(); i++) {
            String username;
            double similarity;

            if (members.get(i).getUser().isBot()) {
                continue;
            } else if (members.get(i).getId().equals(nameToSearch)) {
                return members.get(i);
            }

            if (hasDiscriminator) {
                username = members.get(i).getUser().getAsTag();
            } else {
                username = members.get(i).getUser().getName();
            }

            similarity = Utils.similarity(username, nameToSearch) + 0.001;

            if (members.get(i).getNickname() != null) {
                double secondSimilarity = Utils.similarity(members.get(i).getNickname(), nameToSearch);
                similarity = similarity > secondSimilarity ? similarity : secondSimilarity;
            }

            if (similarity >= 1) {
                return members.get(i);
            }

            if (similarity > bestSimilarity) {
                bestSimilarity = similarity;
                bestSimilarityIndex = i;
            }
        }

        return members.get(bestSimilarityIndex);
    }

    public static ArrayList<Role> roleIDsToRoles(Guild guild, ArrayList<String> roleIDs) {
        ArrayList<Role> sortedRoles = new ArrayList<>();
        ArrayList<Role> unsortedRoles = new ArrayList<Role>(
                guild.getRoles().stream()
                        .filter(x -> roleIDs.contains(x.getId()))
                        .sorted()
                        .collect(Collectors.toList())
        );

        for (int i = 0; i < roleIDs.size(); i++) {
            for (int j = 0; j < unsortedRoles.size(); j++) {
                if (roleIDs.get(i).equals(unsortedRoles.get(j).getId())) {
                    sortedRoles.add(unsortedRoles.get(j));
                    break;
                }
            }
        }

        return sortedRoles;
    }

}
