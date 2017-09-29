package ee.netgroup.hm.components;

import org.openqa.selenium.By;
import ee.netgroup.hm.helpers.Helpers;
import ee.netgroup.hm.page.BrokenMateriasPage;
import ee.netgroup.hm.page.DeletedMateriasPage;
import ee.netgroup.hm.page.DeletedPortfoliosPage;
import ee.netgroup.hm.page.ImproperPortfoliosPage;
import ee.netgroup.hm.page.MyFavoritesPage;
import ee.netgroup.hm.page.MyMaterialsPage;
import ee.netgroup.hm.page.SearchResultsPage;
import ee.netgroup.hm.page.UnreviewedLearningObjectsPage;

public class LeftMenu extends Component{
	
	private static By dashboardButton = By.id("dashboard");
	private By improperPortfolios = By.id("improperPortfolios");
	private static By preschoolEducation = By.xpath("//div/span[text()='Alusharidus']");
	private By estonianLanguageTaxon = By.xpath("//div/span[text()='Eesti keel']");
	private static By myMaterials = By.id("myMaterials");
	private static By myThings = By.xpath("//a[@id='myProfile']");
	private By myPortfolios = By.id("myPortfolios");
	private static By myFavorites = By.id("myFavorites");
	private By unreviewedLearningObjects = By.id("unReviewed");
	private By deletedPortfolios = By.id("deletedPortfolios");
	private By deletedMaterials = By.id("deletedMaterials");
	private By brokenMaterials = By.id("brokenMaterials");
	private By preschoolEducationMaterialCount = By.xpath("//span[@data-ng-bind='materialCount']");
	private By preschoolEducationMaterialCountSearchPage = By.xpath("//span[@data-ng-bind='getNumberOfResults()']");
	
	
	public static LeftMenu clickDashboard() {
		Helpers.waitForClickable(dashboardButton);
		getDriver().findElement(dashboardButton).click();
		return new LeftMenu();
	}

	public ImproperPortfoliosPage clickImproperPortfolios() {
		Helpers.waitForClickable(improperPortfolios);
		getDriver().findElement(improperPortfolios).click();
		return new ImproperPortfoliosPage();
	}

	public static LeftMenu clickToFilterPreschoolEducation() {
		getDriver().findElement(preschoolEducation).click();
		return new LeftMenu();
	}

	public SearchResultsPage clickToFilterPreschoolEducationEstonianLanguage() {
		Helpers.waitForClickable(estonianLanguageTaxon);
		getDriver().findElement(estonianLanguageTaxon).click();
		return new SearchResultsPage();
	}

	public static MyMaterialsPage clickMyMaterials() {
		getDriver().findElement(myMaterials).click();
		return new MyMaterialsPage();
	}

	public static LeftMenu clickMyThings() {
		getDriver().findElement(myThings).click();
		return new LeftMenu();
	}

	public LoginPopUp clickMyPortfolios() {
		Helpers.waitForClickable(myPortfolios);
		getDriver().findElement(myPortfolios).click();
		return new LoginPopUp();
	}

	public LoginPopUp clickMyMaterialsWhenNotLoggedIn() {
		Helpers.waitForClickable(myMaterials);
		getDriver().findElement(myMaterials).click();
		return new LoginPopUp();
	}

	public LoginPopUp clickMyFavorites() {
		Helpers.waitForClickable(myFavorites);
		getDriver().findElement(myFavorites).click();
		return new LoginPopUp();
	}

	public static MyFavoritesPage goToMyFavorites() {
		getDriver().findElement(myFavorites).click();
		return new MyFavoritesPage();
	}

	public UnreviewedLearningObjectsPage clickUnreviewedLearninObjects() {
		Helpers.waitForVisibility(unreviewedLearningObjects);
		getDriver().findElement(unreviewedLearningObjects).click();
		return new UnreviewedLearningObjectsPage();
	}

	public DeletedPortfoliosPage clickDeletedPortfolios() {
		getDriver().findElement(deletedPortfolios).click();
		return new DeletedPortfoliosPage();
	}

	public DeletedMateriasPage clickDeletedMaterials() {
		getDriver().findElement(deletedMaterials).click();
		return new DeletedMateriasPage();
	}

	public BrokenMateriasPage clickBrokenMaterials() {
		getDriver().findElement(brokenMaterials).click();
		return new BrokenMateriasPage();
	}

	public boolean getMaterialCount() {
		Helpers.waitForSeconds(2000);
		String menuCount = getDriver().findElement(preschoolEducationMaterialCount).getText(); 
		int result1 = Integer.parseInt(menuCount);
		String searchCount = getDriver().findElement(preschoolEducationMaterialCountSearchPage).getText(); 
		int result2 = Integer.parseInt(searchCount);

		if(result1 == result2){
		return true;
		}else{
		return false;
		}
	}

}
