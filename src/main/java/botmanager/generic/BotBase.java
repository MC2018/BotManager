package botmanager.generic;

import javax.security.auth.login.LoginException;
import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.hooks.EventListener;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;

import java.util.ArrayList;
import java.util.Collection;
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
        this(botToken, name, GatewayIntent.getIntents(GatewayIntent.ALL_INTENTS), MemberCachePolicy.ALL, new ArrayList());
    }

    public BotBase(String botToken, String name, Collection<GatewayIntent> gatewayIntents, MemberCachePolicy memberCachePolicy) {
        this(botToken, name, gatewayIntents, memberCachePolicy, new ArrayList());
    }

    public BotBase(String botToken, String name, Collection<GatewayIntent> gatewayIntents, MemberCachePolicy memberCachePolicy, Collection<EventListener> eventListeners) {
        BOT_TOKEN = botToken;
        this.name = name;
        
        try {
            JDABuilder builder = JDABuilder
                    .create(BOT_TOKEN, gatewayIntents)
                    .addEventListeners(this)
                    .setMemberCachePolicy(memberCachePolicy);

            if (eventListeners.size() > 0) {
                builder = builder.addEventListeners(eventListeners);
            }

            JDA_INSTANCE = builder.build();
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
