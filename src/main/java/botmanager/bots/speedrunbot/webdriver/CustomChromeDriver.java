package botmanager.bots.speedrunbot.webdriver;

import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

/**
 *
 * @author MC_2018 <mc2018.git@gmail.com>
 */

public class CustomChromeDriver extends ChromeDriver {

    private boolean occupied = false;
    
    public CustomChromeDriver() {
        super();
    }

    public CustomChromeDriver(ChromeOptions options) {
        super(options);
    }
    
    public String getPageSource(String url) throws Exception {
        String result;
        
        if (occupied) {
            throw new Exception("occupoto sir");
        }
        
        occupied = true;
        
        super.get(url);
        result = super.getPageSource();
        
        occupied = false;
        return result;
    }
    
    public boolean isOccupied() {
        return occupied;
    }
}
