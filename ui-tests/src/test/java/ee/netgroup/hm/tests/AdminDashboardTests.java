package ee.netgroup.hm.tests;

import static ee.netgroup.hm.page.LandingPage.goToLandingPage;
import static org.junit.Assert.*;
import org.junit.Test;

public class AdminDashboardTests {
	
	@Test
	public void AdminDashboardTests_Moderator_LearningObjectIsMarkedAsProper() {

		boolean improperContentBannerIsHidden = goToLandingPage()
				.chooseUserType("Moderator")
				.clickImproperLearningObjects()
				.openImproperLearningObject()
				.markContentAsNotImproper()
				.isErrorBannerHidden();
		assertTrue(improperContentBannerIsHidden);
	}
	
	@Test
	public void AdminDashboardTests_Admin_LearningObjectIsMarkedAsProper() {

		boolean improperContentBannerIsHidden = goToLandingPage()
				.chooseUserType("Admin")
				.clickImproperLearningObjects()
				.openImproperLearningObject()
				.markContentAsNotImproper()
				.isErrorBannerHidden();
		assertTrue(improperContentBannerIsHidden);
	}

	@Test
	public void AdminDashboardTests_NewUnreviwedLearningObject_LearningObjectIsMarkedReviewed() {

		boolean unreviwedBannerIsHidden = goToLandingPage()
				.chooseUserType("Admin")
				.clickUnreviewedLearninObjects()
				.openNewLearningObject()
				.markContentIsReviewed()
				.isErrorBannerHidden();
		assertTrue(unreviwedBannerIsHidden);
	}
	
	@Test
	public void AdminDashboardTests_RestoreDeletedObject_LearningObjectIsRestored() {

		boolean deletedBannerIsHidden = goToLandingPage()
				.chooseUserType("Admin")
				.clickDeletedLearningObjects()
				.openDeletedLearningObject()
				.restoreDeletedLearningObject()
				.isErrorBannerHidden();
		assertTrue(deletedBannerIsHidden);
	}



	
}
