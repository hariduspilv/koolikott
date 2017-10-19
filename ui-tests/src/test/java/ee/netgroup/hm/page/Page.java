package ee.netgroup.hm.page;

import org.openqa.selenium.By;

import ee.netgroup.hm.helpers.Helpers;
import ee.netgroup.hm.tests.SeleniumUser;

public abstract class Page extends SeleniumUser{
	
	private By markAsReviewed = By.xpath("//button[@data-ng-click='button.onClick()']");
	private By errorBanner = By.xpath("//div[@class='error-message-body flex']");
	

	public Page markContentIsReviewed() {
		getDriver().findElement(markAsReviewed).click();
		return this;		
	}

	public boolean isErrorBannerHidden() {
		Helpers.waitForMilliseconds(1000);
		return getDriver().findElements(errorBanner).size() < 1;
	}


}

