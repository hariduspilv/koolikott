package ee.hm.dop.tests;

import static org.junit.Assert.*;
import static ee.hm.dop.page.LandingPage.goToLandingPage;
import org.junit.Test;



public class PortfolioActionsTests {
	

	@Test
	public void shareWithLinkPrivacy() {

		boolean privacyIsShareWithLink = goToLandingPage()
				.chooseUserType("SmallPublisher")
				.getLeftMenu()
				.getMyPortfoliosPage()
				.openPortfolio()
				.clickActionsMenu()
				.clickEditPortfolio()
				.clickVisibilityButton()
				.selectShareWithLink()
				.clickExitAndSave()
				.getPrivacyConfirmationPopup()
				.clickExitAndStayPrivate()
				.isPortfolioPrivacyShareOnlyWithLink();
				
				assertTrue(privacyIsShareWithLink);

	}
	
	
	@Test
	public void notifyImproperPortfolio() {

		boolean improperContentIsDisplayed = goToLandingPage()
				.chooseUserType("User")
				.openPortfolio()
				.clickActionsMenu()
				.clickNotifyImproperContent()
				.confirmImproperContent()
				.getUserMenu()
				.logOff()
				.chooseUserType("Admin")
				.getLeftMenu()
				.clickDashboard()
				.clickImproperPortfolios()
				.getDashboardPage()
				.clickToOrderImproperPortfolios()
				.clickOpenPortfolioFromDashboard()
				.setContentIsNotImproper()
				.contentsIsNotImproper();

		assertFalse(improperContentIsDisplayed);

	}
	
	
	@Test
	public void editChapterDescription() {

		boolean preTag = goToLandingPage()
				.chooseUserType("SmallPublisher")
				.getLeftMenu()
				.getMyPortfoliosPage()
				.openPortfolio()
				.clickActionsMenu()
				.clickEditPortfolio()
				.addNewChapter()
				.addDescription()
				.clickExitAndSave()
				.getPrivacyConfirmationPopup()
				.makePortfolioPublic()
				.clickActionsMenu()
				.clickEditPortfolio()
				.clickToSelectDescription()
				.clickToSelectBold()
				.clickToSelectItalic()
				.clickToSelectUl()
				.clickToSelectOl()
				.clickToSelectPre()
				.clickExitAndSave()
				.getPreTag();

				assertTrue(preTag);

	}
	
	
	@Test
	public void portfolioIsAddedToRecommendations() {

		boolean removeFromRecommendationListIsDisplayed = goToLandingPage()
				.chooseUserType("SmallPublisher")
				.getFabButton()
				.clickAddPortfolio()
				.insertPortfolioTitle()
				.uploadPhoto()
				.selectEducationalContext()
				.selectSubjectArea()
				.selectAgeGroup()
				.addDescription()
				.clickCreatePortfolioButton()
				.clickExitAndSave()
				.getPrivacyConfirmationPopup()
				.makePortfolioPublic()
				.insertTagAndEnter1()
				.getUserMenu()
				.logOff()
				.chooseUserType("Admin")
				.getSimpleSearch()
				.insertSearchCriteriaAndSearch1()
				.sortResults()
				.openSearchResultPortfolio()
				.clickActionsMenu()
				.addToRecommendationsList()
				.clickActionsMenu()
				.removeFromRecommendationListIsDisplayed();
				
		assertTrue(removeFromRecommendationListIsDisplayed);


	}
}
