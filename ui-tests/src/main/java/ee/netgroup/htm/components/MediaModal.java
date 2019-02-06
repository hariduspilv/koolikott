package ee.netgroup.htm.components;

import ee.netgroup.htm.helpers.Arrays;
import ee.netgroup.htm.helpers.Helpers;
import ee.netgroup.htm.page.EditPortfolioPage;
import org.openqa.selenium.By;

import static com.codeborne.selenide.WebDriverRunner.getWebDriver;

public class MediaModal {

    private static By mediaUrl = By.xpath("//input[@data-ng-model='media.url']");
    private static By mediaTitle = By.xpath("//input[@data-ng-model='media.title']");
    private static By mediaAuthorCheckbox = By.xpath("//span[@data-translate='I_AM_AUTHOR']");
    private static By mediaSource = By.xpath("//input[@data-ng-model='media.source']");
    private static By mediaLicenseType = By.xpath("//md-select[@data-ng-model='media.licenseType']");
    private static By mediaLicenseTypeOption = By.xpath("//md-option[@data-translate='LICENSETYPE_LONG_NAME_CCBYNCND']");
    private static By addMediaButton = By.xpath("//span[@data-translate='BUTTON_ADD_MEDIA']");


    public MediaModal setMediaLink() {
        getWebDriver().findElement(mediaUrl).click();
        getWebDriver().findElement(mediaUrl).click();
        getWebDriver().findElement(mediaUrl).sendKeys("jpeg.org/images/jpeg2000-home.jpg");
        return this;
    }

    public MediaModal setMediaTitle() {
        getWebDriver().findElement(mediaTitle).sendKeys(Helpers.randomElement(Arrays.mediaTitlesArray));
        return this;
    }

    public MediaModal setMediaAuthor() {
        getWebDriver().findElement(mediaAuthorCheckbox).click();
        return this;
    }

    public MediaModal setMediaSource() {
        Helpers.waitForVisibility(mediaSource).sendKeys(Helpers.generateUrl());
        return this;
    }

    public MediaModal setMediaLicenseType() {
        getWebDriver().findElement(mediaLicenseType).click();
        getWebDriver().findElement(mediaLicenseTypeOption).click();
        return this;
    }

    public EditPortfolioPage clickAddMedia() {
        Helpers.waitForClickable(addMediaButton);
        getWebDriver().findElement(addMediaButton).click();
        return new EditPortfolioPage();
    }

}