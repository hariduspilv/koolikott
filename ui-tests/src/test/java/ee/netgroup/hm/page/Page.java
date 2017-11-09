package ee.netgroup.hm.page;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import ee.netgroup.hm.helpers.Helpers;
import ee.netgroup.hm.tests.SeleniumUser;

public abstract class Page extends SeleniumUser{
	
	private By markAsReviewed = By.xpath("//button[@aria-label='Märgi ülevaadatuks']");
	private By errorBanner = By.xpath("//md-toolbar[@class='error-message _md _md-toolbar-transitions']");
	private By markProperButton = By.xpath("//button[@aria-label='Märgi sisu sobivaks']");
	private By restoreButton = By.xpath("//button[@aria-label='Taasta']");
	

	public Page markContentIsReviewed() {
		getDriver().findElement(markAsReviewed).click();
		return this;		
	}

	public boolean isErrorBannerHidden() {
		Helpers.waitForMilliseconds(1000);
		return getDriver().findElements(errorBanner).size() < 1;
	}
	
	public Page markContentAsNotImproper() {
		Helpers.moveToElement(markProperButton);
		getDriver().findElement(markProperButton).sendKeys(Keys.ENTER);
		Helpers.waitForMilliseconds(5000);
		return this;
	}

	public Page restoreDeletedLearningObject() {
		Helpers.waitForClickable(restoreButton);
		getDriver().findElement(restoreButton).click();
		Helpers.waitForMilliseconds(3000);
		return this;
	}

}

