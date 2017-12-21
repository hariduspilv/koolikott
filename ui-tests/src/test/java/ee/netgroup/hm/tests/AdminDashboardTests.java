package ee.netgroup.hm.tests;

import static ee.netgroup.hm.page.LandingPage.goToLandingPage;
import static org.junit.Assert.*;
import org.junit.Assert;
import org.junit.Test;
import ee.netgroup.hm.components.MaterialModal;

public class AdminDashboardTests {
	
	@Test
	public void AdminDashboardTests_Moderator_LearningObjectIsMarkedAsProper() {

		boolean improperContentBannerIsHidden = goToLandingPage()
				.chooseUserType("Moderator")
				.clickImproperLearningObjects()
				.openImproperLearningObject()
				.markContentAsNotImproper()
				.isBannerToolbarHidden();
		assertTrue(improperContentBannerIsHidden);
	}
	
	@Test
	public void AdminDashboardTests_Admin_LearningObjectIsMarkedAsProper() {

		boolean improperContentBannerIsHidden = goToLandingPage()
				.chooseUserType("Admin")
				.clickImproperLearningObjects()
				.openImproperLearningObject()
				.markContentAsNotImproper()
				.isBannerToolbarHidden();
		assertTrue(improperContentBannerIsHidden);
	}

	@Test
	public void AdminDashboardTests_NewUnreviwedLearningObject_LearningObjectIsMarkedReviewed() {

		boolean unreviwedBannerIsHidden = goToLandingPage()
				.chooseUserType("Admin")
				.clickUnreviewedLearninObjects()
				.openNewLearningObject()
				.markContentIsReviewed()
				.isBannerToolbarHidden();
		assertTrue(unreviwedBannerIsHidden);
	}
	
	@Test
	public void AdminDashboardTests_RestoreDeletedObject_LearningObjectIsRestored() {

		boolean deletedBannerIsHidden = goToLandingPage()
				.chooseUserType("Admin")
				.clickDeletedLearningObjects()
				.openDeletedLearningObject()
				.restoreDeletedLearningObject()
				.isBannerToolbarHidden();
		assertTrue(deletedBannerIsHidden);
	}

	@Test
	public void AdminDashboardTests_ChangedLearningObject_ChangesAreAccepted() {

		boolean changedBannerIsHidden = goToLandingPage()
				.chooseUserType("Moderator")
				.clickChangedLearningObjects()
				.openChangedLearningObject()
				.markChangesAccepted()
				.isBannerToolbarHidden();
		assertTrue(changedBannerIsHidden);
	}
	
	@Test
	public void AdminDashboardTests_ChangedMaterialLink_ChangesAreDeclined() {

		String materialUrl  = goToLandingPage()	
				.chooseUserType("Admin")
				.clickAddMaterial()
				.setNewHyperLink()
				.addMaterialLicenseType()
				.setMaterialTitle()
				.addDescription()
				.setAuthorFirstName()
				.setAuthorSurName()
				.selectEducation()
				.selectSubjectArea()
				.selectTargetGroup()
				.clickCreateMaterial()
				.markNewMaterialAsReviewed()
				.clickActionsMenu()
				.clickEditMaterial()
				.setHyperLink()
				.clickUpdateMaterial()
				.markChangesDeclined()
				.getMaterialUrlText();
		Assert.assertEquals(MaterialModal.newMaterialUrl, materialUrl);
	}


	
}
