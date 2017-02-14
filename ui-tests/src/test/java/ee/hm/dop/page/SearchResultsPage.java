package ee.hm.dop.page;

import org.openqa.selenium.By;

import ee.hm.dop.helpers.PageHelpers;

public class SearchResultsPage extends Page {
	
	private By educationalTaxon = By.cssSelector("span.hide-overflow");
	private By searchResultPortfolio = By.xpath("//h3[@data-ng-bind='$ctrl.learningObject.title']");
	private By sortDropdown = By.xpath("//md-select[@ng-model='sortDropdown']");
    private By newFirst = By.xpath("//md-option/div[text()='Uusimad eespool']");
	
	public String getEducationalTaxonText() {
		return getDriver().findElement(educationalTaxon).getText();

	}
	
	public PortfolioViewPage openSearchResultPortfolio() {
		getDriver().findElement(searchResultPortfolio).click();
		PageHelpers.waitForSeconds(1500);
		return new PortfolioViewPage();

	}
	
	public SearchResultsPage sortResults() {
		PageHelpers.waitForVisibility(sortDropdown);
		getDriver().findElement(sortDropdown).click();
		getDriver().findElement(newFirst).click();
		return this;
	}

}
