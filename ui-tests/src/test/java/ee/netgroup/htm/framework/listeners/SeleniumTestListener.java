package ee.netgroup.htm.framework.listeners;

import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.logging.LogType;
import org.testng.ITestResult;
import org.testng.TestListenerAdapter;

import java.util.Date;

import static com.codeborne.selenide.WebDriverRunner.getWebDriver;

public class SeleniumTestListener extends TestListenerAdapter {
    private void logBrowserConsoleLogs() {
        System.out.println("================== BROWSER LOGS =======================");
        LogEntries logEntries = getWebDriver().manage().logs().get(LogType.BROWSER);
        for (LogEntry entry : logEntries) {
            System.out.println(new Date(entry.getTimestamp()) + " " + entry.getLevel() + " " + entry.getMessage());
        }
        System.out.println("=======================================================");
    }

    public void onTestFailure(ITestResult testResult) {
        logBrowserConsoleLogs();
    }
}
