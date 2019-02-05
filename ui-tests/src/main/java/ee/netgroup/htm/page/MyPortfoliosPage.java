package ee.netgroup.htm.page;

import ee.netgroup.htm.components.CreateMaterialModal;
import ee.netgroup.htm.components.CreatePortfolioModal;
import ee.netgroup.htm.components.LeftMenu;
import ee.netgroup.htm.components.Search;
import ee.netgroup.htm.helpers.Constants;
import ee.netgroup.htm.helpers.Helpers;
import org.openqa.selenium.By;

import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static ee.netgroup.htm.components.FabButton.getFabButton;

public class MyPortfoliosPage extends Page {

    private static By addPortfolioMessage = By.cssSelector("h2[data-translate='TO_ADD_PORTFOLIO_PRESS_ADD_MATERIAL']");
    private By firstPrivatePortfolio = By.xpath("//md-icon[@aria-label='visibility_off']");

    public static MyPortfoliosPage getMyPortfoliosPage() {
        return new MyPortfoliosPage();
    }

    public String getAddPortfolioMessageText() {
        return getWebDriver().findElement(addPortfolioMessage).getText();
    }

    public CreatePortfolioModal clickAddPortfolio() {
        return getFabButton().clickAddPortfolio();
    }

    public boolean isAddPortfolioButtonVisible() {
        return getFabButton().isAddPortfolioButtonVisible();
    }

    public PortfolioPage openPortfolio() {
        Helpers.waitForClickable(Constants.firstPortfolio).click();
        return new PortfolioPage();
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

    public CreateMaterialModal clickAddMaterial() {
        return getFabButton().clickAddMaterial();
    }

    public MyMaterialsPage clickMyMaterials() {
        return LeftMenu.getLeftMenu().expandMyProfileMenu().clickMyMaterials();
    }

    public MyFavoritesPage goToMyFavorites() {
        return LeftMenu.goToMyFavorites();
    }

    public LeftMenu openTableOfContents() {
        return LeftMenu.openTableOfContents();
    }

    public ImproperLearningObjectsPage clickImproperLearningObjects() {
        return LeftMenu.getLeftMenu().expandMyProfileMenu().clickImproperLearningObjects();
    }

    public NewLearningObjectsPage clickNewLearningObjectsInLeftMenu() {
        return LeftMenu.getLeftMenu().expandMyProfileMenu().clickUnreviewedLearningObjects();
    }

    public DeletedLearningObjectsPage clickDeletedLearningObjects() {
        return LeftMenu.clickDeletedLearningObjects();
    }

    public ChangedLearningObjectsPage clickChangedLearningObjects() {
        return LeftMenu.getLeftMenu().expandMyProfileMenu().clickChangedLearningObjects();
    }

    public PortfolioPage openPrivatePortfolio() {
        Helpers.waitForClickable(firstPrivatePortfolio).click();
        return new PortfolioPage();
    }


}
