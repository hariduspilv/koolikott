package ee.hm.dop.components;

import org.openqa.selenium.By;
import ee.hm.dop.helpers.PageHelpers;



public class LeftMenu extends PageComponent {
	
	
	private By dashboardButton = By.id("dashboard");
	private By improperPortfolios = By.id("improperPortfolios");
	private By myThings = By.id("myProfile");
	private By myPortfolios = By.id("myPortfolios");
	
	
	public LeftMenu clickDashboard() {
		PageHelpers.waitForSeconds(1500);
		PageHelpers.waitForVisibility(dashboardButton);
		getDriver().findElement(dashboardButton).click();
		return this;
	}
	
	public LeftMenu clickMyThings() {
		getDriver().findElement(myThings).click();
		return this;
	}
	
	public LeftMenu clickMyPortfolios() {
		getDriver().findElement(myPortfolios).click();
		return this;
	}
	
	public LeftMenu checkLeftMenuButtons(){
		PageHelpers.waitForVisibility(myPortfolios);
		if (getDriver().findElement(myPortfolios).isDisplayed()){
			getDriver().findElement(myThings).click();
		}
		return this;
	}
	
	public LeftMenu clickImproperPortfolios() {
		PageHelpers.waitForVisibility(improperPortfolios);
		getDriver().findElement(improperPortfolios).click();
		return this;
	}

}
