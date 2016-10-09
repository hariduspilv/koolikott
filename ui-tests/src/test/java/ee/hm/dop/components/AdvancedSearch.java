package ee.hm.dop.components;

import org.openqa.selenium.By;

import ee.hm.dop.helpers.PageHelpers;
import ee.hm.dop.page.LandingPage;

public class AdvancedSearch extends PageComponent {
	
	private By advancedSearchIcon = By.id("header-show-detailed-search-icon");
	private By materialTypeSelection = By.xpath("//md-select[@data-ng-model='detailedSearch.resourceType']");
	private By audioType = By.xpath("//md-option[@value='AUDIO']");
	private By searchButton = By.xpath("//button[@data-ng-click='search()']");
	private By languageSelection = By.xpath("//md-select[@data-ng-model='detailedSearch.language']");
	private By russianLanguage = By.xpath("//md-option[@value='rus']");
	
	
	public AdvancedSearch clickToOpenAdvancedSearch() {
		getDriver().findElement(advancedSearchIcon).click();
		return new AdvancedSearch();
	}
	
	
	public AdvancedSearch selectMaterialType() {
		getDriver().findElement(materialTypeSelection).click();
		getDriver().findElement(audioType).click();
		PageHelpers.waitForSeconds(1500);
		return this;
	}
	
	public AdvancedSearch selectMaterialLanguage() {
		getDriver().findElement(languageSelection).click();
		getDriver().findElement(russianLanguage).click();
		PageHelpers.waitForSeconds(1500);
		return this;
	}
	
	public LandingPage clickSearch() {
		getDriver().findElement(searchButton).click();
		return new LandingPage();
	}
	
	

}
