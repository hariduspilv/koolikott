package ee.netgroup.hm.tests;

import static ee.netgroup.hm.page.LandingPage.goToLandingPage;
import static org.junit.Assert.*;

import org.junit.Test;

public class AdminDashboardTests {
	
	@Test
	public void AdminDashboardTests_ImproperPortfolio_PortfolioIsMarkedAsProper() {

		boolean improperContentBannerIsHidden = goToLandingPage()
				.chooseUserType("Admin")
				.clickDashboard()
				.clickImproperPortfolios()
				.clickToOrderImproperPortfolios()
				.clickOpenImproperPortfolio()
				.markContentAsNotImproper()
				.isContentProper();
		assertTrue(improperContentBannerIsHidden);
	}
	
	@Test
	public void AdminDashboardTests_ImproperMaterial_MaterialIsMarkedAsProper() {

		boolean improperContentBannerIsHidden = goToLandingPage()
				.chooseUserType("Admin")
				.clickDashboard()
				.clickImproperMaterials()
				.clickOpenImproperMaterial()
				.markContentAsNotImproper()
				.isContentProper();
		assertTrue(improperContentBannerIsHidden);
	}

	@Test
	public void AdminDashboardTests_NewUnreviwedLearningObject_LearningObjectIsMarkedReviewed() {

		boolean unreviwedBannerIsHidden = goToLandingPage()
				.chooseUserType("Admin")
				.clickDashboard()
				.clickUnreviewedLearninObjects()
				.clickOpenLearningObject()
				.markContentIsReviewed()
				.isContentReviewed();
		assertTrue(unreviwedBannerIsHidden);
	}
	
	@Test
	public void AdminDashboardTests_RestoreDeletedPortfolio_PortfolioIsRestored() {

		boolean deletedBannerIsHidden = goToLandingPage()
				.chooseUserType("Admin")
				.clickDashboard()
				.clickDeletedPortfolios()
				.clickOpenPortfolio()
				.restoreDeletedPortfolio()
				.isPortfolioRestored();
		assertTrue(deletedBannerIsHidden);
	}
	
	@Test
	public void AdminDashboardTests_RestoreDeletedMaterial_MaterialIsRestored() {

		boolean deletedBannerIsHidden = goToLandingPage()
				.chooseUserType("Admin")
				.clickDashboard()
				.clickDeletedMaterials()
				.clickOpenMaterial()
				.restoreMaterial()
				.isMaterialRestored();
		assertTrue(deletedBannerIsHidden);
	}
	
	@Test
	public void AdminDashboardTests_RestoreBrokenMaterial_MaterialIsRestored() {

		boolean brokenBannerIsHidden = goToLandingPage()
				.chooseUserType("Admin")
				.clickDashboard()
				.clickBrokenMaterials()
				.clickSortByReportedDate()
				.clickOpenMaterial()
				.restoreBrokenMaterial()
				.isMaterialRestored();
		assertTrue(brokenBannerIsHidden);
	}

	
}
