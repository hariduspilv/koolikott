package ee.netgroup.htm.page;

import ee.netgroup.htm.helpers.Constants;
import ee.netgroup.htm.helpers.Helpers;
import org.openqa.selenium.By;

public class EKoolPage extends Page {

    private By username = By.id("username");
    private By password = By.id("password");
    private By login = By.cssSelector("input.button-submit");


    public EKoolPage insertUsernameAndPassword(String user, String pswd) {
        Helpers.waitForVisibility(username).sendKeys(user);
        Helpers.waitForVisibility(password).sendKeys(pswd);
        return this;
    }

    public LandingPage clickSubmitLogin() {
        Helpers.waitForClickable(login).click();
        Helpers.waitForVisibility(Constants.loginConfirmationText);
        return new LandingPage();
    }

}
