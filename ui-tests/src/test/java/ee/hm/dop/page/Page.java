package ee.hm.dop.page;

import ee.hm.dop.components.AdvancedSearch;
import ee.hm.dop.components.Header;
import ee.hm.dop.components.LeftMenu;
import ee.hm.dop.components.Logo;
import ee.hm.dop.components.SimpleSearch;
import ee.hm.dop.components.UserMenu;
import ee.hm.dop.tests.SeleniumUser;

public abstract class Page extends SeleniumUser {
	
	private static final UserMenu userMenu = new UserMenu();

	public UserMenu getUserMenu() {
		return userMenu;
	}
	
	private static final Header header = new Header();

	public Header getHeader() {
		return header;
	}
	
	private static final SimpleSearch simpleSearch = new SimpleSearch();

	public SimpleSearch getSimpleSearch() {
		return simpleSearch;
	}
	
	private static final Logo logo = new Logo();

	public Logo getLogo() {
		return logo;
	}
	
	private static final AdvancedSearch advancedSearch  = new AdvancedSearch ();

	public AdvancedSearch getAdvancedSearch () {
		return advancedSearch ;
	}
	
	private static final LandingPage landingPage = new LandingPage();

	public LandingPage getLandingPage() {
		return landingPage;
	}
	
	private static final LeftMenu leftMenu = new LeftMenu();

	public LeftMenu getLeftMenu() {
		return leftMenu;
	}

   
}
