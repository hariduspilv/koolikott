package ee.hm.dop.components;

import ee.hm.dop.page.ImproperPortfoliosDashboardPage;
import ee.hm.dop.page.LandingPage;
import ee.hm.dop.page.MyProfilePage;
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
	
	private static final ImproperPortfoliosDashboardPage improperPortfoliosDashboardPage = new ImproperPortfoliosDashboardPage();

	public ImproperPortfoliosDashboardPage getDashboardPage() {
		return improperPortfoliosDashboardPage;
	}
	
	private static final MyProfilePage myProfilePage = new MyProfilePage();

	public MyProfilePage getMyProfilePage() {
		return myProfilePage;
	}
	
	private static final Header header = new Header();

	public Header getHeader() {
		return header;
	}
	
	private static final LoginDialogPopup loginDialogPopup = new LoginDialogPopup();

	public LoginDialogPopup getLoginDialogPopup() {
		return loginDialogPopup;
	}
}
