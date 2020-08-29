package botmanager.bots.speedrunbot.scrape;

import com.google.common.base.MoreObjects;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

/**
 *
 * @author MC_2018 <mc2018.git@gmail.com>
 */

public class ScraperTesting {

    public static void main(String[] args) {
        System.setProperty("webdriver.chrome.driver", "C:\\selenium\\chromedriver.exe");
        ChromeOptions options = new ChromeOptions();
        options.setHeadless(true);
        
        WebDriver driver = new ChromeDriver(options);
        driver.get("http://speedrun.com/smb3/#Any");
        MoreObjects t;
        //ImmutableMap tt;
    }
    
}
