package ee.netgroup.hm.components;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import ee.netgroup.hm.helpers.Constants;
import ee.netgroup.hm.helpers.Helpers;
import ee.netgroup.hm.page.SearchResultsPage;

public class Search extends Component{
	
	private static By searchField = By.xpath("//input[@id='header-search-input']");
	private static By numberOfResultsText = By.xpath("//span[contains(text(), 'Otsingule leidus')]");
	private static By advancedSearchIcon = By.id("header-show-detailed-search-icon");
	private By materialTypeSelection = By.xpath("//md-select[@data-ng-model='detailedSearch.resourceType']");
	private By audioType = By.xpath("//md-option[@value='AUDIO']");
	private static By autocompleteSelecton = By.cssSelector("span.highlight");
	private By languageSelection = By.xpath("//md-select[@data-ng-model='detailedSearch.language']");
	private By russianLanguage = By.xpath("//md-option[@value='rus']");


	public static SearchResultsPage insertSearchCriteriaAndSearch(String searchString) {

		if(searchString == "tag"){
			getDriver().findElement(searchField).sendKeys("tag:"+Constants.searchTag);
			Helpers.waitForVisibility(numberOfResultsText);
		}	
		
		if(searchString == "recommended:false"){
			getDriver().findElement(searchField).sendKeys("recommended:false");
			Helpers.waitForVisibility(numberOfResultsText);
		}	
		
		if(searchString == "recommended:true"){
			getDriver().findElement(searchField).sendKeys("recommended:true");
			Helpers.waitForVisibility(numberOfResultsText);
		}	
		
		if(searchString == "publisher"){
			getDriver().findElement(searchField).sendKeys("publisher:"+Constants.searchPublisher);
			Helpers.waitForVisibility(numberOfResultsText);
		}	
			
		return new SearchResultsPage();
	}

	public static Search clickToOpenAdvancedSearch() {
		getDriver().findElement(advancedSearchIcon).click();
		return new Search();
	}

	public SearchResultsPage selectMaterialTypeAudio() {
		getDriver().findElement(materialTypeSelection).click();
		getDriver().findElement(audioType).click();
		return new SearchResultsPage();
	}

	public static SearchResultsPage insertSearchCriteriaWithAutocomplete(String searchString) {
		getDriver().findElement(searchField).sendKeys(searchString);
		Helpers.waitForVisibility(autocompleteSelecton);
		getDriver().findElement(autocompleteSelecton).click();
		Helpers.waitForVisibility(numberOfResultsText);
		return new SearchResultsPage();
	}

	public Search selectMaterialLanguageRussian() {
		getDriver().findElement(languageSelection).click();
		getDriver().findElement(russianLanguage).click();
		return this;
	}

	public SearchResultsPage insertSearchCriteriaInRussian(String searchString) {
		getDriver().findElement(searchField).sendKeys(searchString);
		return new SearchResultsPage();
	}

	public SearchResultsPage insertMaterialSearchCriteria() {
		getDriver().findElement(searchField).sendKeys("a");
		getDriver().findElement(searchField).sendKeys(Keys.ENTER);
		Helpers.waitForVisibility(numberOfResultsText);
		return new SearchResultsPage();
	}



	

}
