package ee.netgroup.hm.page;

import org.openqa.selenium.By;
import ee.netgroup.hm.helpers.Constants;
import ee.netgroup.hm.helpers.Helpers;

public class StuudiumPage extends Page{
	
	private By username = By.id("username");
	private By password = By.id("password");
	private By submit = By.xpath("//input[@type='submit']");
	private By permissionButton = By.name("data[Authorize][allow]");
	
	public StuudiumPage insertUsernameAndPassword(String user, String pswd) {
		getDriver().findElement(username).clear();
		getDriver().findElement(username).sendKeys(user);
		getDriver().findElement(password).clear();
        getDriver().findElement(password).sendKeys(pswd);
        return this;
    }
	
	public StuudiumPage submitStuudium() {
        getDriver().findElement(submit).click();
        return new StuudiumPage();
    }
	
	public LandingPage clickGivePermission() {
        getDriver().findElement(permissionButton).click();
		Helpers.waitForVisibility(Constants.loginConfirmationText);
        return new LandingPage();
    }

}
