package botmanager.speedrunbot.commands;

import botmanager.JDAUtils;
import botmanager.generic.BotBase;
import botmanager.Utils;
import botmanager.speedrunbot.SpeedrunBot;
import com.tsunderebug.speedrun4j.game.Category;
import com.tsunderebug.speedrun4j.game.Game;
import com.tsunderebug.speedrun4j.game.Leaderboard;
import com.tsunderebug.speedrun4j.game.run.Run;
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
public class RunCommand extends SpeedrunBotCommandBase {

    final String[] KEYWORDS = {
        bot.getPrefix() + "run",
        bot.getPrefix() + "r"
    };

    public RunCommand(BotBase bot) {
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
            getNoResultsEmbed(gameID, input);
        }

        if (input.split(bot.getSeparator()).length < 3) {
            sentMessage.editMessage(getSyntaxFailureEmbed()).queue();
        } else {
            sentMessage.editMessage(getRunEmbed(game, input)).queue();
        }
    }

    @Override
    public Field info() {
        return new Field("Run Lookup (User Name)", "```" + bot.getPrefix() + "r game_name/category_name/user_name```", false);
    }

    public MessageEmbed getSyntaxFailureEmbed() {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setThumbnail(bot.getErrorUrl());
        eb.addField("Search Failed", "Make sure that you use proper formatting when writing the command.", false);
        eb.addField("Proper Syntax", "```" + bot.getPrefix() + "p game_title"
                + bot.getSeparator() + "category_title"
                + bot.getSeparator() + "place_#```", false);
        eb.addField("Example", "```" + bot.getPrefix() + "p mario 1"
                + bot.getSeparator() + "any%"
                + bot.getSeparator() + "darbian```", false);
        eb.setColor(SpeedrunBot.getEmbedFailureColor());
        return eb.build();
    }

    public MessageEmbed getRunEmbed(Game game, String input) {
        EmbedBuilder eb = new EmbedBuilder();
        Category category = SpeedrunBot.getCategory(game, input.split(bot.getSeparator())[1]);
        eb.setThumbnail(game.getAssets().getCoverLarge().getUri());

        try {
            Leaderboard lb = Leaderboard.forCategory(category);
            ArrayList<String> names = bot.getLeaderboardNames(lb, category.getWeblink());
            Run run;
            String name = Utils.bestSimilarity(names, input.split(bot.getSeparator())[2]);
            String videoLink;
            String comment;
            int place = names.indexOf(name);

            if (place == -1) {
                return getFailureEmbed("The category you are requesting a "
                        + "run's information from (" + SpeedrunBot.getName(game.getNames()) + " "
                        + category.getName() + ") doesn't seem "
                        + "to have any registered runs.");
            }

            run = lb.getRuns()[place].getRun();
            videoLink = SpeedrunBot.getFirstVideo(run);
            comment = run.getComment();

            eb.setTitle(SpeedrunBot.getName(game.getNames()) + " " + category.getName() + " " + SpeedrunBot.getNumericalSuffix(place) + " Place Run", run.getWeblink());
            eb.addField("", "**Runner: " + SpeedrunBot.playerArrayToString(run.getPlayers()) + "**", false);
            eb.addField("Date Submitted", run.getDate(), true);
            eb.addField("Time", SpeedrunBot.formatTime(run.getTimes().getPrimary()), true);

            if (comment != null) {
                eb.addField("Comment", comment, false);
            }

            if (videoLink != null) {
                eb.addField("Video Link", videoLink, false);
            }

            eb.setColor(SpeedrunBot.getEmbedColor());
            return eb.build();
        } catch (IOException e) {
            e.printStackTrace();
            return getConnectionFailureEmbed();
        } catch (NumberFormatException e) {
            return getSyntaxFailureEmbed();
        }
    }

}
