package ee.hm.dop.page;

import org.openqa.selenium.By;

import ee.hm.dop.helpers.PageHelpers;

public class PreschoolEducationResultsPage extends Page {
	
	private By openSearchResult = By.xpath("//h3[@data-ng-bind='$ctrl.learningObject.title']");
	private By sortDropdown = By.xpath("//md-select[@ng-model='sortDropdown']");
	private By newFirst = By.xpath("//md-option/div[text()='Uusimad eespool']");
	
	public PreschoolEducationResultsPage sortResults() {
		PageHelpers.waitForVisibility(sortDropdown);
		getDriver().findElement(sortDropdown).click();
		getDriver().findElement(newFirst).click();
		return this;
	}
	
	public SearchResultsPage openSearchResult() {
        getDriver().findElement(openSearchResult).click();
		return new SearchResultsPage();
	}

}
