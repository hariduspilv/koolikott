package ee.netgroup.hm.page;

import org.openqa.selenium.By;
import ee.netgroup.hm.components.AddMaterialPopUp;
import ee.netgroup.hm.components.AddPortfolioForm;
import ee.netgroup.hm.components.LeftMenu;
import ee.netgroup.hm.components.Search;
import ee.netgroup.hm.components.fabButton;
import ee.netgroup.hm.helpers.Constants;
import ee.netgroup.hm.helpers.Helpers;

public class MyPortfoliosPage extends Page{
	
	//private By firstPortfolio = By.xpath("//h3[@data-ng-bind='$ctrl.learningObject.title']");
	private static By addPortfolioMessage = By.cssSelector("h3");
	
	
	public AddPortfolioForm clickAddPortfolio() {
		return fabButton.clickAddPortfolio();
	}

	public PortfolioPage openPortfolio() {
		getDriver().findElement(Constants.firstPortfolio).click();
		return new PortfolioPage();
	}

	public LeftMenu clickDashboard() {
		return LeftMenu.clickDashboard();
	}

	public SearchResultsPage insertSearchCriteriaAndSearch(String searchString) {
		return Search.insertSearchCriteriaAndSearch(searchString);
	}

	public Search clickToOpenAdvancedSearch() {
		return Search.clickToOpenAdvancedSearch();
	}

	public SearchResultsPage insertSearchCriteriaWithAutocomplete(String searchString) {
		return Search.insertSearchCriteriaWithAutocomplete(searchString);
	}

	public LeftMenu clickToFilterPreschoolEducation() {
		return LeftMenu.clickToFilterPreschoolEducation();
	}

	public AddMaterialPopUp clickAddMaterial() {
		return fabButton.clickAddMaterial();
	}

	public MyMaterialsPage clickMyMaterials() {
		return LeftMenu.clickMyMaterials();
	}

	public static boolean getAddPortfolioMessageText() {
		Helpers.waitForSeconds(1000);
		return getDriver().findElement(addPortfolioMessage).getText().contains("Kogumiku lisamiseks");
	}

	public MyFavoritesPage goToMyFavorites() {
		return LeftMenu.goToMyFavorites();
	}




}
