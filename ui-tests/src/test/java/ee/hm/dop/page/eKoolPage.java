package ee.hm.dop.page;

import org.openqa.selenium.By;

import ee.hm.dop.helpers.PageHelpers;

public class eKoolPage extends Page {
	
	private By userName = By.id("username");
	private By password = By.id("password");
	private By login = By.cssSelector("button.button-submit");

	
	public eKoolPage insertUsernameAndPassword(String username, String passWord) {
        getDriver().findElement(userName).sendKeys(username);
        getDriver().findElement(password).sendKeys(passWord);
        return this;
    }
	
	public LandingPage clickSubmitLogin() {
        getDriver().findElement(login).click();
        PageHelpers.waitForSeconds(1500);
        getDriver().findElement(login).click();
        PageHelpers.waitForSeconds(3500);
        return new LandingPage();
    }

}
