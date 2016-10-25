package ee.hm.dop.page;

import org.openqa.selenium.By;

import ee.hm.dop.helpers.PageHelpers;

public class StuudiumConfirmationPage extends Page {
	
	private By permissionButton = By.name("data[Authorize][allow]");
	
	public LandingPage clickGivePermissionButton() {
        getDriver().findElement(permissionButton).click();
        PageHelpers.waitForSeconds(1500);
        return new LandingPage();
    }
	

}
