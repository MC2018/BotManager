package botmanager.speedrunbot.commands;

import botmanager.JDAUtils;
import botmanager.generic.BotBase;
import botmanager.speedrunbot.SpeedrunBot;
import botmanager.speedrunbot.generic.SpeedrunBotCommandBase;
import com.tsunderebug.speedrun4j.game.Category;
import com.tsunderebug.speedrun4j.game.Game;
import com.tsunderebug.speedrun4j.game.Leaderboard;
import com.tsunderebug.speedrun4j.game.run.Run;
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
public class WorldRecordCommand extends SpeedrunBotCommandBase {

    final String[] KEYWORDS = {
        bot.getPrefix() + "worldrecord",
        bot.getPrefix() + "record",
        bot.getPrefix() + "wr"
    };

    public WorldRecordCommand(BotBase bot) {
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
            sentMessage.editMessage(getGenericWorldRecordEmbed(game, input)).queue();
        } else {
            sentMessage.editMessage(getSpecificWorldRecordEmbed(game, input)).queue();
        }
    }

    @Override
    public Field info() {
        //return "**" + bot.getPrefix() + "worldrecord  game\\_name**"
        //        + " - lists info on world records from the game's 3 most popular categories (shortcut is **" + bot.getPrefix() + "wr**)\n"
        //        + "**" + bot.getPrefix() + "worldrecord  game\\_name" + bot.getSeparator() + "category\\_name**"
        //        + " - informs you of specific info on any category's world record (shortcut is **" + bot.getPrefix() + "wr**)";
        return new Field("World Record Lookup", "```" + bot.getPrefix() + "wr game_name\n"
                + bot.getPrefix() + "wr game_name/category_name```", false);
    }

    public MessageEmbed getSyntaxFailureEmbed() {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setThumbnail(bot.getErrorUrl());
        eb.addField("Search Failed", "Make sure that you use proper formatting when writing the command.", false);
        eb.addField("Proper Syntax", "```" + bot.getPrefix() + "wr game_title\n"
                + "" + bot.getPrefix() + "wr game_title"
                + bot.getSeparator() + "category_title```", false);
        eb.addField("Example", "```" + bot.getPrefix() + "wr portal\n"
                + bot.getPrefix() + "wr ocarina of time"
                + bot.getSeparator() + "glitchless```", false);
        return eb.build();
    }

    public MessageEmbed getGenericWorldRecordEmbed(Game game, String input) {
        EmbedBuilder eb = new EmbedBuilder();
        ArrayList<Leaderboard> leaderboards = new ArrayList();
        eb.setTitle(SpeedrunBot.getName(game.getNames()) + " World Records (Popular Categories)", game.getWeblink());
        eb.setThumbnail(game.getAssets().getCoverLarge().getUri());

        try {
            for (Category category : game.getCategories().getCategories()) {
                leaderboards.add(Leaderboard.forCategory(category));
                leaderboards.get(leaderboards.size() - 1).category = category.getName();
            }
        } catch (Exception e) {
            return getCategoryOverloadEmbed(game);
        }

        leaderboards = SpeedrunBot.getTopLeaderboards(leaderboards, 3);

        if (leaderboards.isEmpty()) {
            return getFailureEmbed("The leaderboard for '" + SpeedrunBot.getName(game.getNames()) + "' has no runs submitted.");
        }

        for (int i = 0; i < leaderboards.size(); i++) {
            Run run = leaderboards.get(i).getRuns()[0].getRun();
            String names = SpeedrunBot.playerArrayToString(run.getPlayers());
            String time = SpeedrunBot.formatTime(run.getTimes().getPrimary());
            String video = SpeedrunBot.getFirstVideo(run);
            String result = names + " (" + time + ")";

            if (video != null) {
                result += "\n" + video;
            }

            eb.addField(leaderboards.get(i).category, result, false);
        }

        Category[] categories;
        String categoryList;
        
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
        
        return eb.build();
    }

    public MessageEmbed getSpecificWorldRecordEmbed(Game game, String input) {
        EmbedBuilder eb = new EmbedBuilder();
        Category category = SpeedrunBot.getCategory(game, input.split(bot.getSeparator())[1]);
        Leaderboard lb;
        Run run;
        String videoLink;
        String comment;
        int place = 1;

        try {
            lb = Leaderboard.forCategory(category);
        } catch (IOException e) {
            return getConnectionFailureEmbed();
        }
        
        eb.setThumbnail(game.getAssets().getCoverLarge().getUri());
        
        if (lb.getRuns().length < place) {
            return getFailureEmbed("The category you are requesting a run's information from ("
                    + SpeedrunBot.getName(game.getNames()) + " " + category.getName() + ") "
                    + (lb.getRuns().length == 0 ? "" : "only") + " has "
                    + lb.getRuns().length + " run" + (lb.getRuns().length == 1 ? "" : "s") + ".");
        }

        run = lb.getRuns()[place - 1].getRun();
        videoLink = SpeedrunBot.getFirstVideo(run);
        comment = run.getComment();
        eb.setTitle(SpeedrunBot.getName(game.getNames()) + " " + category.getName() + " " + SpeedrunBot.getNumericalSuffix(place - 1) + " Place Run", run.getWeblink());
        eb.addField("", "**Runner: " + SpeedrunBot.playerArrayToString(run.getPlayers()) + "**", false);
        eb.addField("Date Submitted", run.getDate(), true);
        eb.addField("Time", SpeedrunBot.formatTime(run.getTimes().getPrimary()), true);

        if (comment != null) {
            eb.addField("Comment", comment, false);
        }

        if (videoLink != null) {
            eb.addField("Video Link", videoLink, false);
        }

        return eb.build();
    }

}
