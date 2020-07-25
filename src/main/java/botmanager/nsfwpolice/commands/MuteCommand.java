package botmanager.nsfwpolice.commands;

import botmanager.JDAUtils;
import botmanager.nsfwpolice.NSFWPolice;
import botmanager.nsfwpolice.generic.NSFWPoliceCommandBase;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

/**
 *
 * @author MC_2018 <mc2018.git@gmail.com>
 */

public class MuteCommand extends NSFWPoliceCommandBase {

    public MuteCommand(NSFWPolice bot) {
        super(bot);
    }

    @Override
    public void run(Event genericEvent) {
        GuildMessageReceivedEvent event;

        if (!(genericEvent instanceof GuildMessageReceivedEvent)) {
            return;
        }

        event = (GuildMessageReceivedEvent) genericEvent;
        
        if (JDAUtils.hasRole(event.getMember(), "Muted")) {
            event.getMessage().delete().queue();
        }
    }

    @Override
    public String info() {
        return null;
    }

}
