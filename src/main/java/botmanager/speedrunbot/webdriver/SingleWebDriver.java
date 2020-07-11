package botmanager.speedrunbot.webdriver;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.chrome.ChromeOptions;

/**
 *
 * @author MC_2018 <mc2018.git@gmail.com>
 */

public class SingleWebDriver {

    private static boolean propertiesInit = false;
    CustomChromeDriver webDriver;

    public SingleWebDriver(boolean headless) {
        ChromeOptions options = new ChromeOptions();
        
        if (!propertiesInit) {
            System.setProperty("webdriver.chrome.driver", "C:\\selenium\\chromedriver.exe");
            System.setProperty("webdriver.chrome.silentOutput", "true");
            propertiesInit = true;
        }

        options.setHeadless(headless);
        webDriver = new CustomChromeDriver(options);
    }
    
    public void newTab(String url) {
        ((JavascriptExecutor) webDriver).executeScript("window.open('" + url + "')");
    }
    
    public String getPageSource(String url) {
        try {
            return webDriver.getPageSource(url);
        } catch (Exception e) {
            e.printStackTrace();
                return null;
        }
    }
    
}
