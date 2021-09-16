package botmanager.bots.masspoll.objects;

import botmanager.bots.masspoll.MassPoll;

import java.io.File;
import java.util.ArrayList;

public class GuildSettings {

    public String guildID;
    public ArrayList<String> mentionableRoles = new ArrayList<>();
    public ArrayList<String> rolesToPoll = new ArrayList<>();
    public ArrayList<String> whitelistedUsers = new ArrayList<>();
    public ArrayList<String> blacklistedUsers = new ArrayList<>();

    public GuildSettings(String guildID) {
        this.guildID = guildID;
    }

    public static File getFileLocation(MassPoll bot, String guildID) {
        return new File("data/" + bot.getName() + "/guilds/" + guildID + "/settings.json");
    }

}
