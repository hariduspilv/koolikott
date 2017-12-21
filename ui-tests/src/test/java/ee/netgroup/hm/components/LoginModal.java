package ee.netgroup.hm.components;

import org.openqa.selenium.By;
import ee.netgroup.hm.helpers.Constants;
import ee.netgroup.hm.helpers.Helpers;
import ee.netgroup.hm.page.LandingPage;
import ee.netgroup.hm.page.StuudiumPage;
import ee.netgroup.hm.page.EKoolPage;

public class LoginModal extends Components{
	
	private By mobileIdCodeField = By.name("idCode");
    private By mobilePhoneNumberField = By.name("phoneNumber");
    private By mobileLoginButton = By.xpath("//button[@class='md-raised login-button-mobiilid md-button md-ink-ripple']");
    private By eKoolLoginButton = By.xpath("//button[@data-ng-click='ekoolAuth()']");
    private By stuudiumButton = By.xpath("//button[@data-ng-click='stuudiumAuth()']");
    private static By logInText = By.xpath("//h2[@data-ng-bind='title']");
    
	public LoginModal insertMobileIDCode(String mobileIdCode) {
		Helpers.waitForMilliseconds(1000);
		Helpers.waitForVisibility(mobileIdCodeField);
    	getDriver().findElement(mobileIdCodeField).sendKeys(mobileIdCode);
    	Helpers.waitForMilliseconds(1000);
        return this;
	}
	
	public LoginModal insertMobilePhoneNumber(String mobilePhoneNumber) {
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
		Helpers.waitForMilliseconds(1000);
        getDriver().findElement(eKoolLoginButton).click();
        return new EKoolPage();
	}

	public StuudiumPage clickLoginWithStuudium() {
		Helpers.waitForMilliseconds(1000);
        getDriver().findElement(stuudiumButton).click();
        return new StuudiumPage();
	}

	public static String getLogginInIsRequiredText() {
		return getDriver().findElement(logInText).getText();
	}

	public static LoginModal getLogInPopUp() {
		return new LoginModal();
	}

	


}
