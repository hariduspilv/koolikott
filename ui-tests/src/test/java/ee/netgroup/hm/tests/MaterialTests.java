package ee.netgroup.hm.tests;

import static ee.netgroup.hm.page.LandingPage.goToLandingPage;
import static org.junit.Assert.*;
import org.junit.Assert;
import org.junit.Test;
import ee.netgroup.hm.helpers.Constants;
import ee.netgroup.hm.page.MaterialPage;

public class MaterialTests {
	
	@Test
	public void MaterialTests_CreateMaterial_AsPublisher_MaterialIsCreated() {

		String creatorName = goToLandingPage()
				.chooseUserType("Publisher")
				.clickAddMaterial()
				.setHyperLink()
				.setMaterialTitle()
				.uploadPhoto()
				.addDescription()
				.clickNextStep()
				.selectEducation()
				.selectSubjectArea()
				.selectTargetGroup()
				.clickCreateMaterial()
				.getCreatorName();
		Assert.assertEquals("Lisaja: Publisher Publisher", creatorName);
	}
	
	@Test
	public void MaterialTests_DeleteMaterial_MaterialIsDeleted() {

		boolean deletedMaterialBanner = goToLandingPage()
				.chooseUserType("Admin")
				.clickMyMaterials()
				.openMaterial()
				.clickActionsMenu()
				.clickDeleteMaterial()
				.clickConfirmDeleteMaterial()
				.isMaterialDeletedBannerVisible();
		assertTrue(deletedMaterialBanner);
	}
	
	@Test
	public void MaterialTests_AddDeletedMaterial_ErrorMessageIsDisplayed() {

		String validationError = goToLandingPage()
				.chooseUserType("Publisher")
				.clickAddMaterial()
				.insertDeletedMaterialUrl()
				.getValidationError();
		Assert.assertEquals("Selline link on kustutatud materjalide nimekirjas", validationError);
	}
	
	@Test
	public void MaterialTests_AddExistingMaterial_ErrorMessageIsDisplayed() {

		String existingMaterial = goToLandingPage()
				.chooseUserType("Publisher")
				.clickAddMaterial()
				.insertExistingMaterialUrl()
				.getExistingMaterialValidationError();
		Assert.assertEquals("Selline materjal on juba olemas", existingMaterial);
	}

	@Test
	public void MaterialTests_CreateNewMaterial_FromPortfolioEditPage_MaterialIsAddedToPortfolio() {

		boolean isMaterialBoxDisplayed = goToLandingPage()
				.chooseUserType("User")
				.clickAddPortfolio()
				.setPortfolioTitle()
				.selectEducationalContext()
				.selectSubjectArea()
				.selectAgeGroup()
				.addDescription()
				.clickCreatePortfolio()
				.setChapterTitle()
				.clickAddMaterial()
				.clickAddNewMaterial()
				.setHyperLink()
				.setMaterialTitle()
				.addDescription()
				.clickNextStep()
				.clickNextStep()
				.setAuthorFirstName()
				.setAuthorSurName()
				.setPublisherName()
				.clickCreateMaterialPortfolio()
				.clickSaveAndExit()
				.makePortfolioPublic()
				.isMaterialBoxDisplayed();
		assertTrue(isMaterialBoxDisplayed);
	}
	
	@Test
	public void MaterialTests_AddExistingMaterial_FromPortfolioEditPage_MaterialIsAddedToPortfolio() { 

		boolean isMaterialBoxDisplayed = goToLandingPage()
				.chooseUserType("Admin")
				.openPortfolio()
				.clickActionsMenu()
				.clickEditPortfolio()
				.setChapterTitle()
				.clickAddMaterial()
				.clickAddExistingMaterial()
				.selectAllEducationalContexts()
				.insertMaterialSearchCriteria()
				.closeDetailedSearch()
				.clickToSelectMaterial2()
				.clickAddMaterialToPortfolio()
				.clickSaveAndExitConfirmationControl()
				.isMaterialBoxDisplayed();
		assertTrue(isMaterialBoxDisplayed);
	}
	

	@Test
	public void MaterialTests_AddExistingMaterial_FromMyMaterialsPage_MaterialIsAddedToPortfolio() {

		String successMessage = goToLandingPage()
				.chooseUserType("Publisher")
				.clickMyMaterials()
				.clickToSelectMaterial()
				.selectPortfolio()
				.selectChapter()
				.clickDone()
				.getSuccessMessage();
		Assert.assertEquals("Materjal(id) edukalt lisatud", successMessage);
	}
	
	@Test
	public void MaterialTests_MarkAsSuggested_MaterialIsSuggested() {

		String likesNumber = goToLandingPage()
				.chooseUserType("Publisher")
				.clickMyMaterials()
				.openMaterial()
				.likeMaterial()
				.getLikesNumber();
		Assert.assertEquals("1", likesNumber);
	}
	
	@Test
	public void MaterialTests_AddTag_TagIsAddedToMaterial() {

		MaterialPage tagsAmount = goToLandingPage()	
				.chooseUserType("Publisher")
				.clickAddMaterial()
				.setHyperLink()
				.setMaterialTitle()
				.addDescription()
				.clickNextStep()
				.insertTagAndEnter()
				.selectEducation()
				.selectSubjectArea()
				.selectTargetGroup()
				.clickCreateMaterial();
				
	int initialTagsAmount = tagsAmount.getTagsCount();
	    
	          tagsAmount = tagsAmount
	        		  
	        	.clickActionsMenu()
	            .clickEditMaterial()
	            .clickNextStep()
	            .insertTagAndEnter()
	            .clickUpdateMaterial();
	            
	int newTagsAmount = tagsAmount.getTagsCount();         
             
		assertTrue(initialTagsAmount + 1 == newTagsAmount);
	}
	

	@Test
	public void MaterialTests_AddToFavorites_MaterialIsInFavourtes(){
		
		boolean starIsSelected = goToLandingPage()
				.chooseUserType("Moderator")
				.clickMyMaterials()
				.clickToSelectStar()
				.openMaterial()
				.starIsSelected();
		assertTrue(starIsSelected);
	}
	
	@Test
	public void MaterialTests_RemoveFromFavorites_MaterialIsRemovedFromFavorites() {
		
		boolean starIsUnselected = goToLandingPage()
				.chooseUserType("Moderator")
				.goToMyFavorites()
				.openMaterial()
				.unselectStar()
				.starIsUnselected();
		assertTrue(starIsUnselected);
	}
	
	@Test
	public void MaterialTests_AddTags_ShowMoreButtonIsDisplayed() {

		boolean showMoreButtonIsDisplayed = goToLandingPage()
				.chooseUserType("Publisher")
				.clickMyMaterials()
				.openMaterial()
				.insertTags()
				.showMoreButtonIsDisplayed();
		assertTrue(showMoreButtonIsDisplayed);
	}
	
	@Test
	public void MaterialTests_CreateMaterial_MaterialIsUnreviewedBannerIsDisplayed() {

		String unreviewedBannerText = goToLandingPage()
				.chooseUserType("Admin")
				.clickAddMaterial()
				.setHyperLink()
				.setMaterialTitle()
				.addDescription()
				.clickNextStep()
				.selectEducation()
				.selectSubjectArea()
				.selectTargetGroup()
				.clickNextStep()
				.setAuthorFirstName()
				.setAuthorSurName()
				.clickCreateMaterial()
				.getUnreviewedBannerText();
		Assert.assertEquals(Constants.unreviewedBannerText, unreviewedBannerText);
	}
	
	@Test
	public void MaterialTests_ReportBroken_BrokenMaterialIsReported() {

		String brokenContentIsReported = goToLandingPage()
				.chooseUserType("User")
				.clickMyMaterials()
				.openMaterial()
				.clickActionsMenu()
				.clickReportBrokenMaterial()
				.clickReportMaterial()
				.getNotificationIsSentText();
		Assert.assertEquals(Constants.reportedText, brokenContentIsReported);
	}
	
	@Test
	public void MaterialTests_ReportImproperTag_TagIsReported() {

		String improperTagIsReported = goToLandingPage()
				.chooseUserType("User")
				.clickMyMaterials()
				.openMaterial()
				.addNewTag()
				.reportImproperTag()
				.setImproperDescription()
				.clickNotifyMaterial()
				.getNotificationIsSentText();
		Assert.assertEquals(Constants.reportedText, improperTagIsReported);
	}
}
