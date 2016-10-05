package ee.hm.dop.tests;
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
				.clickMyProfile()
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
				.clickMyProfile()	
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
				.clickMyProfile()
				.getUserName();

		Assert.assertEquals("Netgroup Test Kaks", userName);

	}


}
