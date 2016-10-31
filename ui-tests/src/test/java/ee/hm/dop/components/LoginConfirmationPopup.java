package ee.hm.dop.components;

import org.openqa.selenium.By;
import ee.hm.dop.page.LandingPage;
import ee.hm.dop.page.MyFavoritesPage;
import ee.hm.dop.page.MyMaterialsPage;
import ee.hm.dop.page.MyPortfoliosPage;



public class LoginConfirmationPopup extends PageComponent {
	
	private By hideLoginButton = By.xpath("//button[@ng-click='hideLogin()']");
	
	public LandingPage clickHideLogin() {
		getDriver().findElement(hideLoginButton).click();
		return new LandingPage();
	}
	
	public MyPortfoliosPage clickHideLogin1() {
		getDriver().findElement(hideLoginButton).click();
		return new MyPortfoliosPage();
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
