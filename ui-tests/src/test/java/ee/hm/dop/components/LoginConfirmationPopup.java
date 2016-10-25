package ee.hm.dop.components;

import org.openqa.selenium.By;
import ee.hm.dop.page.LandingPage;
import ee.hm.dop.page.MyFavoritesPage;
import ee.hm.dop.page.MyMaterialsPage;
import ee.hm.dop.page.PortfolioPage;


public class LoginConfirmationPopup extends PageComponent {
	
	private By hideLoginButton = By.xpath("//button[@ng-click='hideLogin()']");
	
	public LandingPage clickHideLogin() {
		getDriver().findElement(hideLoginButton).click();
		return new LandingPage();
	}
	
	public PortfolioPage clickHideLogin1() {
		getDriver().findElement(hideLoginButton).click();
		return new PortfolioPage();
	}
	
	public MyMaterialsPage clickHideLogin2() {
		getDriver().findElement(hideLoginButton).click();
		return new MyMaterialsPage();
	}
	
	public MyFavoritesPage clickHideLogin3() {
		getDriver().findElement(hideLoginButton).click();
		return new MyFavoritesPage();
	}

}
