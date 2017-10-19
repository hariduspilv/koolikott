package ee.netgroup.hm.tests;

import static ee.netgroup.hm.page.LandingPage.goToLandingPage;
import org.junit.Assert;
import org.junit.Test;
import ee.netgroup.hm.helpers.Constants;

public class ReportImproperTests {
	
	
	@Test
	public void ReportImproperTests_AnonymousUser_Content_LoggingInIsRequired() {

		String loggingInIsRequired = goToLandingPage()
				.logoutIfLoggedIn()
				.openPortfolio()
				.clickActionsMenu()
				.clickReportImproperContent()
				.getLogginInIsRequiredText();
		Assert.assertEquals(Constants.logInModalTitleText, loggingInIsRequired);
	}
	
	@Test
	public void ReportImproperTests_AnonymousUser_Comment_LoggingInIsRequired() {

		String loggingInIsRequired = goToLandingPage()
				.logoutIfLoggedIn()
				.openMaterialUrl()
				.showMaterialComments()
				.reportImproperComment()
				.getLogginInIsRequiredText();
		Assert.assertEquals(Constants.logInModalTitleText, loggingInIsRequired);
	}
	
	@Test
	public void ReportImproperTests_AnonymousUser_Tag_LoggingInIsRequired() {

		String loggingInIsRequired = goToLandingPage()
				.logoutIfLoggedIn()
				.searchTag("AutomaatTest:")
				.openSearchResultPortfolio()
				.reportImproperTag()
				.getLogginInIsRequiredText();
		Assert.assertEquals(Constants.logInModalTitleText, loggingInIsRequired);
	}
	
	@Test
	public void ReportImproperTests_AnonymousUserLogsInWithMobileID_MaterialIsReported() {

		String improperContentIsReported = goToLandingPage()
				.logoutIfLoggedIn()
				.openMaterial()
				.clickActionsMenu()
				.clickReportImproperContent()
				.getLogInPopUp()
				.insertMobileIDCode(Constants.mobileIdCode)
				.insertMobilePhoneNumber(Constants.mobilePhoneNumber)
				.clickLoginWithMobileID() 
				.getReportImproperPopUp()
				.setImproperReason()
				.setImproperDescription()
				.clickNotify()
				.getNotificationIsSentText();
		Assert.assertEquals(Constants.reportedText, improperContentIsReported);
	}
	
	@Test
	public void ReportImproperTests_AnonymousUserLogsInWithStuudium_PortfolioIsReported() {

		String improperContentIsReported = goToLandingPage()
				.logoutIfLoggedIn()
				.openPortfolio()
				.clickActionsMenu()
				.clickReportImproperContent()
				.getLogInPopUp()
				.clickLoginWithStuudium()
				.insertUsernameAndPassword(Constants.stuudiumUser, Constants.stuudiumPswd)
				.submitStuudium()
				.clickGivePermission()
				.getReportImproperPopUp()
				.setImproperReason()
				.setImproperDescription()
				.clickNotify()
				.getNotificationIsSentText();
		Assert.assertEquals(Constants.reportedText, improperContentIsReported);
	}

}
