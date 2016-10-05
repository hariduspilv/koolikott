package ee.hm.dop.components;

import org.openqa.selenium.By;
import ee.hm.dop.page.LandingPage;


public class LoginConfirmationPopup extends PageComponent {
	
	private By hideLoginButton = By.xpath("//button[@ng-click='hideLogin()']");
	
	public LandingPage clickHideLogin() {
		getDriver().findElement(hideLoginButton).click();
		return new LandingPage();
	}

}
