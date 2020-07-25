package botmanager.speedrunbot.commands;

import botmanager.JDAUtils;
import botmanager.generic.BotBase;
import botmanager.speedrunbot.SpeedrunBot;
import botmanager.speedrunbot.generic.SpeedrunBotCommandBase;
import com.tsunderebug.speedrun4j.game.Category;
import com.tsunderebug.speedrun4j.game.Game;
import com.tsunderebug.speedrun4j.game.Leaderboard;
import java.io.IOException;
import java.util.ArrayList;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.MessageEmbed.Field;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

/**
 *
 * @author MC_2018 <mc2018.git@gmail.com>
 */

public class InfoCommand extends SpeedrunBotCommandBase {

    public final String[] KEYWORDS = {
        "info",
        "i"
    };
    
    public InfoCommand(BotBase bot) {
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
            return;
        }

        if (input.split(bot.getSeparator()).length == 1) {
            sentMessage.editMessage(getGeneralInfoEmbed(game, input)).queue();
        } else {
            sentMessage.editMessage(getSpecificInfoEmbed(game, input)).queue();
        }
    }
    
    @Override
    public Field info() {
        return new Field("Game Info Lookup", "```" + bot.getPrefix() + "i game_name", false);
    }

    public MessageEmbed getSyntaxFailureEmbed() {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setThumbnail(bot.getErrorUrl());
        eb.addField("Search Failed", "Make sure that you use proper formatting when writing the command.", false);
        eb.addField("Proper Syntax", "```" + bot.getPrefix() + "lb game_title\n"
                + bot.getPrefix() + "lb game_title"
                + bot.getSeparator() + "category_title```", false);
        eb.addField("Examples", "```" + bot.getPrefix() + "lb botw\n"
                + bot.getPrefix() + "lb celeste"
                + bot.getSeparator() + "any%```", false);
        eb.setColor(SpeedrunBot.getEmbedFailureColor());
        return eb.build();
    }
    
    public MessageEmbed getGeneralInfoEmbed(Game game, String input) {
        EmbedBuilder eb = new EmbedBuilder();
        ArrayList<Leaderboard> leaderboards = new ArrayList();
        Category[] categories;
        String categoryList;
        
        eb.setTitle(SpeedrunBot.getName(game.getNames()) + " Info", game.getWeblink());
        eb.setThumbnail(game.getAssets().getCoverLarge().getUri());


        leaderboards = SpeedrunBot.getTopLeaderboards(leaderboards, 3);

        for (int i = 0; i < leaderboards.size(); i++) {
            String[] runsInfo = SpeedrunBot.getRunnerStatsFromLeaderboard(leaderboards.get(i), 3);
            String runs = String.join("\n", runsInfo);
            eb.addField(leaderboards.get(i).category, "```" + runs + "```", false);
        }

        try {
            categories = game.getCategories().getCategories();
            categoryList = "";
        } catch (IOException e) {
            e.printStackTrace();
            return getConnectionFailureEmbed();
        }

        for (int i = 0; i < categories.length; i++) {
            categoryList += categories[i].getName();

            if (i + 1 < categories.length) {
                categoryList += ", ";
            }
        }

        eb.addField("Category List", categoryList, false);
        eb.setColor(SpeedrunBot.getEmbedColor());
        return eb.build();
    }
    
    public MessageEmbed getSpecificInfoEmbed(Game game, String input) {
        return null;
    }
    
}
