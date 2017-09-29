package ee.netgroup.hm.page;

import org.openqa.selenium.By;
import ee.netgroup.hm.components.AddMaterialsToPortfolioToolbar;
import ee.netgroup.hm.helpers.Constants;
import ee.netgroup.hm.helpers.Helpers;

public class SearchResultsPage extends Page{
	
	private By sortDropdown = By.xpath("//md-select[@ng-model='sortDropdown']");
    private By newestFirst = By.xpath("//md-option/div[text()='Uusimad eespool']");
    //private By searchResultMaterial = By.xpath("//h3[@data-ng-bind='$ctrl.getCorrectLanguageTitle($ctrl.learningObject)']");

	public SearchResultsPage sortResultsNewestFirst() {
		getDriver().findElement(sortDropdown).click();
		getDriver().findElement(newestFirst).click();
		Helpers.waitForSeconds(1000);
		return this;
	}

	public PortfolioPage openSearchResultPortfolio() {
		getDriver().findElement(Constants.firstPortfolio).click();
		return new PortfolioPage();
	}

	public MaterialPage openSearchResultMaterial() {
		getDriver().findElement(Constants.firstMaterial).click();
		return new MaterialPage();
	}

	public AddMaterialsToPortfolioToolbar clickToSelectMaterial() {
		return AddMaterialsToPortfolioToolbar.clickToSelectMaterial();
	}




}
