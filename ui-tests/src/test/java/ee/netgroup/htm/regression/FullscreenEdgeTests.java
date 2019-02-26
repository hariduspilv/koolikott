package ee.netgroup.htm.regression;

import ee.netgroup.htm.DriverConf;
import ee.netgroup.htm.page.EditPortfolioPage;
import ee.netgroup.htm.page.LandingPage;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

import static com.codeborne.selenide.Selenide.switchTo;
import static ee.netgroup.htm.enums.VocationalEducationSubjectArea.ARTS;

public class FullscreenEdgeTests extends DriverConf {

    @AfterMethod
    public void switchBackToFirstTab() {
        switchTo().window(0);
    }

    @AfterClass
    public void cleanup() {
        EditPortfolioPage.getEditPortfolioPage().closeEditModeIfOpen();
        switchTo().window(1).close();
        switchTo().window(1).close();
        switchTo().window(0);
    }

    @Test
    public void clickingLinkInFSModeRedirectsToLinkNotInFSMode() {
        boolean isSimilarObjectsAsideVisible = LandingPage
                .goToLandingPage()
                .chooseUserType("Admin")
                .clickAddPortfolio()
                .setTitle()
                .setDefaultLicenseType()
                .selectVocationalEducation()
                .selectSubjectAreaForVocationEducation(ARTS.getTranslationKey())
                .clickCreate()
                .setMaterialLinkAsDescription()
                .clickSaveAndExitConfirmationControl()
                .clickFullscreenButton()
                .clickOnExternalLink()
                .canViewSearchField();
        Assert.assertTrue(isSimilarObjectsAsideVisible);
    }

    @Test
    public void clickingLinkInFSModeDoesNotCloseFSModeInCurrentTab() {
        boolean similarObjectsAsideIsVisible = LandingPage
                .goToLandingPage()
                .chooseUserType("Admin")
                .clickAddPortfolio()
                .setTitle()
                .setDefaultLicenseType()
                .selectVocationalEducation()
                .selectSubjectAreaForVocationEducation(ARTS.getTranslationKey())
                .clickCreate()
                .setMaterialLinkAsDescription()
                .clickSaveAndExitConfirmationControl()
                .clickFullscreenButton()
                .clickOnExternalLink(0)
                .canViewSearchField();
        Assert.assertFalse(similarObjectsAsideIsVisible);
    }

    @Test
    public void canNotViewFullscreenBtnOnEditPortfolioPage() {
        boolean isFSBtnVisible = LandingPage
                .goToLandingPage()
                .chooseUserType("Admin")
                .clickNewLearningObjectsInLeftMenu()
                .openFirstNewLearningObject_Portfolio()
                .clickActionsMenu()
                .clickEditPortfolio()
                .fsModeBtnIsVisible();
        Assert.assertFalse(isFSBtnVisible);
    }

    @Test
    public void navigatingBackToPreviousPageClosesFSMode() {
        boolean canViewSearchField = LandingPage
                .goToLandingPage()
                .chooseUserType("Admin")
                .clickAddPortfolio()
                .setTitle()
                .setDefaultLicenseType()
                .selectVocationalEducation()
                .selectSubjectAreaForVocationEducation(ARTS.getTranslationKey())
                .clickCreate()
                .clickSaveAndExitConfirmationControl()
                .clickFullscreenButton()
                .navigateBackToPreviousPage()
                .canViewSearchField();
        Assert.assertTrue(canViewSearchField);
    }

    @Test
    public void clickingSideNavHeaderDoesNotCloseFSMode() {
        boolean canViewSearchField = LandingPage
                .goToLandingPage()
                .chooseUserType("Admin")
                .clickNewLearningObjectsInLeftMenu()
                .openFirstNewLearningObject_Portfolio()
                .clickFullscreenButton()
                .clickOnSideNav_Header_Five_Times()
                .canViewSearchField();
        Assert.assertFalse(canViewSearchField);
    }

    @Test
    public void clickingSideNavFirstChapterDoesNotCloseFSMode() {
        boolean canViewSearchField = LandingPage
                .goToLandingPage()
                .chooseUserType("Admin")
                .clickNewLearningObjectsInLeftMenu()
                .openFirstNewLearningObject_Portfolio()
                .clickFullscreenButton()
                .clickOnSideNav_FirstChapter_Five_Times()
                .canViewSearchField();
        Assert.assertFalse(canViewSearchField);
    }

    @Test
    public void exitingFSModeReturnsBanner() {
        boolean bannerIsBack = LandingPage
                .goToLandingPage()
                .chooseUserType("Admin")
                .openPortfolio()
                .clickFullscreenButton()
                .clickFullscreenButton()
                .isLOBannerVisible();
        Assert.assertTrue(bannerIsBack);
    }

}
