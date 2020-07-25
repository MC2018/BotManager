package botmanager.speedrunbot.commands;

import botmanager.JDAUtils;
import botmanager.generic.BotBase;
import botmanager.speedrunbot.SpeedrunBot;
import net.dv8tion.jda.api.events.Event;
import botmanager.speedrunbot.generic.SpeedrunBotCommandBase;
import com.tsunderebug.speedrun4j.game.Game;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.MessageEmbed.Field;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

/**
 *
 * @author MC_2018 <mc2018.git@gmail.com>
 */

public class RulesCommand extends SpeedrunBotCommandBase {

    final String[] KEYWORDS = {
        bot.getPrefix() + "rules",
    };

    public RulesCommand(BotBase bot) {
        super(bot);
    }

    @Override
    public void run(Event genericEvent) {
        GuildMessageReceivedEvent event;
        Game game;
        Message sentMessage;
        String input;
        String gameID;

        boolean found = false;

        if (!(genericEvent instanceof GuildMessageReceivedEvent)) {
            return;
        }

        event = (GuildMessageReceivedEvent) genericEvent;
        input = event.getMessage().getContentRaw();

        for (String keyword : KEYWORDS) {
            if (input.toLowerCase().startsWith(keyword + " ")) {
                input = input.toLowerCase().replace(keyword + " ", "");
                found = true;
                break;
            } else if (input.toLowerCase().replaceAll(" ", "").equals(keyword)) {
                JDAUtils.sendGuildMessage(event.getChannel(), getSyntaxFailureEmbed());
            }
        }

        if (!found) {
            return;
        }

        sentMessage = JDAUtils.sendGuildMessageReturn(event.getChannel(), getWaitingEmbed());
        gameID = bot.determineGameID(input.split(bot.getSeparator())[0]);
        game = SpeedrunBot.getGame(gameID);

        if (game == null) {
            sentMessage.editMessage(getNoResultsEmbed(gameID, input)).queue();
        } else if (input.length() == 0) {
            sentMessage.editMessage(getSyntaxFailureEmbed()).queue();
        } else if (input.split(bot.getSeparator()).length == 1) {
            //sentMessage.editMessage(getGenericWorldRecordEmbed(game, input)).queue();
        } else {
            //sentMessage.editMessage(getSpecificWorldRecordEmbed(game, input)).queue();
        }
    }

    @Override
    public Field info() {
        return null;
    }

    public MessageEmbed getSyntaxFailureEmbed() {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setThumbnail(bot.getErrorUrl());
        eb.addField("Search Failed", "Make sure that you use proper formatting when writing the command.", false);
        eb.addField("Proper Syntax", "```" + bot.getPrefix() + "rules game_title\n"
                + bot.getPrefix() + "rules game_title"
                + bot.getSeparator() + "category_title```", false);
        eb.addField("Examples", "```" + bot.getPrefix() + "rules undertale\n"
                + bot.getPrefix() + "rules shovel knight"
                + bot.getSeparator() + "any%```", false);
        eb.setColor(SpeedrunBot.getEmbedFailureColor());
        return eb.build();
    }
    
    public MessageEmbed getGeneralRulesEmbed(Game game) {
        EmbedBuilder eb = new EmbedBuilder();
        
        eb.setTitle(SpeedrunBot.getName(game.getNames()) + " Rules", game.getWeblink());
        eb.setThumbnail(game.getAssets().getCoverLarge().getUri());
        
        //game.
        eb.setColor(SpeedrunBot.getEmbedColor());
        return eb.build();
    }

}
