package botmanager.speedrunbot.commands;

import botmanager.JDAUtils;
import botmanager.generic.BotBase;
import botmanager.speedrunbot.SpeedrunBot;
import com.tsunderebug.speedrun4j.game.Category;
import com.tsunderebug.speedrun4j.game.Game;
import com.tsunderebug.speedrun4j.game.Leaderboard;
import java.io.IOException;
import java.util.ArrayList;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import botmanager.speedrunbot.generic.SpeedrunBotCommandBase;
import net.dv8tion.jda.api.entities.MessageEmbed.Field;

/**
 *
 * @author MC_2018 <mc2018.git@gmail.com>
 */
public class LeaderboardCommand extends SpeedrunBotCommandBase {

    final String[] KEYWORDS = {
        bot.getPrefix() + "leaderboard",
        bot.getPrefix() + "lb"
    };

    public LeaderboardCommand(BotBase bot) {
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
            sentMessage.editMessage(getGeneralLeaderboardEmbed(game, input)).queue();
        } else {
            sentMessage.editMessage(getSpecificLeaderboardEmbed(game, input)).queue();
        }
    }

    @Override
    public Field info() {
        return new Field("Leaderboard Lookup", "```" + bot.getPrefix() + "lb game_name\n"
                + bot.getPrefix() + "lb game_name" + bot.getSeparator() + "category_name```", false);
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

    public MessageEmbed getSpecificLeaderboardEmbed(Game game, String input) {
        EmbedBuilder eb = new EmbedBuilder();
        Category category = SpeedrunBot.getCategory(game, input.split(bot.getSeparator())[1]);
        eb.setTitle(SpeedrunBot.getName(game.getNames()) + " " + category.getName(), category.getWeblink());
        eb.setThumbnail(game.getAssets().getCoverLarge().getUri());

        try {
            Leaderboard lb = Leaderboard.forCategory(category);
            String[] runsInfo = SpeedrunBot.getRunnerStatsFromLeaderboard(lb, 10);
            String runs = String.join("\n", runsInfo);

            eb.addField("Top Leaderboard", "```" + runs + "```", false);
        } catch (IOException e) {
            e.printStackTrace();
            return getConnectionFailureEmbed();
        }

        eb.setColor(SpeedrunBot.getEmbedColor());
        return eb.build();
    }

    public MessageEmbed getGeneralLeaderboardEmbed(Game game, String input) {
        EmbedBuilder eb = new EmbedBuilder();
        ArrayList<Leaderboard> leaderboards = new ArrayList();
        Category[] categories;
        String categoryList;
        
        eb.setTitle(SpeedrunBot.getName(game.getNames()) + " (Popular Categories)", game.getWeblink());
        eb.setThumbnail(game.getAssets().getCoverLarge().getUri());

        try {
            for (Category category : game.getCategories().getCategories()) {
                leaderboards.add(Leaderboard.forCategory(category));
                leaderboards.get(leaderboards.size() - 1).category = category.getName();
            }
        } catch (IOException e) {
            return getCategoryOverloadEmbed(game);
        }

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

}
