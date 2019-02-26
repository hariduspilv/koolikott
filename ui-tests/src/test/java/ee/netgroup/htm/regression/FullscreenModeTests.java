package ee.netgroup.htm.regression;

import ee.netgroup.htm.DriverConf;
import ee.netgroup.htm.components.FabButton;
import ee.netgroup.htm.framework.listeners.SeleniumTestListener;
import ee.netgroup.htm.page.LandingPage;
import ee.netgroup.htm.page.LearningObjectPage;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

@Listeners({SeleniumTestListener.class})
public class FullscreenModeTests extends DriverConf {

    @BeforeClass
    public void setup() {
        LandingPage.goToLandingPage()
                .chooseUserType("Moderator")
                .clickNewLearningObjectsInLeftMenu()
                .openFirstNewLearningObject()
                .clickFullscreenButton();
    }

    @AfterClass
    public void fullscreenModeTestsCleanup() {
        LearningObjectPage
                .getLearningObjectPage()
                .closeFSModeIfOpen();
    }

    @Test
    public void bannerIsHiddenInFSMode() {
        boolean isLOBannerVisible = LearningObjectPage
                .getLearningObjectPage()
                .isLOBannerVisible();
        Assert.assertFalse(isLOBannerVisible);
    }

    @Test
    public void isQRButtonHiddenInFSMode() {
        boolean isQrBtnVisible = LearningObjectPage
                .getLearningObjectPage()
                .isQRBtnVisible();
        Assert.assertFalse(isQrBtnVisible);
    }

    @Test
    public void isActionsMenuButtonHiddenInFSMode() {
        boolean isActionsMenuBtnVisible = LearningObjectPage
                .getLearningObjectPage()
                .isActionsMenuBtnVisible();
        Assert.assertFalse(isActionsMenuBtnVisible);
    }

    @Test
    public void canNotViewSimilarObjectsAsideInFSMode() {
        boolean canViewSimilarObjectsAside = LearningObjectPage
                .getLearningObjectPage()
                .canViewSimilarObjectsAside();
        Assert.assertFalse(canViewSimilarObjectsAside);
    }

    @Test
    public void canNotViewRecommendedObjectsAsideInFSMode() {
        boolean canViewRecommendedObjectsAside = LearningObjectPage
                .getLearningObjectPage()
                .canViewRecommendedObjectsAside();
        Assert.assertFalse(canViewRecommendedObjectsAside);
    }

    @Test
    public void canNotGetCustomerSupportInFSMode() {
        boolean canGetCustomerSupport = LearningObjectPage
                .getLearningObjectPage()
                .canGetCustomerSupport();
        Assert.assertFalse(canGetCustomerSupport);
    }

    @Test
    public void searchFieldIsHiddenInFSMode() {
        boolean isSearchFieldVisible = LearningObjectPage
                .getLearningObjectPage()
                .canViewSearchField();
        Assert.assertFalse(isSearchFieldVisible);
    }

    @Test
    public void canNotAddNewLearningObjectInFSMode() {
        boolean isAddPortfolioBtnVisible = FabButton
                .getFabButton()
                .isAddPortfolioButtonVisible();
        Assert.assertFalse(isAddPortfolioBtnVisible);
    }

}
