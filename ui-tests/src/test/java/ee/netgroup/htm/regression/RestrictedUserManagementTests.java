package ee.netgroup.htm.regression;

import ee.netgroup.htm.api.EkoolikottApi;
import ee.netgroup.htm.components.LeftMenu;
import ee.netgroup.htm.page.LandingPage;
import ee.netgroup.htm.page.MyPortfoliosPage;
import ee.netgroup.htm.BaseTest;
import org.testng.annotations.Test;

import static ee.netgroup.htm.api.UserRole.USER;
import static org.testng.Assert.assertFalse;

public class RestrictedUserManagementTests extends BaseTest {

    private static final String USER_USERNAME = "peeter.paan";
    private static final String USER_ID_CODE = "38011550077";
    private static final String ADMIN_ID_CODE = "89898989898";

    @Test
    public void canNotCreateNewLearningObjectAfterChangingUserRoleToRestricted() {
        LandingPage.getLandingPage().loginUsingDev(ADMIN_ID_CODE);
        LeftMenu.getLeftMenu()
                .expandAdminMenu()
                .clickRestrictedUsers()
                .insertAndSelectUserName(USER_USERNAME)
                .clickOpen()
                .changeUserRoleToRestricted()
                .clickSave();

        LandingPage.getLandingPage().loginUsingDev(USER_ID_CODE);
        boolean isAddPortfolioButtonVisible = LeftMenu.getLeftMenu()
                .expandMyProfileMenu()
                .clickMyPortfolios()
                .isAddPortfolioButtonVisible();
        assertFalse(isAddPortfolioButtonVisible);
    }

    @Test
    public void canCreateNewLearningObjectAfterChangingUserRoleToUser(){
        EkoolikottApi.setUserRole(USER_ID_CODE, USER);

        LandingPage.goToLandingPage().loginUsingDev(USER_ID_CODE);
        MyPortfoliosPage.getMyPortfoliosPage()
                .isAddPortfolioButtonVisible();
    }

}
