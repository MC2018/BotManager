package botmanager.bots.masspoll.objects;

import botmanager.bots.masspoll.MassPoll;
import botmanager.generic.BotBase;
import botmanager.utils.IOUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.Button;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

public class Poll {

    public static final String[] NUMBER_EMOTES = {
            "\u0031\u20E3",
            "\u0032\u20E3",
            "\u0033\u20E3",
            "\u0034\u20E3",
            "\u0035\u20E3",
            "\u0036\u20E3",
            "\u0037\u20E3",
            "\u0038\u20E3",
            "\u0039\u20E3"
    };

    private final int OPTION_SIZE_BUFFER = 4;
    private long pollID;
    private String uuid;
    private Date timeStarted;
    private Date timeFirstPolled = new Date(0);
    private Date timeLastPolled = new Date(0);
    private String guildID;
    private String creatorID;
    private String testPollMessageID = "0";
    private String roleSelectorMessageID = "0";
    private int rolesToMention = 0;
    private ArrayList<String> rolesToChooseFrom;
    private ArrayList<MemberToPoll> membersToPoll;
    private ArrayList<PollUserData> pollUserData;
    private String question = "";
    private ArrayList<String> options;
    private boolean voiceRestrictedOption;

    public Poll(MassPoll bot, String creatorID, String guildID) {
        this.uuid = UUID.randomUUID().toString();
        timeStarted = new Date();
        this.pollID = generateID(bot);
        this.guildID = guildID;
        this.creatorID = creatorID;
        this.rolesToChooseFrom = new ArrayList<>();
        this.membersToPoll = new ArrayList<>();
        this.pollUserData = new ArrayList<>();
        this.options = new ArrayList<>();
    }

    public long getPollID() {
        return pollID;
    }

    public String getUUID() {
        return uuid;
    }

    public void setTimeFirstPolled(Date timeFirstPolled) {
        this.timeFirstPolled = this.timeLastPolled = timeFirstPolled;
    }

    public Date getTimeLastPolled() {
        return timeLastPolled;
    }

    public void setTimeLastPolled(Date timeLastPolled) {
        this.timeLastPolled = timeLastPolled;
    }

    public String getGuildID() {
        return guildID;
    }

    public void setGuildID(String guildID) {
        this.guildID = guildID;
    }

    public String getCreatorID() {
        return creatorID;
    }

    public String getTestPollMessageID() {
        return testPollMessageID;
    }

    public void setTestPollMessageID(String messageID) {
        testPollMessageID = messageID;
    }

    public String getRoleSelectorMessageID() {
        return roleSelectorMessageID;
    }

    public void setRoleSelectorMessageID(String messageID) {
        roleSelectorMessageID = messageID;
    }

    public ArrayList<String> getRolesToMention() {
        ArrayList<String> roles = new ArrayList<>();

        for (int i = 0; i < rolesToChooseFrom.size(); i++) {
            if ((rolesToMention & (1 << i)) > 0) {
                roles.add(rolesToChooseFrom.get(i));
            }
        }

        return roles;
    }

    public void updateRolesToMention(int index, boolean upvote) {
        if (upvote) {
            rolesToMention |= (1 << index);
        } else {
            rolesToMention &= ~(1 << index);
        }
    }

    public ArrayList<String> getRolesToChooseFrom() {
        return rolesToChooseFrom;
    }

    public void setRolesToChooseFrom(ArrayList<String> rolesToChooseFrom) {
        this.rolesToChooseFrom = rolesToChooseFrom;
    }

    public ArrayList<MemberToPoll> getMembersToPoll() {
        return  membersToPoll;
    }

    public void addMemberToPoll(Member member) {
        MemberToPoll memberToPoll = new MemberToPoll();

        if (membersToPollContains(member.getId())) {
            return;
        }

        memberToPoll.userID = member.getId();
        memberToPoll.username = member.getUser().getName();
        memberToPoll.nickname = member.getEffectiveName();
        memberToPoll.tag = member.getUser().getAsTag();

        membersToPoll.add(memberToPoll);
    }

    public void removeMemberToPoll(String id) {
        for (int i = 0; i < membersToPoll.size(); i++) {
            if (membersToPoll.get(i).userID.equals(id)) {
                membersToPoll.remove(i);
                return;
            }
        }
    }

    public boolean membersToPollContains(String id) {
        for (int i = 0; i < membersToPoll.size(); i++) {
            if (membersToPoll.get(i).userID.equals(id)) {
                return true;
            }
        }

        return false;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public void addOption(String option) throws IndexOutOfBoundsException {
        if (options.size() >= 9) {
            throw new IndexOutOfBoundsException("The maximum limit for options is 9.");
        }

        options.add(option);
    }

    public void editOption(int index, String option) throws Exception {
        if (index >= options.size()) {
            throw new IndexOutOfBoundsException("There are not " + (index + 1) + " options.");
        }

        if (getOptionsLengthWithout(index) + option.length() + OPTION_SIZE_BUFFER > MessageEmbed.VALUE_MAX_LENGTH) {
            throw new Exception("You are using too many characters amongst all of your options.");
        }

        options.remove(index);
        options.add(index, option);
    }

    public void removeOption(int index) {
        options.remove(index);
    }

    public void reorderOption(int oldIndex, int newIndex) {
        options.add(newIndex, options.remove(oldIndex));
    }

    public int getOptionsSize() {
        return options.size();
    }

    public int getOptionsLength() {
        int length = 0;

        for (String option : options) {
            length += option.length() + OPTION_SIZE_BUFFER; // to make sure it doesn't go over size
        }

        return length;
    }

    public int getOptionsLengthWithout(int index) {
        int length = getOptionsLength();

        return length - OPTION_SIZE_BUFFER - options.get(index).length();
    }

    public ArrayList<String> getOptions() {
        ArrayList<String> options = new ArrayList<>();

        for (String option : this.options) {
            options.add(option);
        }

        return options;
    }

    public void addNewUser(Member member, String messageID, boolean messageable) {
        PollUserData newUser = new PollUserData();
        User user = member.getUser();
        newUser.userID = member.getId();
        newUser.messageID = messageID;
        newUser.username = user.getName() + "#" + user.getDiscriminator();
        newUser.nickname = member.getEffectiveName();
        newUser.votes = 0;
        newUser.messageable = messageable;
        pollUserData.add(newUser);
    }

    public void updateUserVote(String userID, int index, boolean upvote) {
        for (int i = 0; i < pollUserData.size(); i++) {
            if (!pollUserData.get(i).userID.equals(userID)) {
                continue;
            }

            if (upvote) {
                pollUserData.get(i).votes |= (1 << index);
            } else {
                pollUserData.get(i).votes &= ~(1 << index);
            }

            return;
        }
    }

    public int getUserVotes(String userID) {
        for (PollUserData userData : pollUserData) {
            if (userData.userID.equals(userID)) {
                return userData.votes;
            }
        }

        return -1;
    }

    public void addUserComment(String userID, String comment) {
        for (int i = 0; i < pollUserData.size(); i++) {
            if (pollUserData.get(i).userID.equals(userID)) {
                pollUserData.get(i).comments.add(comment);
                return;
            }
        }
    }

    public void setUserMessageable(String userID, String messageID, boolean messageable) {
        for (int i = 0; i < pollUserData.size(); i++) {
            if (pollUserData.get(i).userID.equals(userID)) {
                pollUserData.get(i).messageID = messageID;
                pollUserData.get(i).messageable = messageable;
                return;
            }
        }
    }

    public ArrayList<PollUserData> getUserDataCopy() {
        ArrayList<PollUserData> freshData = new ArrayList<>();

        for (PollUserData userData : pollUserData) {
            PollUserData newUser = new PollUserData();
            newUser.userID = userData.userID;
            newUser.username = userData.username;
            newUser.nickname = userData.nickname;
            newUser.votes = userData.votes;
            newUser.comments = userData.comments;
            newUser.messageable = userData.messageable;
            freshData.add(newUser);
        }

        return pollUserData;
    }

    public boolean getVoiceRestrictedOption() {
        return voiceRestrictedOption;
    }

    public void setVoiceRestrictedOption(boolean voiceRestrictedOption) {
        this.voiceRestrictedOption = voiceRestrictedOption;
    }

    public boolean isVoiceRestricted() {
        return voiceRestrictedOption && (rolesToMention & (1 << rolesToChooseFrom.size())) > 0;
    }

    public static File getCounterFile(BotBase bot) {
        return new File("data/" + bot.getName() + "/poll_counter.json");
    }

    public static File getFileLocation(MassPoll bot, long pollID) {
        return new File("data/" + bot.getName() + "/polls/" + pollID + ".json");
    }

    private int generateID(MassPoll bot) {
        try {
            File pollCounterFile = getCounterFile(bot);
            Integer counter;

            try {
                counter = IOUtils.readGson(pollCounterFile, Integer.class);
            } catch (IOException e) {
                counter = 0;
                System.out.println("No poll counter file found; poll ID was reset to 1");

                if (getFileLocation(bot, 0).getParentFile().exists()) {
                    Path oldPath = Paths.get(getFileLocation(bot, 0).getParentFile().getAbsolutePath());
                    Path newPath = Paths.get(getFileLocation(bot, 0).getParentFile().getAbsolutePath() + " copy " + UUID.randomUUID().toString());
                    File[] files = oldPath.toFile().listFiles();

                    Files.copy(oldPath, newPath);

                    for (File file : files) {
                        file.delete();
                    }

                    Files.deleteIfExists(oldPath);
                }
            }

            IOUtils.writeGson(pollCounterFile, ++counter, true);
            return counter;
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public MessageEmbed generatePollEmbed(Guild guild) {
        return generatePollEmbed(guild, true);
    }

    private MessageEmbed generatePollEmbed(Guild guild, boolean isDefinite) {
        EmbedBuilder builder = new EmbedBuilder();
        StringBuilder optionsBuilder = new StringBuilder();

        builder.setTitle("Poll from " + guild.getName());
        builder.setThumbnail(guild.getIconUrl());

        for (int i = 0; i < options.size(); i++) {
            optionsBuilder.append(NUMBER_EMOTES[i] + " " + options.get(i) + "\n");
        }

        builder.setDescription(question);
        builder.addField("", optionsBuilder.toString(), false);
        builder.addField("", "Reply to this message to add a comment.", false);

        if (isDefinite) {
            builder.setFooter("ID " + pollID);
        }

        return builder.build();
    }

    public MessageEmbed generateRoleSelectorEmbed(ArrayList<Role> roles, boolean inVoiceChannel) {
        EmbedBuilder builder = new EmbedBuilder();
        String roleList = "";
        String memberList = "";

        builder.setTitle("Select Roles");
        builder.setDescription("Select which roles the poll should send to.");

        for (int i = 0; i < roles.size(); i++) {
            roleList += Poll.NUMBER_EMOTES[i] + " " + roles.get(i).getName() + "\n";
        }

        for (MemberToPoll memberToPoll : membersToPoll) {
            if (memberToPoll.username.equals(memberToPoll.nickname)) {
                memberList += memberToPoll.tag + "\n";
            } else {
                memberList += memberToPoll.tag + " (" + memberToPoll.nickname + ")\n";
            }
        }

        if (inVoiceChannel) {
            roleList += Poll.NUMBER_EMOTES[roles.size()] + " *Apply only to people in the voice channel*\n";
            setVoiceRestrictedOption(true);
        }

        builder.addField("Options", roleList, false);

        if (!memberList.isEmpty()) {
            builder.addField("Individual Members to Include", memberList, false);
        }

        return builder.build();
    }

    public ArrayList<ActionRow> generateActionRows(String userID, int numberOfButtons, ButtonSelectionType buttonSelection) {
        int votes = getUserVotes(userID);

        if (votes == -1) {
            return null;
        }

        return generateActionRows(votes, numberOfButtons, buttonSelection);
    }

    public ArrayList<ActionRow> generateActionRows(ButtonSelectionType buttonSelection) {
        if (buttonSelection != ButtonSelectionType.RoleSelection) {
            return null;
        }

        return generateActionRows(rolesToMention, rolesToChooseFrom.size() + (voiceRestrictedOption ? 1 : 0), buttonSelection);
    }

    public ArrayList<ActionRow> generateActionRows(int votes, int numberOfButtons, ButtonSelectionType buttonSelection) {
        ArrayList<ActionRow> rows = new ArrayList<>();
        ArrayList<Button> currentRow = new ArrayList<>();
        String typeString = buttonSelection.name();

        for (int i = 0; i < numberOfButtons; i++) {
            if (i != 0 && i % 5 == 0) {
                rows.add(ActionRow.of(currentRow));
                currentRow = new ArrayList<>();
            }

            if (((1 << i) & votes) == 0) {
                currentRow.add(Button.secondary(pollID + "_" + uuid + "_" + typeString + "_" + i, "" + (i + 1)));
            } else {
                currentRow.add(Button.primary(pollID + "_" + uuid + "_" + typeString + "_" + i, "" + (i + 1)));
            }
        }

        rows.add(ActionRow.of(currentRow));
        return rows;
    }

    public void sendTestPollMessage(Guild guild, MessageChannel channel) {
        if (testPollMessageID.equals("0")) {

            channel.sendMessage("This is what your poll looks like so far.")
                    .setEmbeds(generatePollEmbed(guild, false))
                    .queue(response -> {
                        testPollMessageID = response.getId();
                    });
        } else {
            channel.editMessageEmbedsById(testPollMessageID, generatePollEmbed(guild, false)).queue();
        }
    }

    public void sendRoleSelectorMessage(Member member, ArrayList<Role> roles, MessageChannel channel) {
        int buttonCount = roles.size();
        boolean inVoiceChannel = member.getVoiceState().inVoiceChannel();

        if (inVoiceChannel) {
            buttonCount++;
        }

        if (roleSelectorMessageID.equals("0")) {
            channel.sendMessageEmbeds(generateRoleSelectorEmbed(roles, inVoiceChannel))
                    .setActionRows(generateActionRows(0, buttonCount, ButtonSelectionType.RoleSelection))
                    .queue(response -> {
                        roleSelectorMessageID = response.getId();
                    });
        } else {
            channel.editMessageEmbedsById(roleSelectorMessageID, generateRoleSelectorEmbed(roles, inVoiceChannel)).queue();
        }
    }

    public class PollUserData {
        public String userID;
        public String messageID;
        public String username;
        public String nickname;
        public int votes;
        public ArrayList<String> comments = new ArrayList<>();
        public boolean messageable;
    }

    public class MemberToPoll {
        public String userID;
        public String username;
        public String nickname;
        public String tag;
    }

}
