package botmanager;

import botmanager.bots.boteyy_.Boteyy_;
import botmanager.bots.bulletbot.BulletBot;
import botmanager.bots.gitmanager.GitManager;
import botmanager.bots.maidiscordbot.MaiDiscordBot;
import botmanager.bots.nsfwpolice.NSFWPolice;
import botmanager.bots.speedrunbot.SpeedrunBot;
import botmanager.bots.suggestionbox.SuggestionBox;
import botmanager.generic.BotBase;
import botmanager.utils.IOUtils;

import java.io.File;
import java.util.List;

public class MainCLI {

    public static void main(String[] args) {
        BotBase[] bots;

        try {
            List<String> bmTokens = IOUtils.readLines(new File("data/botmanager_tokens.txt"));
            List<String> bbTokens = IOUtils.readLines(new File("data/bbbots_tokens.txt"));


            bots = new BotBase[] {
                    new MaiDiscordBot(bmTokens.get(0), "MaiDiscordBot"),
                    new Boteyy_(bmTokens.get(1), "Boteyy_"),
                    //new SpeedrunBot(bmTokens.get(2), "Speedrun Bot"),
                    new GitManager(bmTokens.get(3), "Git Manager", "."),

                    new NSFWPolice(bbTokens.get(0), "NSFW Police"),
                    new SuggestionBox(bbTokens.get(1), "Suggestion Box"),
                    new BulletBot(bbTokens.get(2), "Bullet Bot")
            };
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
