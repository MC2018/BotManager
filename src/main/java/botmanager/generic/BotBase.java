package botmanager.generic;

import javax.security.auth.login.LoginException;
import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

/**
 *
 * @author MC_2018 <mc2018.git@gmail.com>
 */
public abstract class BotBase extends ListenerAdapter {
    
    private final JDA JDA_INSTANCE;
    private final String BOT_TOKEN;
    private String name;
    private ICommand[] commands;
    
    public BotBase(String botToken, String name) {
        BOT_TOKEN = botToken;
        this.name = name;
        
        try {
            EnumSet<GatewayIntent> list = GatewayIntent.getIntents(GatewayIntent.ALL_INTENTS);
            list.remove(GatewayIntent.GUILD_PRESENCES);
            JDA_INSTANCE = JDABuilder
                    .create(BOT_TOKEN, list)
                    .addEventListeners(this)
                    .setMemberCachePolicy(MemberCachePolicy.ALL)
                    .build();
        } catch (LoginException e) {
            throw new RuntimeException("Error in creating JDA\n" + e.getLocalizedMessage());
        }
    }
    
    public void shutdown() {
        JDA_INSTANCE.shutdown();
    }
    
    public JDA getJDA() {
        return JDA_INSTANCE;
    }
    
    public String getBotToken() {
        return BOT_TOKEN;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public ICommand[] getCommands() {
        return commands;
    }
    
    public void setCommands(ICommand[] commands) {
        this.commands = commands;
    }
    
}
