package ee.hm.dop.components;

import org.openqa.selenium.By;

import ee.hm.dop.helpers.PageHelpers;
import ee.hm.dop.page.StuudiumPage;
import ee.hm.dop.page.eKoolPage;

public class LoginDialogPopup extends PageComponent {
	
	private By mobileIdCodeField = By.id("login-personal-code");
    private By mobilePhoneNumberField = By.id("login-phone-number");
    private By mobileLoginButton= By.cssSelector("button[id*='login-mobile-id-button']");
    private By eKoolLoginButton = By.id("login-ekool-button");
    private By stuudiumButton = By.id("login-stuudium-button");
    
    
    public LoginDialogPopup insertMobileIDCode(String mobileIDCode) {
        PageHelpers.waitForVisibility(mobileIdCodeField);
    	getDriver().findElement(mobileIdCodeField).sendKeys(mobileIDCode);
        return this;
    }
    
    public eKoolPage clickLoginWithEKool() {
        getDriver().findElement(eKoolLoginButton).click();
        return new eKoolPage();
    }
    
    public StuudiumPage clickLoginWithStuudium() {
        getDriver().findElement(stuudiumButton).click();
        return new StuudiumPage();
    }
    
    public LoginDialogPopup insertMobilePhoneNumber(String mobilePhoneNumber) {
        PageHelpers.waitForVisibility(mobilePhoneNumberField);
    	getDriver().findElement(mobilePhoneNumberField).sendKeys(mobilePhoneNumber);
        return this;
    }
    
    public LoginConfirmationPopup clickLoginWithMobileID() {
        getDriver().findElement(mobileLoginButton).click();
        return new LoginConfirmationPopup();
    }

}
