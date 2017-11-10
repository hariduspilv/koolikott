package ee.netgroup.hm.tests;

import static ee.netgroup.hm.page.LandingPage.goToLandingPage;
import static org.junit.Assert.*;
import org.junit.Assert;
import org.junit.Test;

import ee.netgroup.hm.components.LeftMenu;

public class ContentsTests {
	
	@Test
	public void ContentsTests_FilterPreschoolEducation() {

		goToLandingPage()
				.chooseUserType("User")
				.openTableOfContents();
		String educationTaxon = LeftMenu
				.clickToFilterPreschoolEducation()
				.clickToFilterPreschoolEducationEstonianLanguage()
				.openSearchResultPortfolio()
				.getEducationalTaxonText();
		Assert.assertEquals("Alusharidus", educationTaxon);
	}
	
	@Test
	public void ContentsTests_MenuMaterialCount() {

		goToLandingPage()
				.chooseUserType("User")
				.openTableOfContents();
		boolean materialCount = LeftMenu
				.clickToFilterPreschoolEducation()
				.getMaterialCount();
		assertTrue(materialCount);
	}

}
