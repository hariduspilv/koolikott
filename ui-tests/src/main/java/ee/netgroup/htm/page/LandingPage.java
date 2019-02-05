package ee.netgroup.htm.page;

import com.codeborne.selenide.Condition;
import ee.netgroup.htm.components.NotificationToast;
import ee.netgroup.htm.components.ReportImproperModal;
import ee.netgroup.htm.components.Search;
import ee.netgroup.htm.components.UserMenu;
import ee.netgroup.htm.helpers.Constants;
import ee.netgroup.htm.helpers.Helpers;
import org.openqa.selenium.By;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static ee.netgroup.htm.helpers.Constants.EKOOLIKOTT_HOME_PAGE_URL;
import static ee.netgroup.htm.page.MyFavoritesPage.getMyFavoritesPage;
import static ee.netgroup.htm.page.MyMaterialsPage.getMyMaterialsPage;
import static ee.netgroup.htm.page.MyPortfoliosPage.getMyPortfoliosPage;
import static ee.netgroup.htm.util.JSWaiter.waitForJQueryAndAngularToFinishRendering;

public class LandingPage extends Page {

    private static Logger logger = LoggerFactory.getLogger(LandingPage.class);
    private By doNotRestoreLastLocationBtn = By.cssSelector("#location-restore-buttons > button.md-secondary");

    public LandingPage() {
        waitForJQueryAndAngularToFinishRendering();
    }

    public static LandingPage goToLandingPage() {
        open(EKOOLIKOTT_HOME_PAGE_URL);
        return new LandingPage();
    }

    public static LandingPage getLandingPage() {
        return new LandingPage();
    }

    public MaterialPage openFirstMaterial() {
        $(Constants.firstMaterial).click();
        return new MaterialPage();
    }

    public PortfolioPage openFirstPortfolio() {
        $(Constants.firstPortfolio).click();
        return new PortfolioPage();
    }

    public MaterialPage openMaterialUrl() {
        getWebDriver().get(Constants.materialWithCommentsUrl);
        return new MaterialPage();
    }


    public void loginUsingDev(String idCode) {
        open(EKOOLIKOTT_HOME_PAGE_URL + "dev/login/" + idCode);
    }

    public MyPortfoliosPage chooseUserType(String userType) {

        if (userType.equals("Admin"))
            open(Constants.admin);

        if (userType.equals("Publisher"))
            open(Constants.publisher);

        if (userType.equals("User"))
            open(Constants.user);

        if (userType.equals("Moderator"))
            open(Constants.moderator);

        if (userType.equals("Restricted"))
            open(Constants.restricted);

        Helpers.waitUntilNotVisible(NotificationToast.getElementLocator());

        return new MyPortfoliosPage();
    }

    public String isPortfolioDeletedTextVisible() {
        return $(Constants.toastText).shouldBe(Condition.visible).getText();
    }

    public UserMenu logoutIfLoggedIn() {
        return UserMenu.logoutIfLoggedIn();
    }

    public UserMenu clickOnUserProfile() {
        return UserMenu.clickProfileIcon();
    }

    public String getAddPortfolioMessageText() {
        return getMyPortfoliosPage().getAddPortfolioMessageText();
    }

    public String getAddMaterialMessageText() {
        return getMyMaterialsPage().getAddMaterialMessageText();
    }

    public String getAddFavoritesMessageText() {
        return getMyFavoritesPage().getAddFavoritesMessageText();
    }

    public SearchResultsPage searchTag(String tag) {
        return Search.searchTag(tag);
    }

    public ReportImproperModal getReportImproperPopUp() {
        return ReportImproperModal.getReportImproperPopUp();
    }

}
