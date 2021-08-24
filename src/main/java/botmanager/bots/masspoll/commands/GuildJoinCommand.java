package botmanager.bots.masspoll.commands;

import botmanager.bots.masspoll.MassPoll;
import botmanager.bots.masspoll.generic.MassPollCommandBase;
import botmanager.bots.masspoll.objects.GuildSettings;
import botmanager.generic.commands.IGuildJoinCommand;
import botmanager.utils.IOUtils;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;

import java.io.File;
import java.io.IOException;

public class GuildJoinCommand extends MassPollCommandBase implements IGuildJoinCommand {

    public GuildJoinCommand(MassPoll bot) {
        super(bot);
    }

    @Override
    public void runOnGuildJoin(GuildJoinEvent event) {
        GuildSettings settings = new GuildSettings(event.getGuild().getId());

        try {
            File file = GuildSettings.getFileLocation(bot, settings.guildID);

            if (!file.exists()) {
                IOUtils.writeGson(file, settings, true);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
