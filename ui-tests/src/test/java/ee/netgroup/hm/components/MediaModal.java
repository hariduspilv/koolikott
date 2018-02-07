package ee.netgroup.hm.components;

import org.openqa.selenium.By;

import ee.netgroup.hm.helpers.Arrays;
import ee.netgroup.hm.helpers.Helpers;
import ee.netgroup.hm.page.EditPortfolioPage;

public class MediaModal extends Components{
	
	private static By mediaUrl = By.xpath("//input[@data-ng-model='media.url']");
	private static By mediaTitle = By.xpath("//input[@data-ng-model='media.title']");
	private static By mediaAuthorCheckbox = By.xpath("//span[@data-translate='I_AM_AUTHOR']");
	private static By mediaSource = By.xpath("//input[@data-ng-model='media.source']");
	private static By mediaLicenseType = By.xpath("//md-select[@data-ng-model='media.licenseType']");
	private static By mediaLicenseTypeOption = By.xpath("//md-option[@data-translate='LICENSETYPE_LONG_NAME_CCBYNCND']");
	private static By addMediaButton = By.xpath("//span[@data-translate='BUTTON_ADD_MEDIA']");
	

	public MediaModal setMediaLink() {
		Helpers.waitForMilliseconds(1000);
		getDriver().findElement(mediaUrl).click();
		getDriver().findElement(mediaUrl).click();
		getDriver().findElement(mediaUrl).sendKeys("jpeg.org/images/jpeg2000-home.jpg");
		return new MediaModal();
	}
	
	public MediaModal setMediaTitle() {
		getDriver().findElement(mediaTitle).sendKeys(Helpers.randomElement(Arrays.mediaTitlesArray));
		return new MediaModal();
	}
	
	public MediaModal setMediaAuthor() {
		getDriver().findElement(mediaAuthorCheckbox).click();
		return new MediaModal();
	}
	
	public MediaModal setMediaSource() {
		getDriver().findElement(mediaSource).sendKeys("http://a" + Helpers.generateRandomUrl());
		return new MediaModal();
	}
	
	public MediaModal setMediaLicenseType() {
		getDriver().findElement(mediaLicenseType).click();
		getDriver().findElement(mediaLicenseTypeOption).click();
		return new MediaModal();
	}

	public EditPortfolioPage clickAddMedia() {
		Helpers.waitForClickable(addMediaButton);
		getDriver().findElement(addMediaButton).click();
		return new EditPortfolioPage();
	}

}