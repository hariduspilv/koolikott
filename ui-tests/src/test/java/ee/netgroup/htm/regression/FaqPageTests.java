package ee.netgroup.htm.regression;

import ee.netgroup.htm.components.LeftMenu;
import ee.netgroup.htm.page.FaqPage;
import ee.netgroup.htm.page.LandingPage;
import ee.netgroup.htm.BaseTest;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

public class FaqPageTests extends BaseTest {

    private static final String englishQuestion = "Is this real life or is it just Fanta sea?";
    private static final String estonianQuestion = "Kus kulgeb Kuu?";

    @AfterMethod
    public void changePageLanguageToEstonian() {
        LandingPage.goToLandingPage()
                .clickChangePageLanguageBtn()
                .chooseEstonianLanguage();
    }

    @Test
    public void canDeleteFaqAfterCreating() {
        LandingPage.getLandingPage().chooseUserType("Admin");

        LeftMenu.getLeftMenu()
                .expandAboutMenu()
                .clickFaq()
                .enterEditMode()
                .clickOpenAddNewFaqSection()
                .addSpecificQuestionInEstonian(estonianQuestion)
                .addRandomEnglishFaq()
                .addRandomRussianFaq()
                .clickAddCreatedFaq();

        String addedEstonianQuestion = FaqPage.getFaqPage().getFirstFaqQuestion();

        String checkEstonianQuestion = FaqPage.getFaqPage()
                .hoverOverFirst()
                .editFirstExistingFaq()
                .clickDeleteFaqBlock()
                .clickConfirmDeleteFaq()
                .getFirstFaqQuestion();
        Assert.assertNotEquals(addedEstonianQuestion, checkEstonianQuestion);
    }

    @Test
    public void canViewNewFaqAsUserAfterAdminCreatedIt() {
        LandingPage.goToLandingPage().chooseUserType("Admin");

        LeftMenu.getLeftMenu()
                .expandAboutMenu()
                .clickFaq()
                .enterEditMode()
                .clickOpenAddNewFaqSection()
                .addRandomEstonianFaq()
                .addSpecificQuestionInEnglish(englishQuestion)
                .addRandomRussianFaq()
                .clickAddCreatedFaq();

        LandingPage.goToLandingPage()
                .chooseUserType("User")
                .clickChangePageLanguageBtn()
                .chooseEnglishLanguage();

        String doesAddedQuestionMatchUserViewQuestionInEnglish = LeftMenu
                .getLeftMenu()
                .expandAboutMenu()
                .clickFaq()
                .getFirstFaqQuestion();
        Assert.assertEquals(doesAddedQuestionMatchUserViewQuestionInEnglish, englishQuestion);

    }

    @Test
    public void canEditFaq() {
        LandingPage.goToLandingPage().chooseUserType("Admin");
        String editedFaqQuestion = LeftMenu.getLeftMenu()
                .expandAboutMenu()
                .clickFaq()
                .enterEditMode()
                .clickOpenAddNewFaqSection()
                .addRandomEstonianFaq()
                .addRandomEnglishFaq()
                .addRandomRussianFaq()
                .clickAddCreatedFaq()
                .hoverOverFirst()
                .editFirstExistingFaq()
                .editEstonianFaq(estonianQuestion)
                .clickUpdateFaq()
                .getFirstFaqQuestion();
        Assert.assertEquals(editedFaqQuestion, estonianQuestion);
    }
}
