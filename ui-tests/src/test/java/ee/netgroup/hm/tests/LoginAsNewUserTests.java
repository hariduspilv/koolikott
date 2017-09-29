package ee.netgroup.hm.tests;

import static ee.netgroup.hm.page.LandingPage.goToLandingPage;
import static org.junit.Assert.*;

import org.junit.Test;

import ee.netgroup.hm.helpers.Constants;

public class LoginAsNewUserTests {
	
	@Test
	public void LogInTests_AsNewUser_MyPortfoliosPage() {

		boolean addPortfolioMessage = goToLandingPage()
				.logoutIfLoggedIn()
				.clickMyThings()
				.clickMyPortfolios()
				.insertMobileIDCode(Constants.mobileIdCode)
				.insertMobilePhoneNumber(Constants.mobilePhoneNumber)
				.clickLoginWithMobileID() 
				.getAddPortfolioMessageText();
		assertTrue(addPortfolioMessage);
	}
	
	@Test
	public void LogInTests_AsNewUser_MyMaterialsPage() {

		boolean addMaterialMessage = goToLandingPage()
				.logoutIfLoggedIn()
				.clickMyThings()
				.clickMyMaterialsWhenNotLoggedIn()
				.insertMobileIDCode(Constants.mobileIdCode)
				.insertMobilePhoneNumber(Constants.mobilePhoneNumber)
				.clickLoginWithMobileID() 
				.getAddMaterialMessageText();
		assertTrue(addMaterialMessage);
	}
	
	@Test
	public void LogInTests_AsNewUser_MyFavoritesPage() {

		boolean addFavoritesMessage = goToLandingPage()
				.logoutIfLoggedIn()
				.clickMyThings()
				.clickMyFavorites()
				.insertMobileIDCode(Constants.mobileIdCode)
				.insertMobilePhoneNumber(Constants.mobilePhoneNumber)
				.clickLoginWithMobileID() 
				.getAddFavoritesMessageText();
		assertTrue(addFavoritesMessage);

	}

}
