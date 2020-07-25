package botmanager.speedrunbot.generic;

import botmanager.generic.BotBase;
import botmanager.speedrunbot.SpeedrunBot;
import botmanager.generic.ICommand;
import com.tsunderebug.speedrun4j.game.Category;
import com.tsunderebug.speedrun4j.game.Game;
import java.io.IOException;
import java.util.ArrayList;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.MessageEmbed.Field;
import net.dv8tion.jda.internal.utils.tuple.Pair;

/**
 *
 * @author MC_2018 <mc2018.git@gmail.com>
 */

public abstract class SpeedrunBotCommandBase implements ICommand {

    protected SpeedrunBot bot;
    
    public SpeedrunBotCommandBase(BotBase bot) {
        this.bot = (SpeedrunBot) bot;
    }
    
    public abstract Field info();
    
    public MessageEmbed getWaitingEmbed() {
        EmbedBuilder eb = new EmbedBuilder();
        eb.addField("Loading", "Speedrun.com's API takes awhile to load, information will be updated as it comes in.", true);
        eb.setColor(SpeedrunBot.getEmbedColor());
        return eb.build();
    }

    public MessageEmbed getConnectionFailureEmbed() {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setThumbnail(bot.getErrorUrl());
        eb.addField("Search Failed", "Either there was a problem connecting to Speedrun.com's API, or the game/category you were searching for"
                + "isn't properly supported.", false);
        eb.setColor(SpeedrunBot.getEmbedFailureColor());
        return eb.build();
    }
    
    public MessageEmbed getFailureEmbed(String text) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setThumbnail(bot.getErrorUrl());
        eb.addField("Search Failed", text, false);
        eb.setColor(SpeedrunBot.getEmbedFailureColor());
        return eb.build();
    }
    
    public MessageEmbed getNoResultsEmbed(String gameName, String input) {
        ArrayList<Pair<String, String>> closeGames;
        String closeGamesStr = "";
        EmbedBuilder eb = new EmbedBuilder();
        eb.setThumbnail(bot.getErrorUrl());
        
        closeGames = bot.getCloseGames(gameName);

        for (Pair<String, String> pair : closeGames) {
            closeGamesStr += pair.getRight().replace(bot.getSeparator(), " (") + ")\n";
        }

        eb.addField("Search Failed", "The game '" + input.split(bot.getSeparator())[0] + "' could not be found. Here are some suggestions of games you may have wanted to search for. "
                + "If your game doesn't appear, message the bot directly for the game you want added.", false);
        eb.addField("Similarly Titled Names", closeGamesStr, false);
        eb.setColor(SpeedrunBot.getEmbedFailureColor());
        return eb.build();
    }
    
    public MessageEmbed getCategoryOverloadEmbed(Game game) {
        EmbedBuilder eb = new EmbedBuilder();
        String categoryList = "";
        
        eb.setTitle(SpeedrunBot.getName(game.getNames()), game.getWeblink());
        eb.setThumbnail(game.getAssets().getCoverLarge().getUri());
        eb.addField("Search Failed", "Your request was too broad, "
                + "please specify from these categories (using the format `"
                + bot.getPrefix() + "lb " + SpeedrunBot.getName(game.getNames())
                + bot.getSeparator() + "category_name`).", true);

        try {
            Category[] categories = game.getCategories().getCategories();

            for (int i = 0; i < categories.length; i++) {
                categoryList += categories[i].getName();

                if (i + 1 < categories.length) {
                    categoryList += ", ";
                }
            }

            eb.addField("Category List", categoryList, false);
            eb.setColor(SpeedrunBot.getEmbedFailureColor());
            return eb.build();
        } catch (IOException er) {
            er.printStackTrace();
            return null;
        }
    }
    
}
