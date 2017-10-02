package ee.netgroup.hm.page;

import org.openqa.selenium.By;
import ee.netgroup.hm.helpers.Constants;
import ee.netgroup.hm.helpers.Helpers;

public class EKoolPage extends Page{
	
	private By username = By.id("username");
	private By password = By.id("password");
	private By login = By.cssSelector("button.button-submit");

	
	public EKoolPage insertUsernameAndPassword(String user, String pswd) {
        getDriver().findElement(username).sendKeys(user);
        getDriver().findElement(password).sendKeys(pswd);
        return this;
    }
	
	public LandingPage clickSubmitLogin() {
        getDriver().findElement(login).click();
        getDriver().findElement(login).click();
		Helpers.waitForVisibility(Constants.loginConfirmationText);
        return new LandingPage();
    }

}
