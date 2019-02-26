package ee.netgroup.htm.regression;

import ee.netgroup.htm.DriverConf;
import ee.netgroup.htm.components.LeftMenu;
import org.testng.annotations.Test;

import static ee.netgroup.htm.components.LeftMenu.getLeftMenu;
import static ee.netgroup.htm.page.LandingPage.goToLandingPage;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;

public class NewUserTests extends DriverConf {
    private static final String NEW_USER_DEV_LOGIN_IDCODE = "6456456";

    @Test
    public void AsNewUser_OpeningMyPortfoliosPage_ShouldDisplayAddYourFirstPortfolioMessage() {
        goToLandingPage()
                .loginUsingDev(NEW_USER_DEV_LOGIN_IDCODE);

        String addPortfolioMessage = getLeftMenu()
                .expandMyProfileMenu()
                .clickMyPortfolios()
                .getAddPortfolioMessageText();
        assertThat(addPortfolioMessage, containsString("Kogumiku lisamiseks"));
    }

    @Test
    public void AsNewUser_OpeningMyMaterialsPage_ShouldDisplayAddYourFirstMaterialMessage() {
        goToLandingPage()
                .loginUsingDev(NEW_USER_DEV_LOGIN_IDCODE);

        String addMaterialMessage = getLeftMenu()
                .expandMyProfileMenu()
                .clickMyMaterials()
                .getAddMaterialMessageText();
        assertThat(addMaterialMessage, containsString("Materjali lisamiseks"));
    }

    @Test
    public void AsNewUser_OpeningMyFavoritesPage_ShouldDisplayNoFavoritesYetMessage() {
        goToLandingPage()
                .loginUsingDev(NEW_USER_DEV_LOGIN_IDCODE);

        String addFavoritesMessage = LeftMenu.getLeftMenu()
                .expandMyProfileMenu()
                .clickMyFavorites()
                .getAddFavoritesMessageText();
        assertThat(addFavoritesMessage, containsString("Sul ei ole veel lemmikuid"));
    }
}
