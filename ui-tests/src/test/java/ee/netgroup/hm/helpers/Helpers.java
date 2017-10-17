package ee.netgroup.hm.helpers;

import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.util.Random;

import org.apache.commons.lang3.RandomStringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import ee.netgroup.hm.tests.SeleniumUser;

public class Helpers extends SeleniumUser{
	
	public static void waitForMilliseconds(long milliSeconds) {

		try {
			Thread.sleep(milliSeconds);
		} catch (InterruptedException e) {

			e.printStackTrace();
		}
	}
	
	 public static void waitForVisibility(By locator) {
	        WebDriverWait wait = new WebDriverWait(getDriver(), 50);
	        wait.until(ExpectedConditions.visibilityOfElementLocated(locator));   
	}
	 
	 public static void waitForClickable(By locator) {
	        WebDriverWait wait = new WebDriverWait(getDriver(), 20);
	        wait.until(ExpectedConditions.elementToBeClickable(locator));
	 }
	 
		public static String randomElement(String[] array) {
			String randomElement = array[new Random().nextInt(array.length)];
			return randomElement;
		}
	
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
		String newGeneratedTag = "AutomaatTest:"+RandomStringUtils.random(length, allowedChars);
		return newGeneratedTag;
	}

	public static boolean elementExists(By id) {
        try {
            getDriver().findElement(id);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

	public static void moveToElement(By locator) {
		Actions builder = new Actions(getDriver());
		WebElement selectElement = getDriver().findElement(locator);
		builder.moveToElement(selectElement).perform();
	}

	public static String generateUrl(int length) {
		String allowedChars = "abcdefghijklmnopqrstuvwxyz" + // alphabets
				"1234567890"; // numbers
		String url = "";
		String temp = RandomStringUtils.random(length, allowedChars);
		url = temp.substring(0, temp.length() - 9) + ".com"; // .sendKeys(Helpers.generateEmail(30));
		return url;
	}

	public static CharSequence generateRegisterNumber(int length) {
		String allowedChars = "ABCDEFGHIJKLMNOPRSTUQWXZV" + // alphabets
				"1234567890"; // numbers
		String regnumber = "";
		String temp = RandomStringUtils.random(length, allowedChars);
		regnumber = temp.substring(0, temp.length() - 9); // .sendKeys(Helpers.generateRegisterNumber(20));
		return regnumber;
	}

	public static void scrollDownToView(By locator) {
		WebElement element = getDriver().findElement(locator);
		Actions actions = new Actions(getDriver());
		actions.moveToElement(element);
		actions.perform();
		getDriver().findElement(By.xpath("//body")).sendKeys(Keys.PAGE_DOWN);		
	}

	public static void waitUntilNotVisible(By locator) {
        WebDriverWait wait = new WebDriverWait(getDriver(), 20);
        wait.until(ExpectedConditions.invisibilityOfElementLocated(locator));
    }

	
}
