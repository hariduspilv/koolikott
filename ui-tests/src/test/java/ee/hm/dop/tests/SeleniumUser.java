package ee.hm.dop.tests;

import java.util.concurrent.TimeUnit;

import org.openqa.selenium.WebDriver;
//import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

public abstract class SeleniumUser {

	private static WebDriver driver;
	
	protected static WebDriver getDriver() {
		initDriver();
		return driver;
	}
		
    // initiates driver
    private static void initDriver() {
        if (driver == null) {

        	driver = new FirefoxDriver();
        	
    		//driver.get("https://oxygen.netgroupdigital.com/#/");
        	driver.manage().window().maximize();
        	driver.manage().deleteAllCookies();
            driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);

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

