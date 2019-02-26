package ee.netgroup.htm.regression;

import ee.netgroup.htm.DriverConf;
import ee.netgroup.htm.components.AddMaterialsToPortfolioToolbar;
import ee.netgroup.htm.helpers.Constants;
import ee.netgroup.htm.page.MyMaterialsPage;
import ee.netgroup.htm.page.PortfolioPage;
import org.testng.annotations.Test;

import static ee.netgroup.htm.enums.VocationalEducationSubjectArea.AGRICULTURE;
import static ee.netgroup.htm.page.LandingPage.goToLandingPage;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class MaterialTests extends DriverConf {

    @Test
    public void CreateMaterial_AsUser_MaterialIsCreated() {
        String creatorName = goToLandingPage()
                .chooseUserType("Publisher")
                .clickAddMaterial()
                .setRandomHyperLink()
                .setTitle()
                .selectDefaultLicenseType()
                .setIsSelfAuthored()
                .selectPreschoolEducation()
                .selectSubjectAreaForPreschoolEducation("DOMAIN_ESTONIAN")
                .selectDefaultTargetGroup()
                .clickCreateMaterial()
                .getCreatorName();

        assertEquals(creatorName, "Autor: Publisher Publisher");
    }

    @Test
    public void DeleteMaterial_MaterialIsDeleted() {

        String deletedBannerText = goToLandingPage()
                .chooseUserType("Moderator")
                .clickMyMaterials()
                .openMaterial()
                .clickActionsMenu()
                .clickDeleteMaterial()
                .clickConfirmDeleteMaterial()
                .getDeletedBannerText();
        assertEquals(Constants.deletedBannerText, deletedBannerText);
    }

    @Test
    public void AddDeletedMaterial_ErrorMessageIsDisplayed() {

        String validationError = goToLandingPage()
                .chooseUserType("Publisher")
                .clickAddMaterial()
                .insertDeletedMaterialUrl()
                .getValidationError();
        assertEquals(validationError, "Selline link on kustutatud materjalide nimekirjas");
    }

    @Test
    public void AddExistingMaterial_ErrorMessageIsDisplayed() {

        String existingMaterial = goToLandingPage()
                .chooseUserType("Publisher")
                .clickAddMaterial()
                .insertExistingMaterialUrl()
                .getExistingMaterialValidationError();
        assertEquals(existingMaterial, "Selline materjal on juba olemas");
    }

    @Test
    public void AddMaterialToPortfolioFromMyMaterialsPage_MaterialIsAddedToPortfolio() {

        goToLandingPage()
                .chooseUserType("User")
                .clickAddPortfolio()
                .setTitle()
                .setDefaultLicenseType()
                .selectEducationalContext_Preschool()
                .selectSubjectAreaForPreschoolEducation("DOMAIN_ESTONIAN")
                .selectDefaultAgeGroup()
                .clickCreate()
                .clickSaveAndExitConfirmationControl()
                .clickMyMaterials()
                .clickToSelectMaterial();

        String materialTitle = MyMaterialsPage.getFirstMaterialTitleFromMaterialsList();

        AddMaterialsToPortfolioToolbar.selectPortfolio()
                .selectChapter()
                .clickDone();

        String materialTitle2 = PortfolioPage.getMaterialTitle();

        assertEquals(materialTitle2, materialTitle);
    }

    @Test
    public void AddToFavorites_MaterialIsInFavorites() {

        boolean starIsSelected = goToLandingPage()
                .chooseUserType("Moderator")
                .clickMyMaterials()
                .clickToSelectStar()
                .openMaterial()
                .starIsSelected();
        assertTrue(starIsSelected);
    }

    @Test
    public void RemoveFromFavorites_MaterialIsRemovedFromFavorites() {

        boolean starIsUnselected = goToLandingPage()
                .chooseUserType("Moderator")
                .goToMyFavorites()
                .openMaterial()
                .unselectStar()
                .starIsUnselected();
        assertTrue(starIsUnselected);
    }

    @Test
    public void AddTags_ShowMoreButtonIsDisplayed() {

        boolean showMoreButtonIsDisplayed = goToLandingPage()
                .chooseUserType("User")
                .clickMyMaterials()
                .openMaterial()
                .insertTags()
                .showMoreButtonIsDisplayed();
        assertTrue(showMoreButtonIsDisplayed);
    }

    @Test
    public void CreateMaterial_MaterialIsUnreviewedBannerIsDisplayed() {

        String unreviewedBannerText = goToLandingPage()
                .chooseUserType("Moderator")
                .clickAddMaterial()
                .setRandomHyperLink()
                .setTitle()
                .setDescription()
                .selectDefaultLicenseType()
                .selectVocationalEducation()
                .selectSubjectAreaForVocationEducation(AGRICULTURE.getTranslationKey())
                .setAuthorFirstName()
                .setAuthorSurName()
                .clickCreateMaterial()
                .getUnreviewedBannerText();
        assertEquals(Constants.unreviewedBannerText, unreviewedBannerText);
    }

    @Test
    public void ReportImproperTag_TagIsReported() {

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
    }

}
