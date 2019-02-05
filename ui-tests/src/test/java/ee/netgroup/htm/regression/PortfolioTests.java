package ee.netgroup.htm.regression;

import ee.netgroup.htm.helpers.Constants;
import ee.netgroup.htm.page.LandingPage;
import ee.netgroup.htm.page.MyPortfoliosPage;
import ee.netgroup.htm.page.PortfolioPage;
import ee.netgroup.htm.BaseTest;
import org.testng.annotations.Test;

import static ee.netgroup.htm.enums.VocationalEducationSubjectArea.AGRICULTURE;
import static ee.netgroup.htm.enums.VocationalEducationSubjectArea.ARTS;
import static ee.netgroup.htm.page.LandingPage.goToLandingPage;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class PortfolioTests extends BaseTest {

    @Test
    public void canDeletePortfolioAfterCreating() {
        LandingPage
                .goToLandingPage()
                .chooseUserType("Admin");

        MyPortfoliosPage.getMyPortfoliosPage()
                .clickAddPortfolio()
                .setTitle("test title")
                .setDefaultLicenseType()
                .selectVocationalEducation()
                .selectSubjectAreaForVocationEducation(AGRICULTURE.getTranslationKey())
                .clickCreate()
                .clickSaveAndExit()
                .clickExitAndMakePortfolioPublic();

        boolean portfolioIsDeletedAfterCreating = PortfolioPage.getPortfolioPage()
                .clickActionsMenu()
                .clickDeletePortfolio()
                .confirmDeletePortfolio()
                .isPortfolioDeletedToolbarVisible();
        assertTrue(portfolioIsDeletedAfterCreating);
    }

    @Test
    public void canCreatePortfolio() {

        String portfolioIsCreatedAlertText = goToLandingPage()
                .chooseUserType("Publisher")
                .clickAddPortfolio()
                .setTitle()
                .setDefaultLicenseType()
                .selectEducationalContext_Preschool()
                .selectSubjectAreaForPreschoolEducation("DOMAIN_ARTS")
                .selectDefaultAgeGroup()
                .setPortfolioIllustrationAuthor()
                .setPortfolioIllustrationSource()
                .setPortfolioIllustrationLicenseType()
                .setDescription()
                .clickCreate()
                .getSuccessAlertText();
        assertEquals("Alusta selle muutmisega (kliki siia) - lisa lõike, teksti, pilte, videosid, materjale e-koolikotist. Salvestamine toimub automaatselt. Jõudu tööle!", portfolioIsCreatedAlertText);
    }

    @Test
    public void canConfirmDeletionOfPortfolio() {

        String deletedPortfolioToast = goToLandingPage()
                .chooseUserType("User")
                .openPortfolio()
                .clickActionsMenu()
                .clickDeletePortfolio()
                .clickConfirm()
                .isPortfolioDeletedTextVisible();
        assertEquals("Kogumik kustutatud", deletedPortfolioToast);
    }

    @Test
    public void ShareWithLink_VisibilityIsChanged() {

        LandingPage.goToLandingPage()
                .chooseUserType("Publisher")
                .openPortfolio()
                .clickActionsMenu()
                .clickEditPortfolio()
                .clickVisibilityButton()
                .markPortfolioAsPrivate()
                .clickSaveAndExit()
                .clickExitAndStayPrivate();
        boolean privacyIsShareWithLink = goToLandingPage()
                .chooseUserType("Publisher")
                .openPortfolio()
                .clickActionsMenu()
                .clickEditPortfolio()
                .clickVisibilityButton()
                .selectShareWithLink()
                .clickSaveAndExit()
                .clickExitAndStayPrivate()
                .isPortfolioPrivacyShareOnlyWithLink();
        assertTrue(privacyIsShareWithLink);
    }

    @Test
    public void canMakeAdvancedEditingOnChapterDescription() {

        boolean descriptionIsQuoted = goToLandingPage()
                .chooseUserType("User")
                .clickAddPortfolio()
                .setTitle()
                .setDefaultLicenseType()
                .selectVocationalEducation()
                .selectSubjectAreaForVocationEducation(ARTS.getTranslationKey())
                .setDescription()
                .clickCreate()
                .setChapterTitle()
                .addDescription()
                .clickToSelectDescription()
                .clickToSelectBold()
                .clickToSelectItalic()
                .clickToSelectQuoteText()
                .clickSaveAndExitConfirmationControl()
                .isDescriptionQuoted();
        assertTrue(descriptionIsQuoted);
    }

    @Test
    public void canAddTagToPortfolio() {

        boolean tagIsAdded = goToLandingPage()
                .chooseUserType("User")
                .openPortfolio()
                .addNewTag()
                .isTagAddedToPortfolio();
        assertTrue(tagIsAdded);
    }

    @Test
    public void AddToRecommendations_PortfolioIsRecommended() {

        boolean addedToRecommendations = goToLandingPage()
                .chooseUserType("Admin")
                .insertSearchCriteriaAndSearch("recommended:false")
                .sortResultsNewestFirst()
                .openSearchResultPortfolio()
                .clickActionsMenu()
                .addToRecommendationsList()
                .clickActionsMenu()
                .isRemoveFromRecommendationsDisplayed();
        assertTrue(addedToRecommendations);
    }

    @Test
    public void RemoveFromRecommendations_PortfolioIsRemovedFromRecommendations() {

        boolean removedFromRecommendations = goToLandingPage()
                .chooseUserType("Admin")
                .insertSearchCriteriaAndSearch("recommended:true")
                .openSearchResultPortfolio()
                .clickActionsMenu()
                .removeFromRecommendationsList()
                .clickActionsMenu()
                .isAddToRecommendationsDisplayed();
        assertTrue(removedFromRecommendations);
    }

    @Test
    public void AddSystemTag_LoChangedBannerIsDisplayed() { // LO=Learning Object

        String changedLOBannerText = goToLandingPage()
                .chooseUserType("Admin")
                .clickAddPortfolio()
                .setTitle()
                .setDefaultLicenseType()
                .selectEducationalContext_Preschool()
                .selectSubjectAreaForPreschoolEducation("DOMAIN_MOVEMENT")
                .selectDefaultAgeGroup()
                .clickCreate()
                .clickSaveAndExit()
                .clickExitAndMakePortfolioPublic()
                .markNewPortfolioAsReviewed()
                .addNewSystemTag()
                .getChangedLOBannerText();
        assertEquals(changedLOBannerText, Constants.changedLOBannerText);
    }

    @Test
    public void NarrowChapterBlock_WidthIsNarrow() {

        boolean chapterWidth = goToLandingPage()
                .chooseUserType("User")
                .clickAddPortfolio()
                .setTitle()
                .setDefaultLicenseType()
                .selectEducationalContext_Preschool()
                .selectSubjectAreaForPreschoolEducation("DOMAIN_MOVEMENT")
                .selectDefaultAgeGroup()
                .setDescription()
                .clickCreate()
                .setChapterTitle()
                .addDescription()
                .clickChangeChapterWidth()
                .clickSaveAndExitConfirmationControl()
                .isChapterNarrow();
        assertTrue(chapterWidth);
    }

    @Test
    public void AddSubchapter_SubchapterIsAdded() {

        boolean subchapter = goToLandingPage()
                .chooseUserType("User")
                .clickAddPortfolio()
                .setTitle()
                .setDefaultLicenseType()
                .selectEducationalContext_Preschool()
                .selectSubjectAreaForPreschoolEducation("DOMAIN_MATHEMATICS")
                .selectDefaultAgeGroup()
                .setDescription()
                .clickCreate()
                .setChapterTitle()
                .addSubchapterTitle()
                .clickToSelectDescription()
                .setSubchapterTitleH3()
                .clickSaveAndExitConfirmationControl()
                .isSubchapterDisplayed();
        assertTrue(subchapter);
    }

    @Test
    public void ChangeChapterOrder_OrderIsChanged() {

        String firstChapterTitle = goToLandingPage()
                .chooseUserType("User")
                .clickAddPortfolio()
                .setTitle()
                .setDefaultLicenseType()
                .selectEducationalContext_Preschool()
                .selectSubjectAreaForPreschoolEducation("DOMAIN_MATHEMATICS")
                .selectDefaultAgeGroup()
                .setDescription()
                .clickCreate()
                .setSpecificChapterTitle("1")
                .addNewChapter()
                .setSecondChapterTitle("2")
                .clickMoveChapterDown()
                .clickSaveAndExitConfirmationControl()
                .getFirstChapterTitle();
        assertEquals(firstChapterTitle, "2");
    }

    @Test
    public void AddMediaFile_MediaFileIsAddedToChapter() {

        boolean newMediaFile = goToLandingPage()
                .chooseUserType("User")
                .clickAddPortfolio()
                .setTitle()
                .setDefaultLicenseType()
                .selectEducationalContext_Preschool()
                .selectSubjectAreaForPreschoolEducation("DOMAIN_MATHEMATICS")
                .selectDefaultAgeGroup()
                .setDescription()
                .clickCreate()
                .setChapterTitle()
                .addDescription()
                .clickAddNewMedia()
                .setMediaLink()
                .setMediaTitle()
                .setMediaAuthor()
                .setMediaSource()
                .setMediaLicenseType()
                .clickAddMedia()
                .clickSaveAndExitConfirmationControl()
                .isMediaFileAddedToChapter();
        assertTrue(newMediaFile);
    }

    @Test
    public void AlignRight_MaterialIsAligned() {

        boolean alignRight = goToLandingPage()
                .chooseUserType("User")
                .clickAddPortfolio()
                .setTitle()
                .setDefaultLicenseType()
                .selectEducationalContext_Preschool()
                .selectSubjectAreaForPreschoolEducation("DOMAIN_MOVEMENT")
                .selectDefaultAgeGroup()
                .setDescription()
                .clickCreate()
                .setChapterTitle()
                .addDescription()
                .clickAddExistingMaterial()
                .selectAllEducationalContexts()
                .insertMaterialSearchCriteria()
                .closeDetailedSearch()
                .viewAllSearchResults_ByTitle_Material()
                .clickToSelectMaterial2()
                .clickAddMaterialToPortfolio()
                .clickAlignRight()
                .clickSaveAndExitConfirmationControl()
                .isMaterialAligned();
        assertTrue(alignRight);
    }

    @Test
    public void CreateNewMaterial_MaterialIsAddedToPortfolio() {

        boolean isMaterialBoxDisplayed = goToLandingPage()
                .chooseUserType("User")
                .clickAddPortfolio()
                .setTitle()
                .setDefaultLicenseType()
                .selectEducationalContext_Preschool()
                .selectSubjectAreaForPreschoolEducation("DOMAIN_ESTONIAN")
                .selectDefaultAgeGroup()
                .setDescription()
                .clickCreate()
                .setChapterTitle()
                .clickAddNewMaterial()
                .setRandomHyperLink()
                .setTitle()
                .setDescription()
                .selectDefaultLicenseType()
                .setAuthorFirstName()
                .setAuthorSurName()
                .setPublisherName()
                .clickCreateMaterialPortfolio()
                .clickSaveAndExit()
                .clickExitAndMakePortfolioPublic()
                .isMaterialBoxDisplayed();
        assertTrue(isMaterialBoxDisplayed);
    }

    @Test
    public void AddExistingMaterial_MaterialIsAddedToPortfolio() {

        boolean isMaterialBoxDisplayed = goToLandingPage()
                .chooseUserType("Admin")
                .openPortfolio()
                .clickActionsMenu()
                .clickEditPortfolio()
                .setChapterTitle()
                .clickAddExistingMaterial()
                .selectAllEducationalContexts()
                .insertMaterialSearchCriteria()
                .closeDetailedSearch()
                .viewAllSearchResults_ByTitle_Material()
                .clickToSelectMaterial2()
                .clickAddMaterialToPortfolio()
                .clickSaveAndExitConfirmationControl()
                .isMaterialBoxDisplayed();
        assertTrue(isMaterialBoxDisplayed);
    }

    @Test
    public void AddPreferredMaterial_MaterialIsAddedToPortfolio() {

        boolean isMaterialBoxDisplayed = goToLandingPage()
                .chooseUserType("Admin")
                .clickAddPortfolio()
                .setTitle()
                .setDefaultLicenseType()
                .selectEducationalContext_Preschool()
                .selectSubjectAreaForPreschoolEducation("DOMAIN_ARTS")
                .selectDefaultAgeGroup()
                .setDescription()
                .clickCreate()
                .setChapterTitle()
                .clickAddPreferredMaterial()
                .selectAllEducationalContexts()
                .insertMaterialSearchCriteria()
                .closeDetailedSearch()
                .viewAllSearchResults_ByTitle_Material()
                .clickToSelectMaterial2()
                .clickAddMaterialToPortfolio()
                .clickSaveAndExitConfirmationControl()
                .isMaterialBoxDisplayed();
        assertTrue(isMaterialBoxDisplayed);
    }

    @Test
    public void ChangeVisibilityInDetailview_VisibilityIsChanged() {

        String portfolioPublicText = goToLandingPage()
                .chooseUserType("User")
                .openPrivatePortfolio()
                .changeVisibility()
                .setVisibilityToPublic()
                .getPublicPortfolioText();
        assertEquals("Kõik muutused on nüüd avalikult nähtavad", portfolioPublicText);
    }

}
