package ee.netgroup.htm.regression;

import ee.netgroup.htm.DriverConf;
import ee.netgroup.htm.helpers.Constants;
import org.testng.annotations.Test;

import static ee.netgroup.htm.page.LandingPage.goToLandingPage;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.testng.Assert.assertEquals;

public class SearchTests extends DriverConf {

    @Test
    public void SearchTests_ByTagName_TagIsFound() {

        String tagText = goToLandingPage()
                .chooseUserType("Admin")
                .insertSearchCriteriaAndSearch("tag")
                .openSearchResultPortfolio()
                .getMatchingTagText("epic");
        assertEquals(tagText, Constants.searchTag); //finds the first one, but would need to go through the list
    }

    @Test
    public void SearchTests_ByPublisher_PublisherIsFound() {

        String publisherName = goToLandingPage()
                .chooseUserType("Admin")
                .insertSearchCriteriaAndSearch("publisher")
                .openSearchResultMaterial()
                .getMatchingPublisherName("Pegasus");
        assertThat(publisherName, containsString(Constants.searchPublisher));
    }

    @Test
    public void SearchTests_ByMaterialDescription_DescriptionIsFound() {

        String materialDescription = goToLandingPage()
                .chooseUserType("Admin")
                .insertSearchCriteriaWithAutocomplete("do not touch")
                .viewAllExactSearchResults_ByDescription_Materials()
                .openSearchResultMaterial()
                .getMaterialDescription();
        assertEquals("Performance test resource description. DO NOT TOUCH!!!!2", materialDescription);
    }

    @Test
    public void SearchTests_ByMaterialLanguage_LanguageIsRussian() {

        String materialLanguage = goToLandingPage()
                .chooseUserType("User")
                .clickToOpenAdvancedSearch()
                .selectMaterialLanguageRussian()
                .insertSearchCriteriaInRussian("Катарина")
                .viewAllSearchResults_ByTitle_Material()
                .openSearchResultMaterial()
                .clickActionsMenu()
                .clickEditMaterial()
                .getMaterialLanguage();
        assertEquals("Vene", materialLanguage);
    }

}
