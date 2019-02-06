package ee.netgroup.htm.login;

import org.testng.annotations.Test;

import static ee.netgroup.htm.page.LandingPage.goToLandingPage;
import static org.testng.Assert.assertEquals;

public class LogInTests {

    @Test
    public void MobileID_UserIsLoggedIn() {
        String mobileIdUser = "Mary Änn O’connež-Šuslik Testnumber";
        String mobileidPhoneNr = System.getProperty("mobileid.phonenr");
        String mobileidIdcode = System.getProperty("mobileid.idcode");

        String username = goToLandingPage()
                .logoutIfLoggedIn()
                .clickLogin()
                .insertMobileIDCode(mobileidIdcode)
                .insertMobilePhoneNumber(mobileidPhoneNr)
                .clickLoginWithMobileID()
                .clickOnUserProfile()
                .getUserName();
        assertEquals(mobileIdUser, username);
    }

    @Test
    public void ESchool_UserIsLoggedIn() {
        String ekoolUser = "Peeter Paan";
        String ekoolUsername = System.getProperty("ekool.username");
        String ekoolPassword = System.getProperty("ekool.password");

        String username = goToLandingPage()
                .logoutIfLoggedIn()
                .clickLogin()
                .clickLoginWithEKool()
                .insertUsernameAndPassword(ekoolUsername, ekoolPassword)
                .clickSubmitLogin()
                .clickOnUserProfile()
                .getUserName();
        assertEquals(ekoolUser, username);
    }

    @Test
    public void Stuudium_UserIsLoggedIn() {
        String stuudiumUsername = System.getProperty("stuudium.username");
        String stuudiumPassword = System.getProperty("stuudium.password");

        String username = goToLandingPage()
                .logoutIfLoggedIn()
                .clickLogin()
                .clickLoginWithStuudium()
                .insertUsernameAndPassword(stuudiumUsername, stuudiumPassword)
                .submitStuudium()
                .clickGivePermission()
                .clickOnUserProfile()
                .getUserName();
        assertEquals(stuudiumUsername, username);
    }

}
