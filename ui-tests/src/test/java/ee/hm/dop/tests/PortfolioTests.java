package ee.hm.dop.tests;

import static org.junit.Assert.assertTrue;

import org.junit.Assert;
import org.junit.FixMethodOrder;

import static ee.hm.dop.page.LandingPage.goToLandingPage;

import org.junit.Test;
import org.junit.runners.MethodSorters;

    @FixMethodOrder(MethodSorters.NAME_ASCENDING)

public class PortfolioTests {

	@Test
	public void createPortfolio() {

		String tagText = goToLandingPage()
				.chooseUserType("User")
				.clickAddPortfolio()
				.insertPortfolioTitle()
				.uploadPhoto()
				.addDescription()
				.selectEducationalContext()
				.selectSubjectArea()
				.selectAgeGroup()
				.insertTagAndEnter("epic")
				.clickCreatePortfolioButton()
				.getTagText();

		Assert.assertEquals("epic", tagText);

	}
	
	@Test
	public void createPortfolioCopy() {

		boolean portfolioCopyWasMade = goToLandingPage()
				.chooseUserType("User")
				.clickAddPortfolio()
				.insertPortfolioTitle()
				.addDescription()
				.uploadPhoto()
				.selectEducationalContext()
				.selectSubjectArea()
				.selectAgeGroup()
				.insertTagAndEnter("automated")
				.clickCreatePortfolioButton()
				.clickExitAndSave()
				.moveCursorToCopyPortfolio()
				.clickCopyPortfolio()
				.insertSpecificPortfolioTitle("(Copy)")
				.insertTags()
				.savePortfolioCopy()
				.clickExitAndSave()
				.wordCopyIsAddedToPortfolioTitle();
		
		assertTrue(portfolioCopyWasMade);

	}
	

	@Test
	public void makePortfolioVisible() {

		String visibility = goToLandingPage()
				.chooseUserType("User")
				.openPortfolio()
				.clickActionsMenu()
				.clickEditPortfolio()
				.clickVisibilityButton()
				.selectMakePublic()
				.isPortfolioChangedToPublic();
				
		Assert.assertEquals("visibility", visibility);

	}
	
	
	@Test
	public void notifyImproperPortfolio() {

		boolean contentsIsNotImproperIsDisabled = goToLandingPage()
				.chooseUserType("User")
				.openPortfolio()
				.clickActionsMenu()
				.clickNotifyImproperContent()
				.confirmImproperContent()
				.getUserMenu()
				.logOff()
				.chooseUserType("Admin")
				.getLeftMenu()
				.checkLeftMenuButtons()
				.clickDashboard()
				.clickImproperPortfolios()
				.getDashboardPage()
				.clickToOrderImproperPortfolios()
				.clickOpenPortfolioFromDashboard()
				.clickActionsMenu()
				.setContentIsNotImproper()
				.clickActionsMenu()
				.contentsIsNotImproperIsDisabled();

		assertTrue(contentsIsNotImproperIsDisabled);

	}
	
	@Test
	public void removePortfolio() {

		String deletedPortfolioToast = goToLandingPage()
				.chooseUserType("Admin")
				.openPortfolio()
				.clickActionsMenu()
				.clickRemovePortfolio()
				.confirmImproperContent()
				.getLeftMenu()
				.clickMyThings()
				.clickMyPortfolios()
				.getMyProfilePage()
				.isPortfolioDeletedToastVisible();

		Assert.assertEquals("Kogumik kustutatud", deletedPortfolioToast);

	}
	
	
	@Test
	public void portfolioIsAddedToRecommendations() {

		boolean removeFromRecommendationListIsDisplayed = goToLandingPage()
				.chooseUserType("User")
				.clickAddPortfolio()
				.insertPortfolioTitle()
				.addDescription()
				.uploadPhoto()
				.selectEducationalContext()
				.selectSubjectArea()
				.selectAgeGroup()
				.insertTagAndEnter1()
				.clickCreatePortfolioButton()
				.clickExitAndSave()
				.getUserMenu()
				.logOff()
				.chooseUserType("Admin")
				.getSimpleSearch()
				.insertSearchCriteriaAndSearch1()
				.openSearchResultPortfolio()
				.clickActionsMenu()
				.addToRecommendationsList()
				.clickActionsMenu()
				.removeFromRecommendationListIsDisplayed();
				
		assertTrue(removeFromRecommendationListIsDisplayed);


	}
}
