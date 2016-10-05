package ee.hm.dop.components;

import ee.hm.dop.page.LandingPage;
import ee.hm.dop.tests.SeleniumUser;

public abstract class PageComponent extends SeleniumUser {
	
	private static final MaterialTaxonomyPart materialTaxonomyPart = new MaterialTaxonomyPart();

	public MaterialTaxonomyPart getMaterialTaxonomyPart() {
		return materialTaxonomyPart;
	}
	
	private static final LandingPage landingPage = new LandingPage();

	public LandingPage getLandingPage() {
		return landingPage;
	}
	
	private static final SimpleSearch simpleSearch = new SimpleSearch();

	public SimpleSearch getSimpleSearch() {
		return simpleSearch;
	}
}
