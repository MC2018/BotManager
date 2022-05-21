package botmanager.bots.masspoll.objects;

import botmanager.bots.masspoll.MassPoll;
import net.dv8tion.jda.api.entities.Role;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class GuildSettings {

    public String guildID;
    public ArrayList<PermissionGroup> permissionGroups = new ArrayList<>();
    public ArrayList<String> whitelistedUsers = new ArrayList<>();
    public ArrayList<String> blacklistedUsers = new ArrayList<>();

    public GuildSettings(String guildID) {
        this.guildID = guildID;
    }

    public static File getFileLocation(MassPoll bot, String guildID) {
        return new File("data/" + bot.getName() + "/guilds/" + guildID + "/settings.json");
    }

    public boolean canCreatePoll(List<Role> roles) {
        return !findMentionableRoles(roles).isEmpty();
    }

    public ArrayList<String> findMentionableRoles(List<Role> roles) {
        ArrayList<String> result = new ArrayList<>();

        for (PermissionGroup permissionGroup : permissionGroups) {
            for (Role role : roles) {
                if (permissionGroup.permittedRoles.contains(role.getId())) {
                    for (String mentionableRole : permissionGroup.mentionableRoles) {
                        if (!result.contains(mentionableRole)) {
                            result.add(mentionableRole);
                        }
                    }

                    break;
                }
            }
        }

        return result;
    }

}
