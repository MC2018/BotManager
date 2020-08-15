package botmanager.bulletbot.commands;

import botmanager.JDAUtils;
import botmanager.bulletbot.BulletBot;
import botmanager.bulletbot.generic.BulletBotCommandBase;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

/**
 *
 * @author MC_2018 <mc2018.git@gmail.com>
 */

public class WordTrackerCommand extends BulletBotCommandBase {

    String[][] characterReplacements = {
        {"1", "i"},
        {"l", "i"},
        {"3", "e"},
        {"#", "h"},
        {"4", "a"},
        {"0", "o"}
    };
    
    public WordTrackerCommand(BulletBot bot) {
        super(bot);
    }

    @Override
    public void run(Event genericEvent) {
        GuildMessageReceivedEvent event;
        EmbedBuilder eb;
        String message;
        boolean found = false;
        
        if (!(genericEvent instanceof GuildMessageReceivedEvent)) {
            return;
        }

        event = (GuildMessageReceivedEvent) genericEvent;
        message = event.getMessage().getContentRaw().toLowerCase();
        
        for (String[] characterReplacement : characterReplacements) {
            message = message.replaceAll(characterReplacement[0], characterReplacement[1]);
        }
        
        for (String dirtyWord : bot.getDirtyWords()) {
            if (message.contains(dirtyWord)) {
                found = true;
                break;
            }
        }
        
        message = event.getMessage().getContentStripped();
        
        for (String dirtyWord : bot.getDirtyWords()) {
            if (message.contains(dirtyWord)) {
                found = true;
                break;
            }
        }
        
        if (!found) {
            return;
        }
        
        eb = new EmbedBuilder();
        eb.setTitle("Dirty Word Usage");
        eb.setThumbnail(event.getAuthor().getEffectiveAvatarUrl());
        eb.addField("User and Channel", event.getAuthor().getAsMention()
                + " (" + event.getAuthor().getName() + "#" + event.getAuthor().getDiscriminator() + "), "
                + event.getChannel().getAsMention(), false);
        eb.addField("Message", event.getMessage().getContentRaw(), false);
        
        JDAUtils.sendGuildMessage(JDAUtils.findTextChannel(event.getGuild(), "toxicity-tracker"), eb.build());
    }

    
    @Override
    public String info() {
        return null;
    }

}
