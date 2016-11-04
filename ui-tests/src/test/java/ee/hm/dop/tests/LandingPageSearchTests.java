package ee.hm.dop.tests;

import org.junit.Assert;

import static ee.hm.dop.page.LandingPage.goToLandingPage;

import org.junit.Test;


public class LandingPageSearchTests {


	@Test
	public void searchByTagName() {

		String tagText = goToLandingPage()
				.chooseUserType("Admin")
				.getLeftMenu()
				.clickMyPortfolios()
				.getSimpleSearch()
				.insertSearchCriteriaAndSearch("tag:epic")
				.openSearchResultPortfolio()
				.getTagText();

		Assert.assertEquals("epic", tagText);

	}
	
	@Test
	public void searchByPublisher() {

		String publisherName = goToLandingPage()
				.chooseUserType("Admin")
				.getLeftMenu()
				.clickMyPortfolios()
				.getSimpleSearch()
				.insertSearchCriteriaAndSearch("publisher:Pegasus")
				.openSearchResultMaterial()
				.getPublisherName();

		Assert.assertEquals("Pegasus", publisherName);

	}
	
	@Test
	public void searchByMaterialType() {

		String materialType = goToLandingPage()
				.chooseUserType("Admin")
				.getLogo()
				.clickLogo()
				.getAdvancedSearch()
				.clickToOpenAdvancedSearch()
				.selectMaterialType()
				.getLandingPage()
				.openSearchResultMaterial() 
				.getMaterialType();

		Assert.assertEquals("Heli", materialType);

	}
	
	@Test
	public void searchAsYouTypeByMaterialDescription() {

		String materialDescription = goToLandingPage()
				.chooseUserType("Admin")
				.getLogo()
				.clickLogo()
				.getSimpleSearch()
				.insertSearchCriteriaWithAutocomplete() 
				.openSearchResultMaterial()
				.getMaterialDescription();

		Assert.assertEquals("Performance test resource description. DO NOT TOUCH!!!!", materialDescription);

	}
	
	@Test
	public void searchByMaterialLanguage() {

		String materialLanguage = goToLandingPage()
				.chooseUserType("User")
				.getFabButton()
				.moveCursorToAddPortfolio()
				.moveCursorToAddMaterial()
				.clickAddMaterial()
				.uploadFile()
				.setMaterialTitleInRussian()
				.clickToSelectMaterialLanguage() 
				.addDescriptionInRussian()
				.clickNextStep()
				.selectTargetGroup()
				.clickNextStep()
				.insertAuthorsName()
				.insertAuthorsSurname()
				.clickAddMaterial()
				.getLogo()
				.clickLogo()
				.getAdvancedSearch()
				.clickToOpenAdvancedSearch()
				.selectMaterialLanguage()
				.getSimpleSearch()
				.insertSearchCriteriaInRussian()
				.getLandingPage()
				.sortMaterial()
				.openSearchResultMaterial()
				.selectActionFromMenu()
				.clickEditMaterial()
				.getMaterialLanguage();

		Assert.assertEquals("Vene", materialLanguage);

	}

}




