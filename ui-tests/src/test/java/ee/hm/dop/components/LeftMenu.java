package ee.hm.dop.components;

import org.openqa.selenium.By;
import ee.hm.dop.helpers.PageHelpers;
import ee.hm.dop.page.MaterialPage;



public class LeftMenu extends PageComponent {
	
	
	private By dashboardButton = By.id("dashboard");
	private By improperPortfolios = By.id("improperPortfolios");
	private By myThings = By.id("myProfile");
	private By myPortfolios = By.id("myPortfolios");
	private By myMaterials = By.id("myMaterials");
	private By myFavorites = By.id("myFavorites");
	
	
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
	
	public LeftMenu clickMyFavorites() {
		getDriver().findElement(myFavorites).click();
		return this;
	}
	
	public MaterialPage clickMyMaterials() {
		getDriver().findElement(myMaterials).click();
		return new MaterialPage();
	}
	
	public LeftMenu clickMyMaterialsWhenNotLoggedIn() {
		getDriver().findElement(myMaterials).click();
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
