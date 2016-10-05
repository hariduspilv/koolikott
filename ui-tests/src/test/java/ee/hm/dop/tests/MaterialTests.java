package ee.hm.dop.tests;

import static org.junit.Assert.assertTrue;

import org.junit.Assert;
import org.junit.FixMethodOrder;

import static ee.hm.dop.page.LandingPage.goToLandingPage;

import org.junit.Test;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)

public class MaterialTests {

	@Test
	public void createMaterialAsSmallPublisher() {

		String creatorName = goToLandingPage()
				.chooseUserType("SmallPublisher")
				.moveCursorToAddPortfolio()
				.moveCursorToAddMaterial()
				.clickAddMaterial()
				.setHyperLink("http://thoughtcatalog.com/reel/")
				//.uploadPhoto()
				.setMaterialTitle()
				.addDescription()
				.clickNextStep()
				.selectTargetGroup()
				.clickAddMaterial()
				.getCreatorName();

		Assert.assertEquals("Lisaja: Publisher Publisher", creatorName);

	}
	
	@Test
	public void createMaterialThroughPortfolioAsAdmin() {

		boolean isMaterialBoxIsDisplayed = goToLandingPage()
				.chooseUserType("Admin")
				.clickAddPortfolio()
				.insertPortfolioTitle()
				.addDescription()
				//.uploadPhoto()
				.selectEducationalContext()
				.selectSubjectArea()
				.selectAgeGroup()
				.insertTagAndEnter("creative")
				.clickCreatePortfolioButton()
				.addNewChapter()
				.setChapterTitle()
				.checkArrow()
				.setUrlLink()
				.clickAddMaterial()
				.checkHyperLink()
				//.uploadPhoto()
				.setMaterialTitle()
				.addDescription()
				.clickNextStep()
				.clickNextStep()
				.insertAuthorsName()
				.insertAuthorsSurname()
				.insertPublishersName()
				.clickAddMaterial()
				.clickExitAndSave()
				.isMaterialBoxIsDisplayed();

		assertTrue(isMaterialBoxIsDisplayed);

	}
	
	@Test
	public void selectAndAddMaterialToNewPortfolio() {

		boolean materialIsDisplayed = goToLandingPage()
				.chooseUserType("SmallPublisher")
				.clickToSelectMaterial()
				.clickAddPortfolio()
				.insertPortfolioTitle()
				//.uploadPhoto()
				.selectEducationalContext()
				.selectSubjectArea()
				.selectAgeGroup()
				.insertTags()
				.clickCreatePortfolioButton()
				.materialMessageIsDisplayedInPortfolio();

		assertTrue(materialIsDisplayed);

	}
	
	@Test
	public void selectAndAddMaterialToTheExistingPortfolio() {

		boolean successMessage = goToLandingPage()
				.chooseUserType("SmallPublisher")
				.clickToSelectMaterial()
				.moveCursorToAddMaterialToExistingPortfolio()
				.clickToAddMaterialToExistingPortfolio()
				.choosePortfolio()
				.choosePortfolioChapter()
				.successMessageIsDisplayed();

		assertTrue(successMessage);

	}
	
	@Test
	public void markMaterialAsPreferred() {

		String likesNumber = goToLandingPage()
				.chooseUserType("SmallPublisher")
				.openMaterialPage()
				.likeMaterial()
				.getLikesNumber();

		Assert.assertEquals("1", likesNumber);

	}

	@Test
	public void editMaterialByAddingTag() {

		String tagText = goToLandingPage()
				.chooseUserType("SmallPublisher")
				.openMaterialPage()
				.selectActionFromMenu()
				.clickEditMaterial()
				.clickNextStep()
				.insertTagAndEnter("1")
				.clickUpdateMaterial()
				.getTagText();

		Assert.assertEquals("1", tagText);

	}
	
	@Test
	public void showMoreButtonIsDisplayed() {

		boolean showMoreButtonIsDisplayed = goToLandingPage()
				.chooseUserType("SmallPublisher")
				.openMaterialPage()
				.insertTags()
				.showMoreButtonIsDisplayed();
				
		assertTrue(showMoreButtonIsDisplayed);


	}
	
	@Test
	public void addMaterialToFavoritesAndRemoveFromFavorites() {

		boolean starIsSelected = goToLandingPage()
				.chooseUserType("Moderator")
				.clickToSelectStar()
				.openMaterialPage()
				.starIsSelected();

		assertTrue(starIsSelected);
		
		boolean starIsUnselected = goToLandingPage()
				.chooseUserType("Moderator")
				.openMaterialPage()
				.unselectStar()
				.starIsUnselected();

		assertTrue(starIsUnselected);

	}

	

}