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
				.clickReportImproperContent()
				.setImproperReason()
				.setImproperDescription()
				.clickNotify()
				.getNotificationIsSentText();
		Assert.assertEquals(Constants.reportedText, improperContentIsReported);
	}
	
	@Test
	public void PortfolioTests_EditChapterDescription() {

		boolean quoteTag = goToLandingPage()
				.chooseUserType("User")
				.clickAddPortfolio()
				.setPortfolioTitle()
				.selectEducationalContext()
				.selectSubjectArea()
				.selectAgeGroup()
				.addDescription()
				.clickCreatePortfolio()
				.setChapterTitle()
				.addDescription()
				.clickToSelectDescription()
				.clickToSelectBold()
				.clickToSelectItalic()
				.clickToSelectQuoteText()
				.clickSaveAndExitConfirmationControl() 
				.getQuoteTextTag();
		assertTrue(quoteTag);
	}
	
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
	
	@Test
	public void PortfolioTests_AddComment_CommentIsAdded() {

		String commentIsAdded = goToLandingPage()
				.chooseUserType("User")
				.openPortfolio()
				.showPortfolioComments()
				.addNewComment()
				.getCommentText();
		Assert.assertEquals(Constants.commentText, commentIsAdded);
	}
	
	@Test
	public void PortfolioTests_ReportImproperComment_CommentIsReported() {

		String improperCommentIsReported = goToLandingPage()
				.chooseUserType("User")
				.openPortfolio()
				.showPortfolioComments()
				.addNewComment()
				.reportImproperComment()
				.setImproperDescription()
				.clickNotify()
				.getNotificationIsSentText();
		Assert.assertEquals(Constants.reportedText, improperCommentIsReported);
	}

	@Test
	public void PortfolioTests_AddSystemTag_LoChangedBannerIsDisplayed() { // LO=Learning Object

		String changedLOBannerText  = goToLandingPage()
				.chooseUserType("Admin")
				.clickAddPortfolio()
				.setPortfolioTitle()
				.selectEducationalContext()
				.selectSubjectArea()
				.selectAgeGroup()
				.clickCreatePortfolio()
				.clickSaveAndExit()
				.makePortfolioPublic()
				.markNewPortfolioAsReviewed()
				.addNewSystemTag()
				.getChangedLOBannerText();
		Assert.assertEquals(Constants.changedLOBannerText, changedLOBannerText);
	}

	@Test
	public void PortfolioTests_NarrowChapterBlock_WidthIsNarrow() {

		boolean chapterWidth = goToLandingPage()
				.chooseUserType("User")
				.clickAddPortfolio()
				.setPortfolioTitle()
				.selectEducationalContext()
				.selectSubjectArea()
				.selectAgeGroup()
				.addDescription()
				.clickCreatePortfolio()
				.setChapterTitle()
				.addDescription()
				.clickChangeChapterWidth()
				.clickSaveAndExitConfirmationControl() 
				.isChapterNarrow();
		assertTrue(chapterWidth);
	}
	
	@Test
	public void PortfolioTests_AddSubchapter_SubchapterIsAdded() {

		boolean subchapter = goToLandingPage()
				.chooseUserType("User")
				.clickAddPortfolio()
				.setPortfolioTitle()
				.selectEducationalContext()
				.selectSubjectArea()
				.selectAgeGroup()
				.addDescription()
				.clickCreatePortfolio()
				.setChapterTitle()
				.addSubchapterTitle()
				.clickToSelectDescription()
				.setSubchapterTitleH3()
				.clickSaveAndExitConfirmationControl() 
				.isSubchapterDisplayed();
		assertTrue(subchapter);
	}
	
	@Test
	public void PortfolioTests_ChangeChapterOrder_OrderIsChanged() {

		String firstChapterTitle = goToLandingPage()
				.chooseUserType("User")
				.clickAddPortfolio()
				.setPortfolioTitle()
				.selectEducationalContext()
				.selectSubjectArea()
				.selectAgeGroup()
				.addDescription()
				.clickCreatePortfolio()
				.setSpecificChapterTitle("1")
				.addNewChapter()
				.setSecondChapterTitle("2")
				.clickMoveChapterDown()
				.clickSaveAndExitConfirmationControl() 
				.isChapterOrderChanged();
		Assert.assertEquals("2", firstChapterTitle);
	}
	
	@Test
	public void PortfolioTests_AddMediaFile_MediaFileIsAddedToChapter() {

		boolean newMediaFile = goToLandingPage()
				.chooseUserType("User")
				.clickAddPortfolio()
				.setPortfolioTitle()
				.selectEducationalContext()
				.selectSubjectArea()
				.selectAgeGroup()
				.addDescription()
				.clickCreatePortfolio()
				.setChapterTitle()
				.addDescription()
				.clickAddNewMedia()
				.setMediaLink()
				.setMediaTitle()
				.setMediaAuthor()
				.setMediaSource()
				.setMediaLicenseType()
				.clickAddMedia() 
				.clickSaveAndExitConfirmationControl()
				.isMediaFileAddedToChapter();
		assertTrue(newMediaFile);
	}
	
	@Test
	public void PortfolioTests_AlignRight_MaterialIsAligned() {

		boolean alignRight = goToLandingPage()
				.chooseUserType("User")
				.clickAddPortfolio()
				.setPortfolioTitle()
				.selectEducationalContext()
				.selectSubjectArea()
				.selectAgeGroup()
				.addDescription()
				.clickCreatePortfolio()
				.setChapterTitle()
				.addDescription()
				.clickAddExistingMaterial()
				.selectAllEducationalContexts()
				.insertMaterialSearchCriteria()
				.closeDetailedSearch()
				.clickToSelectMaterial2()
				.clickAddMaterialToPortfolio()
				.clickAlignRight()
				.clickSaveAndExitConfirmationControl() 
				.isMaterialAligned();
		assertTrue(alignRight);
	}
	

}
