package ee.netgroup.hm.tests;

import static ee.netgroup.hm.page.LandingPage.goToLandingPage;
import org.junit.Assert;
import org.junit.Test;
import ee.netgroup.hm.helpers.Constants;

public class LogInTests {
	
	@Test
	public void LogInTests_MobileID_UserIsLoggedIn() {

		String username = goToLandingPage()
				.logoutIfLoggedIn()
				.clickLogin()
				.insertMobileIDCode(Constants.mobileIdCode)
				.insertMobilePhoneNumber(Constants.mobilePhoneNumber)
				.clickLoginWithMobileID() 
				.clickOnUserProfile()
				.getUserName();
		Assert.assertEquals(Constants.mobileIdUser, username);
	}
	
	@Test
	public void LogInTests_ESchool_UserIsLoggedIn() {

		String username = goToLandingPage()
				.logoutIfLoggedIn()
				.clickLogin()
				.clickLoginWithEKool()
				.insertUsernameAndPassword(Constants.eSchoolUser, Constants.eSchoolPswd)
				.clickSubmitLogin()
				.clickOnUserProfile()
				.getUserName();
		Assert.assertEquals(Constants.eSchoolUserName, username);
	}
	
	@Test
	public void LogInTests_Stuudium_UserIsLoggedIn() {

		String username = goToLandingPage()
				.logoutIfLoggedIn()
				.clickLogin()
				.clickLoginWithStuudium()
				.insertUsernameAndPassword(Constants.stuudiumUser, Constants.stuudiumPswd)
				.submitStuudium()
				.clickGivePermission()
				.clickOnUserProfile()
				.getUserName();
		Assert.assertEquals(Constants.stuudiumUser, username);
	}
	



}
