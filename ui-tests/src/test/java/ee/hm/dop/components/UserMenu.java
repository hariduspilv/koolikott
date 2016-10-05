package ee.hm.dop.components;

import org.openqa.selenium.By;

import ee.hm.dop.helpers.PageHelpers;
import ee.hm.dop.page.DashboardPage;
import ee.hm.dop.page.LandingPage;
import ee.hm.dop.page.MyProfilePage;

public class UserMenu extends PageComponent {

	private By userMenuIcon = By.id("header-user-menu-icon");
	private By userMenuLogout = By.id("user-menu-logout");
	private By userMenuDashboard = By.id("user-menu-dashboard");
	private By userMenuMyProfile = By.id("user-menu-view-profile");
	private By loginButton = By.id("header-login-icon");
	

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
	

	public DashboardPage clickDashboard() {
		PageHelpers.waitForSeconds(2500);
		getDriver().findElement(userMenuIcon).click();
		PageHelpers.waitForSeconds(1500);
		getDriver().findElement(userMenuDashboard).click();
		return new DashboardPage();

	}
	
	public MyProfilePage clickMyProfile() {
		PageHelpers.waitForVisibility(userMenuIcon);;
		getDriver().findElement(userMenuIcon).click();
		PageHelpers.waitForSeconds(1500);
		getDriver().findElement(userMenuMyProfile).click();
		return new MyProfilePage();

	}
	
	
}
