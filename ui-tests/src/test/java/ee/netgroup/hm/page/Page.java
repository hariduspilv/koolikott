package ee.netgroup.hm.page;

import org.openqa.selenium.By;

import ee.netgroup.hm.helpers.Helpers;
import ee.netgroup.hm.tests.SeleniumUser;

public abstract class Page extends SeleniumUser{
	
	private By markAsReviewed = By.xpath("//button[@data-ng-click='button.onClick()']");
	

	public Page markContentIsReviewed() {
		getDriver().findElement(markAsReviewed).click();
		Helpers.waitForSeconds(1000);
		return this;		
	}

	public boolean isContentReviewed() {
        return getDriver().findElement(markAsReviewed).isDisplayed();
	}

}
