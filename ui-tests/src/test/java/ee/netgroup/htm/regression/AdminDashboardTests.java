package ee.netgroup.htm.regression;

import ee.netgroup.htm.helpers.Constants;
import ee.netgroup.htm.BaseTest;
import org.testng.annotations.Test;

import static ee.netgroup.htm.page.LandingPage.goToLandingPage;
import static ee.netgroup.htm.page.MaterialPage.getMaterialPage;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class AdminDashboardTests extends BaseTest {

    @Test
    public void LearningObjectReportedByUser_CanBeMarkedAsProper_ByModerator() {
        String improperTagIsReported = goToLandingPage()
                .chooseUserType("User")
                .clickMyMaterials()
                .openMaterial()
                .addNewTag()
                .reportImproperTag()
                .setImproperDescription()
                .clickNotifyMaterial()
                .getNotificationIsSentText();
        assertEquals(Constants.reportedText, improperTagIsReported);

        boolean improperContentBannerIsHidden = goToLandingPage()
                .chooseUserType("Moderator")
                .clickImproperLearningObjects()
                .openImproperLearningObject()
                .markContentAsNotImproper()
                .isBannerToolbarHidden();
        assertTrue(improperContentBannerIsHidden);
    }

    @Test
    public void LearningObjectReportedByUser_CanBeMarkedAsProper_ByAdministrator() {

        String improperTagIsReported = goToLandingPage()
                .chooseUserType("User")
                .clickMyMaterials()
                .openMaterial()
                .addNewTag()
                .reportImproperTag()
                .setImproperDescription()
                .clickNotifyMaterial()
                .getNotificationIsSentText();
        assertEquals(Constants.reportedText, improperTagIsReported);

        boolean improperContentBannerIsHidden = goToLandingPage()
                .chooseUserType("Admin")
                .clickImproperLearningObjects()
                .openImproperLearningObject()
                .markContentAsNotImproper()
                .isBannerToolbarHidden();
        assertTrue(improperContentBannerIsHidden);
    }

    @Test
    public void NewLearningObjectCanBeMarkedAsReviewed_ByAdministrator() {
        goToLandingPage()
                .chooseUserType("Admin")
                .clickNewLearningObjectsInLeftMenu()
                .openFirstNewLearningObject();

        boolean unreviwedBannerIsHidden = getMaterialPage()
                .markAsReviewed()
                .isBannerToolbarHidden();

        assertTrue(unreviwedBannerIsHidden);
    }

    @Test(enabled = false)
    public void RestoreDeletedObject_LearningObjectIsRestored() {
        boolean deletedBannerIsHidden = goToLandingPage()
                .chooseUserType("Admin")
                .clickDeletedLearningObjects()
                .openDeletedLearningObject()
                .restoreDeletedLearningObject()
                .isBannerToolbarHidden();
        assertTrue(deletedBannerIsHidden);
    }

}
