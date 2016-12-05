package ee.hm.dop.components;

import org.openqa.selenium.By;
import ee.hm.dop.helpers.PageHelpers;
import ee.hm.dop.page.MyMaterialsPage;
import ee.hm.dop.page.PreschoolEducationResultsPage;



public class LeftMenu extends PageComponent {
	
	
	private By dashboardButton = By.id("dashboard");
	private By improperPortfolios = By.id("improperPortfolios");
	private By myThings = By.id("myProfile");
	private By myPortfolios = By.id("myPortfolios");
	private By myMaterials = By.id("myMaterials");
	private By myFavorites = By.id("myFavorites");
	private By tableOfContents = By.id("sidenavTaxon");
	private By preschoolEducation = By.xpath("//div/span[text()='Alusharidus']");
	private By estonianLanguageTaxon = By.xpath("//div/span[text()='Eesti keel']");
	
	
	public LeftMenu clickDashboard() {
		PageHelpers.waitForVisibility(dashboardButton);
		getDriver().findElement(dashboardButton).click();
		return this;
	}
	
	public LeftMenu clickMyThings() {
		getDriver().findElement(myThings).click();
		return this;
	}
	
	public PreschoolEducationResultsPage clickToFilterPreschoolEducation() {
		getDriver().findElement(preschoolEducation).click();
		return new PreschoolEducationResultsPage();
	}
	
	public PreschoolEducationResultsPage clickToFilterPreschoolEducationEstonianLanguage() {
		getDriver().findElement(estonianLanguageTaxon).click();
		return new PreschoolEducationResultsPage();
	}
	
	public LeftMenu clickTableOfContents() {
		PageHelpers.waitForVisibility(tableOfContents);
		getDriver().findElement(tableOfContents).click();
		return this;
	}
	
	public LeftMenu clickMyFavorites() {
		getDriver().findElement(myFavorites).click();
		return this;
	}
	
	public MyMaterialsPage clickMyMaterials() {
		getDriver().findElement(myMaterials).click();
		return new MyMaterialsPage();
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
