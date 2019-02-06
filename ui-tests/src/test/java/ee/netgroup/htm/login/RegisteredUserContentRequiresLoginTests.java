package ee.netgroup.htm.login;

import com.codeborne.selenide.Configuration;
import ee.netgroup.htm.components.LoginModal;
import ee.netgroup.htm.helpers.Constants;
import ee.netgroup.htm.page.LandingPage;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static com.codeborne.selenide.Selenide.open;
import static ee.netgroup.htm.components.LeftMenu.getLeftMenu;
import static ee.netgroup.htm.components.LoginModal.getLoginModal;
import static ee.netgroup.htm.helpers.Constants.EKOOLIKOTT_HOME_PAGE_URL;
import static ee.netgroup.htm.page.LandingPage.getLandingPage;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class RegisteredUserContentRequiresLoginTests {

    @BeforeClass
    public void navigateToFrontPage() {
        Configuration.startMaximized = true;
        Configuration.browser = "chrome";

        open(EKOOLIKOTT_HOME_PAGE_URL);
    }

    @AfterMethod
    public void closeLoginModal() {
        getLoginModal().closeModal();
    }

    @Test
    public void loginIsRequiredToReportPortfolio() {
        getLandingPage().clickOnPageLogo();
        String loggingInIsRequired = getLandingPage()
                .openFirstPortfolio()
                .clickActionsMenu()
                .clickReportImproperContent()
                .getLogInIsRequiredText();

        assertEquals(Constants.logInModalTitleText, loggingInIsRequired);
    }

    @Test
    public void loginIsRequiredToReportMaterial() {
        LandingPage.getLandingPage().logoutIfLoggedIn();
        getLandingPage().clickOnPageLogo();
        String loggingInIsRequired = getLandingPage()
                .openFirstMaterial()
                .clickActionsMenu()
                .clickReportImproperContent()
                .getLogInIsRequiredText();
        assertEquals(Constants.logInModalTitleText, loggingInIsRequired);
    }

    @Test
    public void loginIsRequiredToOpenMyMaterialsPage() {
        LandingPage.getLandingPage().logoutIfLoggedIn();
        getLeftMenu()
                .expandMyProfileMenu()
                .clickMyMaterials();

        assertTrue(LoginModal.isDisplayed());
    }

    @Test
    public void loginIsRequiredToOpenMyFavoritesPage() {
        LandingPage.getLandingPage().logoutIfLoggedIn();
        getLeftMenu()
                .expandMyProfileMenu()
                .clickMyFavorites();

        assertTrue(LoginModal.isDisplayed());
    }

    @Test
    public void loginIsRequiredToOpenMyPortfolioPage() {
        LandingPage.getLandingPage().logoutIfLoggedIn();
        getLeftMenu()
                .expandMyProfileMenu()
                .clickMyPortfolios();

        assertTrue(LoginModal.isDisplayed());
    }

}
