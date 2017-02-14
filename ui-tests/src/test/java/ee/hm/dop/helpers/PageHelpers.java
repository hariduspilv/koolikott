package ee.hm.dop.helpers;

import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;
//import java.io.File;
//import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

//import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.openqa.selenium.By;
//import org.openqa.selenium.OutputType;
//import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import ee.hm.dop.tests.SeleniumUser;


public class PageHelpers extends SeleniumUser {

	public static void waitForSeconds(long milliSeconds) {

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
	 
	 public static void pressEsc() {

			try {	

				Robot robot = new Robot();

				robot.keyPress(KeyEvent.VK_CONTROL);
				robot.keyPress(KeyEvent.VK_ESCAPE);

				robot.keyRelease(KeyEvent.VK_ESCAPE);
				robot.keyRelease(KeyEvent.VK_CONTROL);

				robot.delay(50);

			} catch (Exception exp) {
				exp.printStackTrace();
			}
		}
	 
	 public static void pressEnter() {

			try {	

				Robot robot = new Robot();

				robot.keyPress(KeyEvent.VK_ENTER);
				robot.keyRelease(KeyEvent.VK_ENTER);


				robot.delay(50);

			} catch (Exception exp) {
				exp.printStackTrace();
			}
		}
	 
	 
	 
	 public static String getDate(int period,String format)
	 {
	      Calendar currentDate = Calendar.getInstance();
	      SimpleDateFormat formatter= new SimpleDateFormat(format);
	      currentDate.add(Calendar.DAY_OF_MONTH, period);
	      String date = formatter.format(currentDate.getTime());
	      return date;
	 }
	 
	 
	
		
	
	public static String generateUrl(int length) {
		String allowedChars = "abcdefghijklmnopqrstuvwxyz" + // alphabets
				"1234567890" + // numbers
				"."; // special characters
		String url = "";
		String temp = RandomStringUtils.random(length, allowedChars);
		url = temp.substring(0, temp.length() - 9) + ".com"; // .sendKeys(Helpers.generateEmail(30));
		return url;
	}
	
	public static void setClipboardData(String string) {

		StringSelection stringSelection = new StringSelection(string);
		Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stringSelection, null);

	}

	public static void uploadFile(String fileLocation) {

		try {

			setClipboardData(fileLocation);
			
			Robot robot = new Robot();
			
			robot.delay(250);

			robot.keyPress(KeyEvent.VK_CONTROL);
			robot.keyPress(KeyEvent.VK_V);

			robot.keyRelease(KeyEvent.VK_V);
			robot.keyRelease(KeyEvent.VK_CONTROL);

			robot.delay(250);

			robot.keyPress(KeyEvent.VK_ENTER);
			robot.keyRelease(KeyEvent.VK_ENTER);
			
			robot.delay(50);


		

		} catch (Exception exp) {
			exp.printStackTrace();
		}
	}
		
		public static void uploadFile1(String fileLocation) {

			try {

				setClipboardData(fileLocation);
				
				Robot robot = new Robot();
				
				robot.delay(110);

				robot.keyPress(KeyEvent.VK_CONTROL);
				robot.keyPress(KeyEvent.VK_V);

				robot.keyRelease(KeyEvent.VK_V);
				robot.keyRelease(KeyEvent.VK_CONTROL);

				robot.delay(500);

				robot.keyPress(KeyEvent.VK_ENTER);
				robot.keyRelease(KeyEvent.VK_ENTER);
				
				robot.delay(50);

			

			} catch (Exception exp) {
				exp.printStackTrace();
			}
	}

	
	

}
