package ee.netgroup.htm.page;

import com.codeborne.selenide.Condition;
import ee.netgroup.htm.components.AddMaterialsToPortfolioToolbar;
import ee.netgroup.htm.helpers.Constants;
import ee.netgroup.htm.helpers.Helpers;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;
import static ee.netgroup.htm.util.JSWaiter.waitForJQueryAndAngularToFinishRendering;

public class SearchResultsPage extends Page {

    private By sortDropdown = By.cssSelector("md-select[ng-model='sortDropdown']");
    private By newestFirst = By.cssSelector("md-option[data-translate='ADDED_DATE_DESC']");
    private By closeDetailedSearchButton = By.id("header-hide-detailed-search-icon");
    private By exactMaterials_Title_SearchResultButton = By.id("title-exact-material-button");
    private By exactMaterials_Description_SearchResultButton = By.id("description-exact-material-button");
    private By Portfolios_Title_SearchResultButton = By.id("title-portfolio-button");
    private By Materials_Description_SearchResultButton = By.id("title-material-button");
    private By closeAdvancedSearchHeaderButton = By.id("header-hide-detailed-search-icon");

    public SearchResultsPage() {
        waitForJQueryAndAngularToFinishRendering();
    }

    public static SearchResultsPage getSearchResultsPage() {
        return new SearchResultsPage();
    }

    public SearchResultsPage sortResultsNewestFirst() {
        Helpers.waitForClickable(sortDropdown).click();
        Helpers.waitForClickable(newestFirst).click();
        return this;
    }

    public SearchResultsPage viewAllExactSearchResults_ByTitles_Materials() {
        Helpers.waitForClickable(exactMaterials_Title_SearchResultButton).click();
        return this;
    }

    public SearchResultsPage viewAllExactSearchResults_ByDescription_Materials() {
        Helpers.waitForClickable(exactMaterials_Description_SearchResultButton).click();
        return this;
    }

    public SearchResultsPage viewAllSearchResults_ByTitle_Portfolios() {
        Helpers.waitForClickable(Portfolios_Title_SearchResultButton).click();
        return this;
    }

    public SearchResultsPage viewAllSearchResults_ByTitle_Material() {
        $(Materials_Description_SearchResultButton).shouldBe(Condition.visible).click();
        return this;
    }

    public PortfolioPage openSearchResultPortfolio() {
        Helpers.waitForClickable(Constants.firstPortfolio);
        $(Constants.firstPortfolio).shouldBe(Condition.visible).click();
        return new PortfolioPage();
    }

    public MaterialPage openSearchResultMaterial() {
        Helpers.waitForClickable(Constants.firstMaterial).click();
        return new MaterialPage();
    }

    public AddMaterialsToPortfolioToolbar clickToSelectMaterial() {
        return AddMaterialsToPortfolioToolbar.clickToSelectMaterial();
    }

    public SearchResultsPage closeDetailedSearch() {
        $(closeDetailedSearchButton).shouldBe(Condition.visible).click();
        return this;
    }

    public AddMaterialsToPortfolioToolbar clickToSelectMaterial2() {
        return AddMaterialsToPortfolioToolbar.clickToSelectMaterial2();
    }


    public void openLearningObjectWithTitle(String learningObjectTitle) {
        Helpers.waitForClickable(By.xpath("//md-card-content/h2[text()=\"" + learningObjectTitle + "\"]")).click();
    }
}
