package ee.netgroup.htm.components;

import com.codeborne.selenide.Condition;
import ee.netgroup.htm.helpers.Constants;
import ee.netgroup.htm.helpers.Helpers;
import ee.netgroup.htm.page.SearchResultsPage;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;

public class Search {

    private static By searchField = By.xpath("//input[@id='header-search-input']");
    private static By numberOfExactResultsText = By.id("exact-search-result-title");
    private static By numberOfResultsText = By.xpath("//h1[contains(text(), 'leidub vasteid otsingule')]");
    private static By advancedSearchIcon = By.id("header-show-detailed-search-icon");
    private static By autocompleteSelecton = By.cssSelector("span.highlight");
    private By materialTypeSelection = By.xpath("//md-select[@data-ng-model='detailedSearch.resourceType']");
    private By audioType = By.xpath("//md-option[@value='AUDIO']");
    private By languageSelection = By.xpath("//md-select[@data-ng-model='detailedSearch.language']");
    private By russianLanguage = By.xpath("//md-option[@value='rus']");
    private By educationalContext = By.id("taxonEducationalSelect");
    private By allEducationalContexts = By.xpath("/html/body/div[3]/md-select-menu/md-content/md-option[1]");


    public static SearchResultsPage insertSearchCriteriaAndSearch(String searchString) {

        if (searchString.equals("tag")) {
            $(searchField).shouldBe(Condition.visible).sendKeys("tag:" + Constants.searchTag);
            Helpers.waitForVisibility(numberOfResultsText);
        }

        if (searchString.equals("recommended:false")) {
            $(searchField).shouldBe(Condition.visible).sendKeys("recommended:false");
            Helpers.waitForVisibility(numberOfResultsText);
        }

        if (searchString.equals("recommended:true")) {
            $(searchField).shouldBe(Condition.visible).sendKeys("recommended:true");
            Helpers.waitForVisibility(numberOfResultsText);
        }

        if (searchString.equals("publisher")) {
            $(searchField).shouldBe(Condition.visible).sendKeys("publisher:" + Constants.searchPublisher);
            Helpers.waitForVisibility(numberOfResultsText);
        }

        return new SearchResultsPage();
    }

    public static Search clickToOpenAdvancedSearch() {
        Helpers.waitForClickable(advancedSearchIcon).click();
        return new Search();
    }

    public static SearchResultsPage insertSearchCriteriaWithAutocomplete(String searchString) {
        $(searchField).shouldBe(Condition.visible).sendKeys(searchString);
        $(autocompleteSelecton).shouldBe(Condition.visible).click();
        $(numberOfExactResultsText).shouldBe(Condition.visible);
        return new SearchResultsPage();
    }

    public static SearchResultsPage searchTag(String tag) {
        $(searchField).shouldBe(Condition.visible).sendKeys("tag:" + tag);
        Helpers.waitForVisibility(numberOfResultsText);
        return new SearchResultsPage();
    }

    public SearchResultsPage selectMaterialTypeAudio() {
        $(materialTypeSelection).shouldBe(Condition.visible).click();
        $(audioType).shouldBe(Condition.visible).click();
        return new SearchResultsPage();
    }

    public Search selectMaterialLanguageRussian() {
        $(languageSelection).shouldBe(Condition.visible).click();
        $(russianLanguage).shouldBe(Condition.visible).click();
        return this;
    }

    public SearchResultsPage insertSearchCriteriaInRussian(String searchString) {
        $(searchField).shouldBe(Condition.visible).sendKeys(searchString);
        return new SearchResultsPage();
    }

    public SearchResultsPage insertMaterialSearchCriteria() {
        $(searchField).shouldBe(Condition.visible).sendKeys("a");
        $(searchField).shouldBe(Condition.visible).sendKeys(Keys.ENTER);
        Helpers.waitForVisibility(numberOfResultsText);
        return new SearchResultsPage();
    }

    public Search selectAllEducationalContexts() {
        getWebDriver().findElement(educationalContext).click();
        getWebDriver().findElement(allEducationalContexts).click();
        return this;
    }


}
