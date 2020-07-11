package botmanager.speedrunbot.webdriver;

import java.util.ArrayList;
import org.openqa.selenium.chrome.ChromeOptions;

/**
 *
 * @author MC_2018 <mc2018.git@gmail.com>
 */
public class WebDriverManager {

    private static boolean propertiesInit = false;

    private final int SIZE;
    private volatile ArrayList<CustomChromeDriver> webDrivers = new ArrayList<>();

    //add queue eventually
    public WebDriverManager(int size) {
        SIZE = size;
        
        if (!propertiesInit) {
            System.setProperty("webdriver.chrome.driver", "C:\\selenium\\chromedriver.exe");
            System.setProperty("webdriver.chrome.silentOutput", "true");
            propertiesInit = true;
        }

        ChromeOptions options = new ChromeOptions();
        options.setHeadless(true);

        for (int i = 0; i < SIZE; i++) {
            Thread thread = new Thread() {
                @Override
                public void run() {
                    webDrivers.add(new CustomChromeDriver(options));
                }
            };
            
            thread.start();
        }
    }

    public String getPageSource(String url) {
        return getPageSource(url, 0);
    }
    
    private String getPageSource(String url, int attempts) {
        try {
            System.out.println(webDrivers.stream().filter(wd -> !wd.isOccupied()).count());
            CustomChromeDriver usableDriver = webDrivers.stream().filter(wd -> !wd.isOccupied()).findFirst().get();
            
            return usableDriver.getPageSource(url);
        } catch (Exception e) {
            e.printStackTrace();

            if (attempts < 3) {
                try {
                    Thread.sleep(1000);
                    return getPageSource(url, ++attempts);
                } catch (Exception er) {
                    er.printStackTrace();
                }
            } else {
                return null;
            }
        }

        return null;
    }

    public int getSize() {
        return SIZE;
    }

    public void close() {
        for (int i = 0; i < webDrivers.size(); i++) {
            webDrivers.get(i).quit();
        }
    }

}
