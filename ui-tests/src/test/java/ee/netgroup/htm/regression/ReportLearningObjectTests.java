package ee.netgroup.htm.regression;

import ee.netgroup.htm.api.EkoolikottApi;
import ee.netgroup.htm.helpers.Constants;
import ee.netgroup.htm.BaseTest;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static ee.netgroup.htm.api.UserRole.USER;
import static ee.netgroup.htm.page.LandingPage.getLandingPage;
import static ee.netgroup.htm.page.LandingPage.goToLandingPage;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class ReportLearningObjectTests extends BaseTest {
    @BeforeClass
    public void openHomePage() {
        goToLandingPage().logoutIfLoggedIn();
    }

    @AfterMethod()
    public void logOut() {
        getLandingPage().logoutIfLoggedIn();
    }

    @Test
    public void material_is_reported_after_login_with_mobile_id() {
        EkoolikottApi.setUserRole("60001019906", USER);
        String improperContentIsReported = getLandingPage()
                .openFirstMaterial()
                .clickActionsMenu()
                .clickReportImproperContent()
                .getLogInPopUp()
                .insertMobileIDCode(Constants.mobileIdCode)
                .insertMobilePhoneNumber(Constants.mobilePhoneNumber)
                .clickLoginWithMobileID()
                .getReportImproperPopUp()
                .setImproperReason()
                .setImproperDescription()
                .clickNotifyMaterial()
                .getNotificationIsSentText();
        assertThat(improperContentIsReported, is(Constants.reportedText));
    }

    @Test
    public void portfolio_is_reported_after_login_with_stuudium() {
        EkoolikottApi.setUserRole("30001010004", USER);

        String improperContentIsReported = getLandingPage()
                .openFirstMaterial()
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
                .clickNotifyPortfolio()
                .getNotificationIsSentText();
        assertThat(improperContentIsReported, is(Constants.reportedText));
    }

    @Test
    public void portfolio_is_reported_after_login_with_ekool() {
        EkoolikottApi.setUserRole("38011550077", USER);

        String improperContentIsReported = getLandingPage()
                .openFirstMaterial()
                .clickActionsMenu()
                .clickReportImproperContent()
                .getLogInPopUp()
                .clickLoginWithEKool()
                .insertUsernameAndPassword(Constants.eSchoolUser, Constants.eSchoolPswd)
                .clickSubmitLogin()
                .getReportImproperPopUp()
                .setImproperReason()
                .setImproperDescription()
                .clickNotifyPortfolio()
                .getNotificationIsSentText();
        assertThat(improperContentIsReported, is(Constants.reportedText));
    }
}
