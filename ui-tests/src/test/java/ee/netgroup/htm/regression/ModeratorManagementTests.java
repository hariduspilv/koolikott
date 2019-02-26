package ee.netgroup.htm.regression;

import ee.netgroup.htm.DriverConf;
import ee.netgroup.htm.api.EkoolikottApi;
import ee.netgroup.htm.components.LeftMenu;
import ee.netgroup.htm.page.LandingPage;
import ee.netgroup.htm.page.MyPortfoliosPage;
import ee.netgroup.htm.page.PortfolioPage;
import org.testng.annotations.Test;

import static ee.netgroup.htm.api.UserRole.MODERATOR;
import static ee.netgroup.htm.api.UserRole.USER;
import static ee.netgroup.htm.enums.VocationalEducationSubjectArea.ARTS;
import static ee.netgroup.htm.page.NewLearningObjectsPage.getNewLearningObjectsPage;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

public class ModeratorManagementTests extends DriverConf {

    private static final String USER_USERNAME = "peeter.paan";
    private static final String USER_ID_CODE = "38011550077";
    private static final String ADMIN_ID_CODE = "89898989898";

    @Test
    public void canReviewNewLearningObjectsAfterGrantingModeratorRights() {
        EkoolikottApi.setUserRole(USER_ID_CODE, USER);

        LandingPage
                .goToLandingPage()
                .loginUsingDev(ADMIN_ID_CODE);
        LeftMenu.getLeftMenu()
                .expandAdminMenu()
                .clickModerators()
                .insertAndSelectUserName(USER_USERNAME)
                .clickOpen()
                .changeUserRoleToModerator()
                .clickSave();

        LandingPage.getLandingPage().logoutIfLoggedIn();
        LandingPage.getLandingPage().loginUsingDev(USER_ID_CODE);

        LeftMenu
                .getLeftMenu()
                .expandMyProfileMenu()
                .clickNewLearningObjectsMenuItem();

        boolean isToolbarHidden = getNewLearningObjectsPage()
                .openFirstNewLearningObject_Portfolio()
                .markNewPortfolioAsReviewed()
                .isUnreviewedToolbarHidden();
        assertTrue(isToolbarHidden);
    }

    @Test
    public void canNotReviewNewLearningObjectsAfterRemovingModeratorRights() {
        EkoolikottApi.setUserRole(USER_ID_CODE, MODERATOR);

        LandingPage.goToLandingPage()
                .loginUsingDev(USER_ID_CODE);

        LeftMenu
                .getLeftMenu()
                .expandMyProfileMenu()
                .isNewLearningObjectsMenuItemVisible();

        LandingPage.getLandingPage().logoutIfLoggedIn();
        LandingPage.goToLandingPage()
                .loginUsingDev(ADMIN_ID_CODE);

        LeftMenu.getLeftMenu()
                .expandAdminMenu()
                .clickModerators()
                .insertAndSelectUserName(USER_USERNAME)
                .clickOpen()
                .changeUserRoleToUser()
                .clickSave();
        LandingPage.getLandingPage().logoutIfLoggedIn();
        LandingPage.getLandingPage().loginUsingDev(USER_ID_CODE);

        boolean doesNewLearningObjectsMenuItemExist = LeftMenu
                .getLeftMenu()
                .expandMyProfileMenu()
                .isNewLearningObjectsMenuItemVisible();
        assertFalse(doesNewLearningObjectsMenuItemExist);
    }

    @Test
    public void canReviewNewTaxonomyLearningObjectsAfterAddingNewTaxonomy() {
        EkoolikottApi.setUserRole(USER_ID_CODE, MODERATOR);
        LandingPage.goToLandingPage().loginUsingDev(ADMIN_ID_CODE);
        MyPortfoliosPage.getMyPortfoliosPage()
                .clickAddPortfolio()
                .setTitle("abc")
                .setDefaultLicenseType()
                .selectVocationalEducation()
                .selectSubjectAreaForVocationEducation(ARTS.getTranslationKey())
                .clickCreate()
                .clickSaveAndExitConfirmationControl();
        LeftMenu.getLeftMenu()
                .expandAdminMenu()
                .clickModerators()
                .insertAndSelectUserName(USER_USERNAME)
                .clickOpen()
                .clickToAddNewTaxon()
                .removeFirstTaxon()
                .addNewTaxon_Preschool()
                .addMovementSubjectAreaForPreschoolTaxon()
                .clickSave();

        LandingPage.goToLandingPage().logoutIfLoggedIn();
        LandingPage.getLandingPage().loginUsingDev(USER_ID_CODE);
        LeftMenu.getLeftMenu()
                .expandMyProfileMenu()
                .clickNewLearningObjectsMenuItem()
                .openFirstNewLearningObject_Portfolio()
                .checkThatPortfolioTitleDoesNotMatch("abc");

        LandingPage.goToLandingPage().logoutIfLoggedIn();
        LandingPage.goToLandingPage().loginUsingDev(ADMIN_ID_CODE);
        LeftMenu.getLeftMenu()
                .expandAdminMenu()
                .clickModerators()
                .insertAndSelectUserName(USER_USERNAME)
                .clickOpen()
                .clickToAddNewTaxon()
                .removeFirstTaxon()
                .addNewTaxon_VocationalEducation()
                .addArtsSubjectAreaforTaxonDomain()
                .clickSave();
        LandingPage.goToLandingPage().logoutIfLoggedIn();
        LandingPage.goToLandingPage().loginUsingDev(USER_ID_CODE);
        LeftMenu.getLeftMenu()
                .expandMyProfileMenu()
                .clickNewLearningObjectsMenuItem()
                .openFirstNewLearningObject_Portfolio()
                .checkIfPortfolioTitleMatches("abc");

        boolean isToolbarHidden = PortfolioPage.getPortfolioPage()
                .markNewPortfolioAsReviewed()
                .isUnreviewedToolbarHidden();
        assertTrue(isToolbarHidden);
    }

}


