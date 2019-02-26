package ee.netgroup.htm.components;

import com.codeborne.selenide.Condition;
import ee.netgroup.htm.helpers.Helpers;
import ee.netgroup.htm.page.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static ee.netgroup.htm.util.JSWaiter.waitForJQueryAndAngularToFinishRendering;

public class LeftMenu {

    private static By improperLearningObjects = By.id("improper");
    private static By preschoolEducation = By.xpath("//div/span[text()='Alusharidus']");
    private static By myMaterials = By.id("myMaterials");
    private static By myThings = By.id("myProfile");
    private static By myFavorites = By.id("myFavorites");
    private static By unreviewedLearningObjects = By.id("unReviewed");
    private static By deletedLearningObjects = By.id("deleted");
    private static By tableOfContents = By.xpath("//span[@data-translate='TABLE_OF_CONTENTS']");
    private static By changedLearningObjects = By.id("changes");
    private By estonianLanguageTaxon = By.xpath("//div/span[text()='Eesti keel']");
    private By myPortfolios = By.id("myPortfolios");
    private By preschoolEducationMaterialCount = By.xpath("//span[@data-ng-bind='materialCount']");
    private By preschoolEducationMaterialCountSearchPage = By.cssSelector("strong");
    private By adminMenu = By.id("Admin");
    private By aboutMenu = By.id("About");

    public LeftMenu() {
        waitForJQueryAndAngularToFinishRendering();
    }

    public static LeftMenu getLeftMenu() {
        return new LeftMenu();
    }

    public static ImproperLearningObjectsPage clickImproperLearningObjects() {
        Helpers.waitForClickable(improperLearningObjects).click();
        return new ImproperLearningObjectsPage();
    }

    public static LeftMenu clickToFilterPreschoolEducation() {
        getWebDriver().findElement(preschoolEducation).click();
        return new LeftMenu();
    }

    public static MyFavoritesPage goToMyFavorites() {
        LeftMenu.getLeftMenu().expandMyProfileMenu();
        Helpers.waitForClickable(myFavorites).click();
        return new MyFavoritesPage();
    }

    public static NewLearningObjectsPage clickUnreviewedLearningObjects() {
        Helpers.waitForClickable(unreviewedLearningObjects).click();
        return new NewLearningObjectsPage();
    }

    public static DeletedLearningObjectsPage clickDeletedLearningObjects() {
        Helpers.waitForClickable(deletedLearningObjects).click();
        return new DeletedLearningObjectsPage();
    }

    public static LeftMenu openTableOfContents() {
        getWebDriver().findElement(tableOfContents).click();
        return new LeftMenu();
    }

    public static ChangedLearningObjectsPage clickChangedLearningObjects() {
        Helpers.waitForClickable(changedLearningObjects).click();
        return new ChangedLearningObjectsPage();
    }

    public LeftMenu expandMyProfileMenu() {
        Helpers.waitForClickable(myThings);
        if(!$(myThings).attr("class").contains("is-opened")){
            $(myThings).click();
        }
        return this;
    }

    public SearchResultsPage clickToFilterPreschoolEducationEstonianLanguage() {
        Helpers.waitForClickable(estonianLanguageTaxon).click();
        return new SearchResultsPage();
    }

    public MyPortfoliosPage clickMyPortfolios() {
        $("#myPortfolios").click();
        return new MyPortfoliosPage();
    }

    public LoginModal clickMyMaterialsWhenNotLoggedIn() {
        Helpers.waitForClickable(myMaterials).click();
        return new LoginModal();
    }

    public MyMaterialsPage clickMyMaterials() {
        LeftMenu.getLeftMenu().expandMyProfileMenu();
        Helpers.waitForClickable(myMaterials).click();
        return new MyMaterialsPage();
    }

    public LoginModal clickMyFavoritesWhenNotLoggedIn() {
        Helpers.waitForClickable(myFavorites).click();
        return new LoginModal();
    }

    public MyFavoritesPage clickMyFavorites() {
        Helpers.waitForClickable(myFavorites).click();
        return new MyFavoritesPage();
    }

    public NewLearningObjectsPage clickNewLearningObjectsMenuItem() {
        $(unreviewedLearningObjects).shouldBe(Condition.visible).click();
        return new NewLearningObjectsPage();
    }

    public boolean isNewLearningObjectsMenuItemVisible() {
        return Helpers.isWebElementVisible(unreviewedLearningObjects);
    }

    public boolean getMaterialCount() {
        String menuCount = getWebDriver().findElement(preschoolEducationMaterialCount).getText();
        int result1 = Integer.parseInt(menuCount);
        String searchCount = getWebDriver().findElement(preschoolEducationMaterialCountSearchPage).getText();
        int result2 = Integer.parseInt(searchCount);

        return result1 == result2;
    }

    public LeftMenu expandTableOfContentsIfNotExpanded() {
        WebElement tableOfContentsWebElement = Helpers.waitForClickable(By.id("sidenavHeader"));
        String classAttribute = tableOfContentsWebElement.getAttribute("class");
        if (!classAttribute.contains("is-opened")) {
            tableOfContentsWebElement.click();
        }
        return this;
    }


    public LeftMenu expandVocationalEducationIfNotExpanded() {
        WebElement vocationalEducationItemWebElement = Helpers.waitForClickable(By.cssSelector("span[data-translate='VOCATIONALEDUCATION']"));
        String classAttribute = vocationalEducationItemWebElement.getAttribute("class");
        if (!classAttribute.contains("is-opened")) {
            vocationalEducationItemWebElement.click();
        }

        return this;
    }

    public LeftMenu selectDomain(String domainTranslationKey) {
        Helpers.waitForClickable(By.cssSelector("span[data-translate='" + domainTranslationKey + "']")).click();
        return this;
    }

    public AdminMenu expandAdminMenu() {
        if(!$(adminMenu).attr("class").contains("is-opened")) {
            $(adminMenu).click();
        }
        return new AdminMenu();
    }

    public AboutMenu expandAboutMenu() {
        if(!$(aboutMenu).attr("class").contains("is-opened")) {
            $(aboutMenu).click();
        }
        return new AboutMenu();
    }

    public static class AdminMenu{ //alamklass
        private By moderatorsMenuItem = By.cssSelector("span[data-translate='MODERATORS_TAB']");
        private By restrictedUsersMenuItem = By.cssSelector("span[data-translate='RESTRICTED_USERS_TAB']");
        private By expertStatistics = By.id("expertStatistics");
        private By moderators = By.id("moderators");
        private By deletedMaterials = By.id("deleted");

        public AdminMenu() {
            waitForJQueryAndAngularToFinishRendering();
        }
        public ModeratorManagementPage clickModerators() {
            $(moderatorsMenuItem).click();
            return new ModeratorManagementPage();
        }

        public RestrictedUserManagementPage clickRestrictedUsers() {
            $(restrictedUsersMenuItem).click();
            return new RestrictedUserManagementPage();
        }

    }

    public static class AboutMenu{
        private By faqMenuItem = By.id("faq");
        private By videoInstructionsMenuItem = By.id("userManual");

        public AboutMenu() {waitForJQueryAndAngularToFinishRendering();}

        public FaqPage clickFaq() {
            Helpers.waitForClickable(faqMenuItem);
            $(faqMenuItem).shouldBe(Condition.visible).click();
            return new FaqPage();
        }

    }


}