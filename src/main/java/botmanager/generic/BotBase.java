package botmanager.generic;

import javax.security.auth.login.LoginException;
import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

/**
 *
 * @author MC_2018 <mc2018.git@gmail.com>
 */
public abstract class BotBase extends ListenerAdapter {
    
    protected final JDA JDA;
    protected final String BOT_TOKEN;
    protected String name;
    protected String prefix;
    protected ICommand[] commands;
    
    public BotBase(String botToken, String name) {
        BOT_TOKEN = botToken;
        this.name = name;
        
        try {
            JDA = new JDABuilder(AccountType.BOT)
                    .addEventListeners(this)
                    .setToken(BOT_TOKEN)
                    .build();
        } catch (LoginException e) {
            throw new RuntimeException("Error in creating JDA\n" + e.getLocalizedMessage());
        }
    }
    
    public void shutdown() {
        JDA.shutdown();
    }
    
}
