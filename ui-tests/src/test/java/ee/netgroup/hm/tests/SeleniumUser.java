package ee.netgroup.hm.tests;

import java.util.concurrent.TimeUnit;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

public class SeleniumUser {
	
private static WebDriver driver;
	
	protected static WebDriver getDriver() {
		initDriver();
		return driver;
	}
		
    // initiates driver
    private static void initDriver() {
        if (driver == null) {
          
            System.setProperty("webdriver.chrome.driver", "C:\\chromedriver.exe");
			ChromeOptions options = new ChromeOptions();
			options.addArguments("--disable-extensions");
			options.addArguments("disable-infobars");
			options.addArguments("start-maximized");
			options.addArguments("-incognito");
			options.addArguments("-disable-cache");
			options.addArguments("--disable-popup-blocking");
			driver = new ChromeDriver(options);
			driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);


            Runtime.getRuntime().addShutdownHook(new Thread() {
                @Override
                public void run() {
                    if (driver != null) {
                        driver.quit();
                    }
                }
            });
        }
    }


}
