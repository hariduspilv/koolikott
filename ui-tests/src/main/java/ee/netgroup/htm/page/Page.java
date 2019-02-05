package ee.netgroup.htm.page;

import com.codeborne.selenide.Condition;
import ee.netgroup.htm.components.NotificationToast;
import ee.netgroup.htm.helpers.Constants;
import ee.netgroup.htm.helpers.Helpers;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.back;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;

public class Page {

    private static By pageLogo = By.id("logo");
    private By markProperButton = By.xpath("//button[@aria-label='Märgi sisu sobivaks']");
    private By restoreButton = By.xpath("//md-icon[text()='restore_page']");
    private By pageLanguageBtn = By.id("header-language-icon");
    private By estonian = By.id("language-select-et");
    private By english = By.id("language-select-en");
    private By russian = By.id("language-select-eng");



    public void clickOnPageLogo() {
        $(pageLogo).click();
    }

    public boolean isBannerToolbarHidden() {
        return getWebDriver().findElements(Constants.bannerToolbar).size() < 1;
    }

    public Page markContentAsNotImproper() {
        Helpers.moveToElement(markProperButton);
        getWebDriver().findElement(markProperButton).sendKeys(Keys.ENTER);
        return this;
    }

    public Page restoreDeletedLearningObject() {
        Helpers.waitForClickable(restoreButton);
        getWebDriver().findElement(restoreButton).click();
        return this;
    }

    public void waitForLoggedInNotificationToClose() {
        Helpers.waitUntilNotVisible(NotificationToast.getElementLocator());
    }

    public Page clickChangePageLanguageBtn() {
        $(pageLanguageBtn).shouldBe(Condition.visible).click();
        return this;
    }

    public Page chooseEstonianLanguage() {
        $(estonian).shouldBe(Condition.visible).click();
        return this;
    }

    public Page chooseEnglishLanguage() {
        $(english).shouldBe(Condition.visible).click();
        return this;
    }

    public Page chooseRussianLanguage() {
        $(russian).shouldBe(Condition.visible).click();
        return this;
    }

    public LearningObjectPage navigateBackToPreviousPage() {
        back();
        return new LearningObjectPage();
    }

}

