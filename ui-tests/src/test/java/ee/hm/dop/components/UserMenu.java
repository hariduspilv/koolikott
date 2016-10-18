package ee.hm.dop.components;

import org.openqa.selenium.By;
import ee.hm.dop.helpers.PageHelpers;
import ee.hm.dop.page.ImproperPortfoliosDashboardPage;
import ee.hm.dop.page.LandingPage;


public class UserMenu extends PageComponent {

	private By userMenuIcon = By.id("header-user-menu-icon");
	private By userMenuLogout = By.id("user-menu-logout");
	private By userMenuDashboard = By.id("user-menu-dashboard");
	private By loginButton = By.id("header-login-icon");
	private By currentUser = By.cssSelector("a#user-menu-dashboard span");
	

	public LandingPage logOff() {
		getDriver().navigate().refresh();
		PageHelpers.waitForSeconds(1500);
		getDriver().findElement(userMenuIcon).click();
		PageHelpers.waitForSeconds(1500);
		getDriver().findElement(userMenuLogout).click();
		return new LandingPage();
	}
	
	public LandingPage logoutIfLoggedIn() {
		
		if (!getDriver().findElement(loginButton).isDisplayed()){
			getDriver().findElement(userMenuIcon).click();
			PageHelpers.waitForSeconds(1500);
			getDriver().findElement(userMenuLogout).click();
		}
			
		return new LandingPage();
		
	}
	

	public ImproperPortfoliosDashboardPage clickDashboard() {
		PageHelpers.waitForSeconds(2500);
		getDriver().findElement(userMenuIcon).click();
		PageHelpers.waitForSeconds(1500);
		getDriver().findElement(userMenuDashboard).click();
		return new ImproperPortfoliosDashboardPage();

	}
	
	public UserMenu clickProfileIcon() {
		PageHelpers.waitForVisibility(userMenuIcon);;
		getDriver().findElement(userMenuIcon).click();
		PageHelpers.waitForSeconds(1500);
		return this;

	}
	
	public String getUserName() {
		return getDriver().findElement(currentUser).getText();
	}
	
	
}
