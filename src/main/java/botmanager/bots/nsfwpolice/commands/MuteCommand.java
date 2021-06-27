package botmanager.bots.nsfwpolice.commands;

import botmanager.utils.JDAUtils;
import botmanager.bots.nsfwpolice.NSFWPolice;
import botmanager.bots.nsfwpolice.generic.NSFWPoliceCommandBase;
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
            try {
                event.getMessage().delete().queue();
            } catch (Exception e) {

            }
        }
    }

    @Override
    public String info() {
        return null;
    }

}
