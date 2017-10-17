package ee.netgroup.hm.tests;

import static ee.netgroup.hm.page.LandingPage.goToLandingPage;
import static org.junit.Assert.*;
import org.junit.Assert;
import org.junit.Test;

public class ContentsTests {
	
	@Test
	public void ContentsTests_FilterPreschoolEducation() {

		String educationTaxon = goToLandingPage()
				.chooseUserType("User")
				.clickToFilterPreschoolEducation()
				.clickToFilterPreschoolEducationEstonianLanguage()
				.openSearchResultPortfolio()
				.getEducationalTaxonText();
		Assert.assertEquals("Alusharidus", educationTaxon);
	}
	
	@Test
	public void ContentsTests_MenuMaterialCount() {

		boolean materialCount = goToLandingPage()
				.chooseUserType("User")
				.clickToFilterPreschoolEducation()
				.getMaterialCount();
		assertTrue(materialCount);
	}

}
