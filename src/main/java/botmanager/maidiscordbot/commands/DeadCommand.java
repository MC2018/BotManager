package botmanager.maidiscordbot.commands;

import botmanager.JDAUtils;
import botmanager.generic.BotBase;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import botmanager.maidiscordbot.generic.MaiDiscordBotCommandBase;

/**
 *
 * @author MC_2018 <mc2018.git@gmail.com>
 */
public class DeadCommand extends MaiDiscordBotCommandBase {

    final String[] KEYWORDS = {
        bot.getPrefix() + "dead",
        bot.getPrefix() + "isthisserverdead"
    };
    
    public DeadCommand(BotBase bot) {
        super(bot);
    }

    @Override
    public void run(Event genericEvent) {
        GuildMessageReceivedEvent event;
        String message;
        boolean found = false;
        boolean dead;
        
        if (!(genericEvent instanceof GuildMessageReceivedEvent)) {
            return;
        }
        
        event = (GuildMessageReceivedEvent) genericEvent;
        message = event.getMessage().getContentRaw();
        
        for (String keyword : KEYWORDS) {
            if (message.startsWith(keyword)) {
                found = true;
                break;
            }
        }
        
        if (!found) {
            return;
        }
        
        dead = event.getGuild().getId().equals("231535629932953602");
        
        if (dead) {
            JDAUtils.sendGuildMessage(event.getChannel(), getWittyReply());
        } else {
            JDAUtils.sendGuildMessage(event.getChannel(), "What, you think this server is like Coco? Nah this one is actually alive.");
        }
    }

    @Override
    public String info() {
        return "**" + bot.getPrefix() + "dead** - checks to see if the server you're on is dead or not";
    }
    
    public String getWittyReply() {
        String[] replies = {
            "This server is dead. Get over it.",
            "Stop trying to resurrect it, it's not helping.",
            "lol this dead",
            "Find something else to suck the life out of cuz this server is dead.",
            "Nothing to see here, just a dead server.",
            "Brendan may want you to believe this is still alive.\n\nIt's not.",
            "You should join Khuma cuz this server is dead.",
            "Figure out another way to waste your life other than waste time on this dead server.",
            "The difference between Brendan and this server? Brendan's still alive.",
            "I'm tired of writing different ways of saying this server is dead. Get it through your skull."
        };
        
        return replies[(int) (Math.random() * replies.length)];
    }
    
}
