package ee.netgroup.hm.components;

import org.openqa.selenium.By;
import ee.netgroup.hm.helpers.Helpers;
import ee.netgroup.hm.page.ChangedLearningObjectsPage;
import ee.netgroup.hm.page.DeletedLearningObjectsPage;
import ee.netgroup.hm.page.ImproperLearningObjectsPage;
import ee.netgroup.hm.page.MyFavoritesPage;
import ee.netgroup.hm.page.MyMaterialsPage;
import ee.netgroup.hm.page.SearchResultsPage;
import ee.netgroup.hm.page.UnreviewedLearningObjectsPage;

public class LeftMenu extends Components{
	
	private static By improperLearningObjects = By.id("improper");
	private static By preschoolEducation = By.xpath("//div/span[text()='Alusharidus']");
	private By estonianLanguageTaxon = By.xpath("//div/span[text()='Eesti keel']");
	private static By myMaterials = By.id("myMaterials");
	private static By myThings = By.xpath("//a[@id='myProfile']");
	private By myPortfolios = By.id("myPortfolios");
	private static By myFavorites = By.id("myFavorites");
	private static By unreviewedLearningObjects = By.id("unReviewed");
	private static By deletedLearningObjects = By.id("deleted");
	private By preschoolEducationMaterialCount = By.xpath("//span[@data-ng-bind='materialCount']");
	private By preschoolEducationMaterialCountSearchPage = By.cssSelector("strong");
	private static By tableOfContents = By.xpath("//span[@data-translate='TABLE_OF_CONTENTS']");
	private static By changedLearningObjects = By.id("changes");

	public static ImproperLearningObjectsPage clickImproperLearningObjects() {
		Helpers.waitForClickable(improperLearningObjects);
		getDriver().findElement(improperLearningObjects).click();
		return new ImproperLearningObjectsPage();
	}

	public static LeftMenu clickToFilterPreschoolEducation() {
		Helpers.waitForMilliseconds(1000);
		getDriver().findElement(preschoolEducation).click();
		return new LeftMenu();
	}

	public SearchResultsPage clickToFilterPreschoolEducationEstonianLanguage() {
		Helpers.waitForMilliseconds(1000);
		getDriver().findElement(estonianLanguageTaxon).click();
		Helpers.waitForMilliseconds(1000);
		return new SearchResultsPage();
	}

	public static MyMaterialsPage clickMyMaterials() {
		Helpers.waitForMilliseconds(1000);
		getDriver().findElement(myMaterials).click();
		return new MyMaterialsPage();
	}

	public static LeftMenu clickMyThings() {
		getDriver().findElement(myThings).click();
		return new LeftMenu();
	}

	public LoginModal clickMyPortfolios() {
		Helpers.waitForClickable(myPortfolios);
		getDriver().findElement(myPortfolios).click();
		return new LoginModal();
	}

	public LoginModal clickMyMaterialsWhenNotLoggedIn() {
		Helpers.waitForClickable(myMaterials);
		getDriver().findElement(myMaterials).click();
		return new LoginModal();
	}

	public LoginModal clickMyFavorites() {
		Helpers.waitForClickable(myFavorites);
		getDriver().findElement(myFavorites).click();
		return new LoginModal();
	}

	public static MyFavoritesPage goToMyFavorites() {
		Helpers.waitForClickable(myFavorites);
		getDriver().findElement(myFavorites).click();
		return new MyFavoritesPage();
	}

	public static UnreviewedLearningObjectsPage clickUnreviewedLearninObjects() {
		Helpers.waitForVisibility(unreviewedLearningObjects);
		getDriver().findElement(unreviewedLearningObjects).click();
		return new UnreviewedLearningObjectsPage();
	}

	public static DeletedLearningObjectsPage clickDeletedLearningObjects() {
		Helpers.waitForClickable(deletedLearningObjects);
		getDriver().findElement(deletedLearningObjects).click();
		return new DeletedLearningObjectsPage();
	}

	public boolean getMaterialCount() {
		Helpers.waitForMilliseconds(2000);
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

	public static LeftMenu openTableOfContents() {
		getDriver().findElement(tableOfContents).click();
		return new LeftMenu();
	}

	public static ChangedLearningObjectsPage clickChangedLearningObjects() {
		Helpers.waitForVisibility(changedLearningObjects);
		getDriver().findElement(changedLearningObjects).click();
		return new ChangedLearningObjectsPage();
	}

}

