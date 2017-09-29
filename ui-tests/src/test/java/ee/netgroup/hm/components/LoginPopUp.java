package ee.netgroup.hm.components;

import org.openqa.selenium.By;
import ee.netgroup.hm.helpers.Constants;
import ee.netgroup.hm.helpers.Helpers;
import ee.netgroup.hm.page.LandingPage;
import ee.netgroup.hm.page.StuudiumPage;
import ee.netgroup.hm.page.EKoolPage;

public class LoginPopUp extends Component{
	
	private By mobileIdCodeField = By.id("login-personal-code");
    private By mobilePhoneNumberField = By.id("login-phone-number");
    private By mobileLoginButton= By.cssSelector("button[id*='login-mobile-id-button']");
    private By eKoolLoginButton = By.cssSelector("button[id*='login-ekool-button']");
    private By stuudiumButton = By.cssSelector("button[id*='login-stuudium-button']");

    
	public LoginPopUp insertMobileIDCode(String mobileIdCode) {
		Helpers.waitForSeconds(1000);
		Helpers.waitForVisibility(mobileIdCodeField);
    	getDriver().findElement(mobileIdCodeField).sendKeys(mobileIdCode);
    	Helpers.waitForSeconds(1000);
        return this;
	}
	
	public LoginPopUp insertMobilePhoneNumber(String mobilePhoneNumber) {
	    Helpers.waitForVisibility(mobilePhoneNumberField);
		getDriver().findElement(mobilePhoneNumberField).sendKeys(mobilePhoneNumber);
	    return this;
	}

	public LandingPage clickLoginWithMobileID() {
		getDriver().findElement(mobileLoginButton).click();
		Helpers.waitForVisibility(Constants.loginConfirmationText);
        return new LandingPage();
	}

	public EKoolPage clickLoginWithEKool() {
		Helpers.waitForSeconds(1000);
        getDriver().findElement(eKoolLoginButton).click();
        return new EKoolPage();
	}

	public StuudiumPage clickLoginWithStuudium() {
		Helpers.waitForSeconds(1000);
        getDriver().findElement(stuudiumButton).click();
        return new StuudiumPage();
	}

	


}
