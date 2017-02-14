package ee.hm.dop.tests;
import static org.junit.Assert.assertTrue;

import static ee.hm.dop.page.LandingPage.goToLandingPage;

import org.junit.Test;


public class LoginTestsAsNewUser {
	
	
	@Test
	public void loginAsNewUserToMyPortfoliosPage() {

		boolean addPortfolioMessage = goToLandingPage()
				.getUserMenu()
				.logoutIfLoggedIn()
				.getLeftMenu()
				.clickMyThings()
				.clickMyPortfolios()
				.getLoginDialogPopup()
				.insertMobileIDCode("11412090004")
				.insertMobilePhoneNumber("+37200000766")
				.clickLoginWithMobileID() 
				.clickHideLogin1()
				.getAddPortfolioMessageText();

		assertTrue(addPortfolioMessage);

	}
	
	@Test
	public void loginAsNewUserToMyMaterialsPage() {

		boolean addMaterialMessage = goToLandingPage()
				.getUserMenu()
				.logoutIfLoggedIn()
				.getLeftMenu()
				.clickMyThings()
				.clickMyMaterialsWhenNotLoggedIn()
				.getLoginDialogPopup()
				.insertMobileIDCode("11412090004")
				.insertMobilePhoneNumber("+37200000766")
				.clickLoginWithMobileID() 
				.clickHideLogin2()
				.getAddMaterialMessageText();

		assertTrue(addMaterialMessage);

	}
	
	@Test
	public void loginAsNewUserToMyFavoritesPage() {

		boolean addFavoritesMessage = goToLandingPage()
				.getUserMenu()
				.logoutIfLoggedIn()
				.getLeftMenu()
				.clickMyThings()
				.clickMyFavorites()
				.getLoginDialogPopup()
				.insertMobileIDCode("11412090004")
				.insertMobilePhoneNumber("+37200000766")
				.clickLoginWithMobileID() 
				.clickHideLogin3()
				.getAddFavoritesMessageText();

		assertTrue(addFavoritesMessage);

	}


}
