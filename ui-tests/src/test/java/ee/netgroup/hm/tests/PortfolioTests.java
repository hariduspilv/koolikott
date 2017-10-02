package ee.netgroup.hm.tests;

import static org.junit.Assert.assertTrue;
import static ee.netgroup.hm.page.LandingPage.goToLandingPage;
import org.junit.Assert;
import org.junit.Test;
import ee.netgroup.hm.helpers.Constants;


public class PortfolioTests {
	
	public static String addWordToTitle = "(Copy)";

	@Test
	public void PortfolioTests_CreatePortfolio_PortfolioIsCreated() {

		String portfolioIsCreatedAlertText = goToLandingPage()
				.chooseUserType("Publisher")
				.clickAddPortfolio()
				.setPortfolioTitle()
				.selectEducationalContext()
				.selectSubjectArea()
				.selectAgeGroup()
				.uploadPhoto()
				.addDescription()
				.clickCreatePortfolio()
				.getSuccessAlertText();
		Assert.assertEquals("Kogumik loodud! Täienda seda võtmesõnadega ja jätkake alloleva tööriistariba abil peatükkide, alampeatükkide, sisutekstide ja materjalide lisamisega.", portfolioIsCreatedAlertText);
	}
	
	@Test
	public void PortfolioTests_CreateCopyOfPortfolio_CopyIsCreated() {

		boolean portfolioCopyWasMade = goToLandingPage()
				.chooseUserType("User")
				.clickAddPortfolio()
				.setPortfolioTitle()
				.selectEducationalContext()
				.selectSubjectArea()
				.uploadPhoto()
				.selectAgeGroup()
				.addDescription()
				.clickCreatePortfolio()
				.clickSaveAndExit()
				.makePortfolioPublic()
				.clickCopyPortfolio()
				.insertSpecificPortfolioTitle(addWordToTitle)
				.clickSavePortfolio()
				.clickSaveAndExit()
				.makePortfolioPublic()
				.wordWasAddedToPortfolioTitle();
		assertTrue(portfolioCopyWasMade);
	}
	
	@Test
	public void PortfolioTests_DeletePortfolio_PortfolioIsDeleted() {

		String deletedPortfolioToast = goToLandingPage()
				.chooseUserType("User")
				.openPortfolio()
				.clickActionsMenu()
				.clickDeletePortfolio()
				.clickConfirm()
				.isPortfolioDeletedTextVisible();
		Assert.assertEquals("Kogumik kustutatud", deletedPortfolioToast);
	}
	
	@Test
	public void PortfolioTests_ShareWithLink_VisibilityIsChanged() {

		boolean privacyIsShareWithLink = goToLandingPage()
				.chooseUserType("Publisher")
				.openPortfolio()
				.clickActionsMenu()
				.clickEditPortfolio()
				.clickVisibilityButton()
				.selectShareWithLink()
				.clickSaveAndExit()
				.clickExitAndStayPrivate()
				.isPortfolioPrivacyShareOnlyWithLink();
		assertTrue(privacyIsShareWithLink);
	}
	
	@Test
	public void PortfolioTests_ReportImproper_PortfolioIsReported() {

		String improperContentIsReported = goToLandingPage()
				.chooseUserType("User")
				.openPortfolio()
				.clickActionsMenu()
				.clickNotifyImproperContent()
				.clickNotify()
				.isPortfolioReportedAsImproper();
		Assert.assertEquals(Constants.reportedText, improperContentIsReported);
	}
	/*
	@Test
	public void PortfolioTests_editChapterDescription() {

		boolean preTag = goToLandingPage()
				.chooseUserType("Publisher")
				.openPortfolio()
				.clickActionsMenu()
				.clickEditPortfolio()
				.addNewChapter()
				.addDescription()
				.clickToSelectDescription()
				.clickToSelectBold()
				.clickToSelectItalic()
				.clickToSelectBulletedList()
				.clickToSelectNumberedList()
				.clickToSelectPreFormattedText()
				.clickSaveAndExitConfirmationControl() 
				.getPreFormattedTextTag();
		assertTrue(preTag);
	}*/
	
	@Test
	public void PortfolioTests_AddTag_TagIsAddedToPortfolio() {

		boolean addedTag = goToLandingPage()
				.chooseUserType("Publisher")
				.openPortfolio()
				.addNewTag()
				.isTagAddedToPortfolio();
		assertTrue(addedTag);
	}
	
	@Test
	public void PortfolioTests_AddToRecommendations_PortfolioIsRecommended() {

		boolean addedToRecommendations = goToLandingPage()
				.chooseUserType("Admin")
				.insertSearchCriteriaAndSearch("recommended:false")
				.sortResultsNewestFirst()
				.openSearchResultPortfolio()
				.clickActionsMenu()
				.addToRecommendationsList()
				.clickActionsMenu()
				.isRemoveFromRecommendationsDisplayed();
		assertTrue(addedToRecommendations);
	}
	
	@Test
	public void PortfolioTests_RemoveFromRecommendations_PortfolioIsRemovedFromRecommendations() {

		boolean removedFromRecommendations = goToLandingPage()
				.chooseUserType("Admin")
				.insertSearchCriteriaAndSearch("recommended:true")
				.openSearchResultPortfolio()
				.clickActionsMenu()
				.removeFromRecommendationsList()
				.clickActionsMenu()
				.isAddToRecommendationsDisplayed();
		assertTrue(removedFromRecommendations);
	}



}