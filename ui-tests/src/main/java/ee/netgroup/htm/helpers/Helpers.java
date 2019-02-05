package ee.netgroup.htm.helpers;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ex.UIAssertionError;
import org.apache.commons.lang3.RandomStringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.util.Random;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;

public class Helpers {
    private static WebDriverWait wait = new WebDriverWait(getWebDriver(), 10);
    private static Logger logger = LoggerFactory.getLogger(Helpers.class);

    @Deprecated
    public static void waitForMilliseconds(long milliSeconds) {
        try {
            Thread.sleep(milliSeconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static WebElement waitForVisibility(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    public static boolean isWebElementVisible(By locator) {
        try {
            return $(locator).shouldBe(Condition.visible) != null;
        } catch (UIAssertionError e) {
            logger.error("Element not found by locator: " + locator);
            return false;
        }
    }

    public static WebElement waitForClickable(By locator) {
        return wait.until(ExpectedConditions.elementToBeClickable(locator));
    }

    public static String randomElement(String[] array) {
        return array[new Random().nextInt(array.length)];
    }

    //TODO: Read file from resources instead
    public static void uploadFile() {

        try {
            Helpers.waitForMilliseconds(1000);
            StringSelection s = new StringSelection("c:\\files\\cateyes.jpg");
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(s, null);

            Robot robot = new Robot();
            robot.keyPress(java.awt.event.KeyEvent.VK_ENTER);
            robot.keyRelease(java.awt.event.KeyEvent.VK_ENTER);
            robot.keyPress(java.awt.event.KeyEvent.VK_CONTROL);
            robot.keyPress(java.awt.event.KeyEvent.VK_V);
            robot.keyRelease(java.awt.event.KeyEvent.VK_CONTROL);
            robot.delay(2000);
            robot.keyPress(java.awt.event.KeyEvent.VK_ENTER);

        } catch (Exception exp) {
            exp.printStackTrace();
        }
    }

    public static String generateNewTag() {
        int length = 6;
        String allowedChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz" + // alphabets
                "1234567890"; // numbers
        return "AutomaatTest:" + RandomStringUtils.random(length, allowedChars);
    }

    public static boolean elementExists(By id) {
        try {
            getWebDriver().findElement(id);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public static void moveToElement(By locator) {
        Actions builder = new Actions(getWebDriver());
        WebElement selectElement = waitForVisibility(locator);
        builder.moveToElement(selectElement).perform();
    }

    public static void scrollDownToView(By locator) {
        WebElement element = getWebDriver().findElement(locator);
        Actions actions = new Actions(getWebDriver());
        actions.moveToElement(element);
        actions.perform();
        getWebDriver().findElement(By.xpath("//body")).sendKeys(Keys.PAGE_DOWN);
    }

    public static void waitUntilNotVisible(By locator) {
        wait.until(ExpectedConditions.invisibilityOfElementLocated(locator));
    }

    public static String generateUrl() {
        int length = 20;
        String allowedChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz" + // alphabets
                "1234567890"; // numbers
        return "https://" + RandomStringUtils.random(length, allowedChars) + ".ee";
    }

    public static void sendKeysByChar(WebElement element, String input) {
        for (char c : input.toCharArray()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            element.sendKeys(String.valueOf(c));

        }
    }
}
