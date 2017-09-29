package ee.netgroup.hm.tests;

import static ee.netgroup.hm.page.LandingPage.goToLandingPage;
import org.junit.Assert;
import org.junit.Test;

import ee.netgroup.hm.helpers.Constants;

public class SearchTests {
	
	@Test
	public void SearchTests_ByTagName_TagIsFound() {

		String tagText = goToLandingPage()
				.chooseUserType("Admin")
				.insertSearchCriteriaAndSearch("tag")
				.openSearchResultPortfolio()
				.getTagText();
		Assert.assertEquals(Constants.searchTag, tagText);
	}
	
	@Test
	public void SearchTests_ByPublisher_PublisherIsFound() {

		String publisherName = goToLandingPage()
				.chooseUserType("Admin")
				.insertSearchCriteriaAndSearch("publisher")
				.openSearchResultMaterial()
				.getPublisherName();
		Assert.assertEquals(Constants.searchPublisher, publisherName);
	}
	
	@Test
	public void SearchTests_ByMaterialTypeAudio_TypeIsAudio() {

		String materialType = goToLandingPage()
				.chooseUserType("Admin")
				.clickToOpenAdvancedSearch()
				.selectMaterialTypeAudio()
				.openSearchResultMaterial() 
				.getMaterialType();
		Assert.assertEquals("Heli", materialType);
	}
	
	@Test
	public void SearchTests_ByMaterialDescription_DescriptionIsFound() {

		String materialDescription = goToLandingPage()
				.chooseUserType("Admin")
				.insertSearchCriteriaWithAutocomplete("do not touch") 
				.openSearchResultMaterial()
				.getMaterialDescription();
		Assert.assertEquals("Performance test resource description. DO NOT TOUCH!!!!", materialDescription);
	}
	
	@Test
	public void SearchTests_ByMaterialLanguage_LanguageIsRussian() {

		String materialLanguage = goToLandingPage()
				.chooseUserType("User")
				.clickToOpenAdvancedSearch()
				.selectMaterialLanguageRussian()
				.insertSearchCriteriaInRussian("Катарина")
				.openSearchResultMaterial()
				.clickActionsMenu()
				.clickEditMaterial()
				.getMaterialLanguage();
		Assert.assertEquals("Vene", materialLanguage);
	}

}
