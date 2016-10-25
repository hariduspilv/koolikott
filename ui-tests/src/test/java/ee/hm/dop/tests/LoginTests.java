package ee.hm.dop.tests;
import static org.junit.Assert.assertTrue;

import org.junit.Assert;

import static ee.hm.dop.page.LandingPage.goToLandingPage;

import org.junit.Test;


public class LoginTests {
	
	@Test
	public void loginWithMobileID() {

		String userName = goToLandingPage()
				.getUserMenu()
				.logoutIfLoggedIn()
				.getHeader()
				.clickLogin()
				.insertMobileIDCode("14212128025")
				.insertMobilePhoneNumber("+37200007")
				.clickLoginWithID() 
				.clickHideLogin()
				.getUserMenu()
				.clickProfileIcon()
				.getUserName();

		Assert.assertEquals("Seitsmes Testnumber", userName);

	}
	
	@Test
	public void loginWithESchool() {

		String userName = goToLandingPage()
				.getUserMenu()
				.logoutIfLoggedIn()
				.getHeader()
				.clickLogin()
				.clickLoginWithEKool()
				.insertUsernameAndPassword("peeter.paan", "parool")
				.clickSubmitLogin()
				.getUserMenu()
				.clickProfileIcon()
				.getUserName();

		Assert.assertEquals("Peeter Paan", userName);

	}
	
	@Test
	public void loginWithStuudium() {

		String userName = goToLandingPage()
				.getUserMenu()
				.logoutIfLoggedIn()
				.getHeader()
				.clickLogin()
				.clickLoginWithStuudium()
				.insertUsernameAndPassword("Netgroup Test Kaks",  "meie teine saladus")
				.submitStuudium()
				.clickGivePermissionButton()
				.getUserMenu()
				.clickProfileIcon()
				.getUserName();

		Assert.assertEquals("Netgroup Test Kaks", userName);

	}
	
	@Test
	public void loginAsNewUserToMyPortfoliosPage() {

		boolean addPortfolioMessage = goToLandingPage()
				.getUserMenu()
				.logoutIfLoggedIn()
				.getLeftMenu()
				.clickMyThings()
				.clickMyPortfolios()
				.getLoginDialogPopup()
				.insertMobileIDCode("14212128025")
				.insertMobilePhoneNumber("+37200007")
				.clickLoginWithID() 
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
				.insertMobileIDCode("14212128025")
				.insertMobilePhoneNumber("+37200007")
				.clickLoginWithID() 
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
				.insertMobileIDCode("14212128025")
				.insertMobilePhoneNumber("+37200007")
				.clickLoginWithID() 
				.clickHideLogin3()
				.getAddFavoritesMessageText();

		assertTrue(addFavoritesMessage);

	}


}
