package ee.hm.dop.components;

import org.openqa.selenium.By;

import ee.hm.dop.page.LandingPage;

public class Logo extends PageComponent {
	
	private By logo = By.id("logo");
	
	public LandingPage clickLogo() {
		getDriver().findElement(logo).click();
		return new LandingPage();

	}

}
