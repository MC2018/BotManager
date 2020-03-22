package botmanager.boteyy_.commands;

import botmanager.generic.BotBase;
import botmanager.Utilities;
import botmanager.boteyy_.Boteyy_;
import java.io.File;
import java.util.Random;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import botmanager.boteyy_.generic.IBoteyy_Command;

/**
 *
 * @author MC_2018 <mc2018.git@gmail.com>
 */

public class JackpotCommand extends IBoteyy_Command {

    public boolean calculatingWinner = false;
    public final String[] KEYWORDS = {
        bot.getPrefix() + "jackpot",
        bot.getPrefix() + "jp",
        bot.getPrefix() + "pot"
    };
    
    public JackpotCommand(BotBase bot) {
        super(bot);
    }

    @Override
    public void run(Event genericEvent) {
        GuildMessageReceivedEvent event;
        String message;
        String result = "";
        int bet, balance, jackpotCap, jackpotBalance;
        boolean found = false;
        
        if (!(genericEvent instanceof GuildMessageReceivedEvent)) {
            return;
        }
        
        event = (GuildMessageReceivedEvent) genericEvent;
        message = event.getMessage().getContentRaw();
        balance = bot.getUserBalance(event.getMember());
        
        for (String keyword : KEYWORDS) {
            if (message.startsWith(keyword + " ")) {
                message = message.replaceFirst(keyword + " ", "");
                found = true;
                break;
            } else if (message.startsWith(keyword)) {
                message = message.replaceFirst(keyword, "");
                found = true;
                break;
            }
        }
        
        if (!found) {
            return;
        }
        
        try {
            String info = Utilities.read(new File("data/" + bot.getName() + "/" + event.getGuild().getId() + "/jackpot.csv"));
            jackpotCap = Integer.parseInt(Utilities.getCSVValueAtIndex(info, 0));
            jackpotBalance = Integer.parseInt(Utilities.getCSVValueAtIndex(info, 1));
        } catch (Exception e) {
            jackpotCap = generateJackpotCap();
            jackpotBalance = 0;
            bot.updateJackpot(event.getGuild(), jackpotCap, jackpotBalance);
        }
        
        if (message.equals("")) {
            result = ""
                    + "The jackpot cap is $" + jackpotCap + " and its current balance is $" + jackpotBalance + ".\n"
                    + event.getMember().getEffectiveName() + " has $" + bot.getUserJackpot(event.getMember()) + " in the pot.";
            
            Utilities.sendGuildMessage(event.getChannel(), result);
            return;
        }
        
        try {
            bet = Integer.parseInt(message);
        } catch (NumberFormatException e) {
            Utilities.sendGuildMessage(event.getChannel(), "'" + message + "' is not a valid number.");
            return;
        }
        
        if (balance < bet) {
            Utilities.sendGuildMessage(event.getChannel(), "You only have $" + balance + ", ntnt.");
            return;
        } else if (bet <= 0) {
            Utilities.sendGuildMessage(event.getChannel(), bet + " is too small of a number.");
            return;
        } else if (calculatingWinner) {
            result += "The winner for the jackpot is currently being determined, try again in a moment.";
            Utilities.sendGuildMessage(event.getChannel(), result);
        } else if (bet > jackpotCap - jackpotBalance) {
            result += "Your bet would have overfilled the pot, bringing the bet down from $" + bet + " to $" + (jackpotCap - jackpotBalance) + ".\n";
            bet = jackpotCap - jackpotBalance;
        } 
        
        jackpotBalance += bet;
        result += "You threw $" + bet + " into the pot, bringing the jackpot to $" + jackpotBalance + ".";        
        bot.addUserBalance(event.getMember(), -1 * bet);
        bot.addUserJackpot(event.getMember(), bet);
        
        if (jackpotCap != jackpotBalance) {
            result += "\n$" + (jackpotCap - jackpotBalance) + " more until the winner is drawn.";
        } else {
            calculatingWinner = true;
            result += "\n\n**JACKPOT**\n";
            result += calculateWinnerAndOutput(event.getGuild(), jackpotBalance);
            jackpotCap = generateJackpotCap();
            jackpotBalance = 0;
            calculatingWinner = false;
        }
        
        Utilities.sendGuildMessage(event.getChannel(), result);
        bot.updateJackpot(event.getGuild(), jackpotCap, jackpotBalance);
    }

    public int generateJackpotCap() {
        Random random = new Random();
        return (random.nextInt(15) + 5) * 100;
    }
    
    public String calculateWinnerAndOutput(Guild guild, int jackpotBalance) {
        File[] files = new File("data/" + bot.getName() + "/" + guild.getId() + "/").listFiles();
        String result = "";
        User winner = null;
        int winningNumber = (int) (Math.random() * jackpotBalance);
        int jackpotCounter = 0;
        
        for (File file : files) {
            String fileName = file.getName().replace(".csv", "");
            Member member;
            User user;
            String name;
            int jackpotBet;
            
            try {
                member = guild.getMemberById(fileName);
                user = bot.getJDA().getUserById(fileName);
            } catch (NumberFormatException e) {
                continue;
            }
            
            if (user == null) {
                continue;
            } else if (member != null) {
                name = member.getEffectiveName();
            } else {
                name = user.getName();
            }
            
            jackpotBet = bot.getUserJackpot(guild, user);
            jackpotCounter += jackpotBet;
            bot.setUserJackpot(guild, user, 0);
            
            if (winner == null && jackpotCounter > winningNumber) {
                winner = user;
                bot.addUserBalance(guild, winner, jackpotBalance);
            }
            
            if (jackpotBet != 0) {
                result += name + ": $" + jackpotBet + "\n";
            }
        }
        
        result = "Winner of $" + jackpotBalance + ": " + winner.getAsMention() + "!\n\nContestants:\n" + result;
        
        return result;
    }
    
    @Override
    public String info() {
        return ""
                + "**" + bot.getPrefix() + "jackpot** - tells you the current jackpot\n"
                + "**" + bot.getPrefix() + "jackpot AMOUNT** - puts your money into a jackpot, the more you put in the higher the chance to win";
    }

}
