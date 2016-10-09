package ee.hm.dop.page;

import org.openqa.selenium.By;

public class StuudiumPage extends Page {
	
	private By username = By.id("username");
	private By password = By.id("password");
	private By submit = By.id("standard_login_submit");
	
	public StuudiumPage insertUsernameAndPassword(String userName, String passWord) {
		getDriver().findElement(username).clear();
		getDriver().findElement(username).sendKeys(userName);
		getDriver().findElement(password).clear();
        getDriver().findElement(password).sendKeys(passWord);
        return this;
    }
	
	public StuudiumConfirmationPage submitStuudium() {
        getDriver().findElement(submit).click();
        return new StuudiumConfirmationPage();
    }

}
