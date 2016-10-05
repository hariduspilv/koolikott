package ee.hm.dop.components;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;

import ee.hm.dop.helpers.PageHelpers;

public class Header extends PageComponent {

	private By loginButton = By.id("header-login-icon");

	public LoginDialogPopup clickLogin() {
		PageHelpers.waitForSeconds(1500);
		getDriver().navigate().refresh();
		PageHelpers.waitForSeconds(3000);
		getDriver().findElement(loginButton).sendKeys(Keys.ENTER);
		return new LoginDialogPopup();
	}
	
}
