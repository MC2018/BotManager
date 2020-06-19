package bbbots.jigsaw;

import bbbots.jigsaw.commands.WordTrackerCommand;
import bbbots.jigsaw.generic.JigsawCommandBase;
import botmanager.Utilities;
import botmanager.generic.BotBase;
import botmanager.generic.ICommand;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

/**
 *
 * @author MC_2018 <mc2018.git@gmail.com>
 */
public class Jigsaw extends BotBase {

    List<String> dirtyWords;
    
    public Jigsaw(String botToken, String name) {
        super(botToken, name);
        getJDA().getPresence().setActivity(Activity.playing("with your mind"));
        
        generateDirtyWords();
        
        setCommands(new JigsawCommandBase[] {
            new WordTrackerCommand(this)
        });
    }
    
    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        for (ICommand command : getCommands()) {
            command.run(event);
        }
    }
    
    public String getUserCSVAtIndex(Guild guild, User user, int index) {
        File file = new File("data/" + getName() + "/" + guild.getId() + "/" + user.getId() + ".csv");

        if (!file.exists()) {
            return "";
        }

        return Utilities.getCSVValueAtIndex(Utilities.read(file), index);
    }

    public void setUserCSVAtIndex(Guild guild, User user, int index, String newValue) {
        File file = new File("data/" + getName() + "/" + guild.getId() + "/" + user.getId() + ".csv");
        String data = Utilities.read(file);
        String[] originalValues = data.split(",");
        String[] newValues;

        if (originalValues.length > index) {
            newValues = data.split(",");
        } else {
            newValues = new String[index + 1];
            System.arraycopy(originalValues, 0, newValues, 0, originalValues.length);

            for (int i = originalValues.length; i < newValues.length; i++) {
                newValues[i] = "";
            }
        }
        
        newValues[index] = newValue;
        Utilities.write(file, Utilities.buildCSV(newValues));
    }
    
    public int getUserDirtyWords(Guild guild, User user) {
        try {
            return Integer.parseInt(getUserCSVAtIndex(guild, user, 0));
        } catch (NumberFormatException e) {
            return 0;
        }
    }
    
    public void setUserDirtyWords(Guild guild, User user, int amount) {
        setUserCSVAtIndex(guild, user, 0, String.valueOf(amount));
    }
    
    public void incrementUserDirtyWords(Guild guild, User user) {
        int dirtyWordCount = getUserDirtyWords(guild, user);
        
        if (dirtyWordCount == -1) {
            dirtyWordCount++;
        }
        
        setUserDirtyWords(guild, user, dirtyWordCount + 1);
    }
    
    public void generateDirtyWords() {
        dirtyWords = Utilities.readLines(new File("data/" + getName() + "/dirty_words.txt"));
        
        if (dirtyWords != null) {
            dirtyWords.forEach(x -> x = x.toLowerCase());
        } else {
            dirtyWords = new ArrayList();
        }
    }
    
    public List<String> getDirtyWords() {
        return dirtyWords;
    }
    
}
