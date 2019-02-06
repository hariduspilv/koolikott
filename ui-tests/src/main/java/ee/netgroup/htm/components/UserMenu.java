package ee.netgroup.htm.components;

import ee.netgroup.htm.helpers.Helpers;
import ee.netgroup.htm.page.MaterialPage;
import ee.netgroup.htm.page.PortfolioPage;
import ee.netgroup.htm.page.SearchResultsPage;
import org.openqa.selenium.By;

import static ee.netgroup.htm.components.LeftMenu.getLeftMenu;
import static ee.netgroup.htm.page.LandingPage.getLandingPage;
import static ee.netgroup.htm.util.JSWaiter.waitForJQueryAndAngularToFinishRendering;

public class UserMenu {

    private static By loginButton = By.id("header-login-icon");
    private static By userMenuIcon = By.id("header-user-menu-icon");
    private static By userMenuLogout = By.id("user-menu-logout");
    private static By userTour = By.xpath("//body[@class='tour-modal-is-showing md-dialog-is-showing']");
    private static By closeTour = By.xpath("//button[@id='tour-cancel-button']");
    private By currentUser = By.cssSelector("a#user-menu-dashboard span");

    public UserMenu() {
        waitForJQueryAndAngularToFinishRendering();
    }

    public static UserMenu logoutIfLoggedIn() {
        boolean loginButtonIsDisplayed = false;
        try {
            Helpers.waitForVisibility(loginButton).isDisplayed();
            loginButtonIsDisplayed = true;
        } catch (Exception ignored) {
            //ignored
        }

        if (!loginButtonIsDisplayed) {
            Helpers.waitForClickable(userMenuIcon).click();
            Helpers.waitForClickable(userMenuLogout).click();
        }

        return new UserMenu();
    }

    public static UserMenu clickProfileIcon() {

        if (Helpers.elementExists(userTour)) {
            Helpers.waitForClickable(closeTour).click();
        }

        Helpers.waitForClickable(userMenuIcon).click();
        return new UserMenu();
    }

    public static boolean isLoggedIn() {
        try {
            Helpers.waitForVisibility(userMenuIcon);
            return true;
        } catch (Exception ignored) {
            // exception ignored
            return false;
        }
    }

    public static UserMenu getUserMenu() {
        return new UserMenu();
    }

    public LoginModal clickLogin() {
        Helpers.waitForClickable(loginButton).click();
        return new LoginModal();
    }

    public String getUserName() {
        return Helpers.waitForVisibility(currentUser).getText();
    }

    public LeftMenu clickMyThings() {
        return getLeftMenu().expandMyProfileMenu();
    }

    public PortfolioPage openFirstPortfolio() {
        return getLandingPage().openFirstPortfolio();
    }

    public MaterialPage openMaterialUrl() {
        return getLandingPage().openMaterialUrl();
    }

    public SearchResultsPage searchTag(String tag) {
        return Search.searchTag(tag);
    }

    public MaterialPage openMaterial() {
        return getLandingPage().openFirstMaterial();
    }


}
