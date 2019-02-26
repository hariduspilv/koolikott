package ee.netgroup.htm.regression;

import ee.netgroup.htm.DriverConf;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static ee.netgroup.htm.components.FabButton.getFabButton;
import static ee.netgroup.htm.components.LeftMenu.getLeftMenu;
import static ee.netgroup.htm.enums.VocationalEducationSubjectArea.ARTS;
import static ee.netgroup.htm.enums.VocationalEducationSubjectArea.BUSINESS_AND_ADMINISTRATION;
import static ee.netgroup.htm.page.LandingPage.getLandingPage;
import static ee.netgroup.htm.page.LandingPage.goToLandingPage;
import static ee.netgroup.htm.page.MaterialPage.getMaterialPage;
import static ee.netgroup.htm.page.SearchResultsPage.getSearchResultsPage;
import static org.testng.Assert.assertTrue;

public class VocationalEducationTests extends DriverConf {

    @BeforeClass
    public void setupLoginAsUser() {
        goToLandingPage().chooseUserType("User");
    }

    @BeforeMethod
    public void goBackToFrontPage() {
        getLandingPage().clickOnPageLogo();
    }

    @Test(dataProvider = "SubjectAreas", description = "Create material with one of the vocational education context.")
    public void canCreateMaterialWithVocationalEducation(String subjectAreaTranslationKey, String subjectAreaName) {
        // using one of the educational context create material
        getFabButton()
                .clickAddMaterial()
                .setRandomHyperLink()
                .selectDefaultLicenseType()
                .setTitle()
                .setIsSelfAuthored()
                .selectVocationalEducation()
                .selectSubjectAreaForVocationEducation(subjectAreaTranslationKey)
                .clickCreateMaterial();

        String materialTitle = getMaterialPage().getMaterialTitle();

        goToLandingPage();

        // navigate to the same educational context in the left menu
        getLeftMenu()
                .expandTableOfContentsIfNotExpanded()
                .expandVocationalEducationIfNotExpanded()
                .selectDomain(subjectAreaTranslationKey);

        // open the material
        getSearchResultsPage()
                .sortResultsNewestFirst()
                .openLearningObjectWithTitle(materialTitle);

        // assert that educational level and field are correct
        assertTrue(getMaterialPage().hasSubjectArea(subjectAreaName));
    }

    @DataProvider(name = "SubjectAreas")
    private Object[][] vocationalEducationSubjectAreas() {
        return new Object[][]{
                {ARTS.getTranslationKey(), ARTS.getText()},
                {BUSINESS_AND_ADMINISTRATION.getTranslationKey(), BUSINESS_AND_ADMINISTRATION.getText()}
        };
    }

}
