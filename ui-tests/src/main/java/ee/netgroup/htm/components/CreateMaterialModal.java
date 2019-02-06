package ee.netgroup.htm.components;

import com.codeborne.selenide.Condition;
import ee.netgroup.htm.helpers.Arrays;
import ee.netgroup.htm.helpers.Constants;
import ee.netgroup.htm.helpers.Helpers;
import ee.netgroup.htm.page.EditPortfolioPage;
import ee.netgroup.htm.page.MaterialPage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import static com.codeborne.selenide.Condition.disappears;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static ee.netgroup.htm.enums.VocationalEducationSubjectArea.AGRICULTURE;
import static ee.netgroup.htm.helpers.Helpers.generateUrl;
import static ee.netgroup.htm.util.JSWaiter.waitForJQueryAndAngularToFinishRendering;

public class CreateMaterialModal {

    private By linkField = By.id("add-material-url-input");
    private By materialTitle = By.name("title");
    private By insertPhoto = By.xpath("//div[@ng-model='newPicture']");
    private By descriptionField = By.id("add-material-description-input");
    private By educationalContext = By.xpath("(//md-select[@id='taxonEducationalSelect'])[2]");
    private By preschoolEducation = By.cssSelector("md-option[data-translate='PRESCHOOLEDUCATION']");
    private By targetGroup = By.xpath("//form[contains(@name, 'targetGroupForm')]");
    private By selectedTargetGroup = By.xpath("(//md-option[contains(@value, 'ZERO_FIVE')])[2]");
    private By closeButton = By.xpath("//md-icon[@aria-label='close']");
    private By createMaterialButton = By.id("create-material-button");
    private By deletedMaterialValidationError = By.xpath("//div[@class='md-input-message-animation']");
    private By existingMaterialValidationError = By.cssSelector("div.md-input-message-animation > span");
    private By authorFirstName = By.id("material-author-0-name");
    private By authorSurname = By.name("surname");
    private By selfAuthorCheckbox = By.xpath("(//md-checkbox)[2]");
    private By publisherName = By.xpath("//input[@data-ng-model='material.publishers[0].name']");
    private By illustrationAuthorCheckbox = By.xpath("//md-checkbox[@data-ng-click='$ctrl.setAuthorToUser()']");
    private By illustrationSource = By.name("pictureSource");
    private By additionalData = By.id("open-additional-info");
    private By reviewLink = By.name("peerReview");
    private By licenseTypeSelect = By.name("licenseType");
    private By defaultLicenseTypeOption = By.xpath("(//md-option[@data-translate='LICENSETYPE_LONG_NAME_CCBY'])[2]");
    private By subjectAreaForVocationalEducation = By.cssSelector("form[name='taxonForm'] > md-input-container > md-select#taxonDomainForVocationaleducationSelect");
    private By subjectAreaForPreschoolEducation = By.cssSelector("form[name='taxonForm'] > md-input-container > md-select#taxonDomainForPreschooleducationSelect");
    private By selectedLanguage = By.cssSelector("#add-material-language-select md-select-value.md-select-value div.md-text span");
    private By updateMaterialButton = By.id("create-material-button");
    private By insertFile = By.xpath("//span[@data-translate='FILE_WORD']");
    private static By modalLocator = By.id("dialog-add-material");

    public CreateMaterialModal() {
        waitForJQueryAndAngularToFinishRendering();
    }

    public static By getModalLocator() {
        return modalLocator;
    }

    public CreateMaterialModal setRandomHyperLink() {
        Helpers.waitForClickable(linkField).click();
        $(linkField).shouldBe(Condition.visible).clear();
        Helpers.sendKeysByChar($(linkField), generateUrl());
        return this;
    }

    public CreateMaterialModal setTitle() {
        $(materialTitle).shouldBe(Condition.visible).click();
        $(materialTitle).sendKeys(Helpers.randomElement(Arrays.materialTitlesArray));
        return this;
    }

    public CreateMaterialModal setDescription() {
        Helpers.waitForVisibility(descriptionField).sendKeys(Helpers.randomElement(Arrays.descriptionArray));
        return this;
    }

    public CreateMaterialModal selectDefaultTargetGroup() {
        getWebDriver().findElements(targetGroup).get(1).click();
        Helpers.waitForClickable(selectedTargetGroup).click();
        getWebDriver().findElements(closeButton).get(2).click();
        return this;
    }

    public MaterialPage clickCreateMaterial() {
        Helpers.moveToElement(createMaterialButton);
        Helpers.waitForClickable(createMaterialButton).click();
        Helpers.waitUntilNotVisible(modalLocator);
        return new MaterialPage();
    }

    public CreateMaterialModal insertDeletedMaterialUrl() {
        Helpers.waitForClickable(linkField).sendKeys(Constants.deletedMaterial);
        return this;
    }

    public String getValidationError() {
        return Helpers.waitForVisibility(deletedMaterialValidationError).getText();
    }

    public CreateMaterialModal insertExistingMaterialUrl() {
        Helpers.waitForClickable(linkField).sendKeys(Constants.existingMaterial);
        return this;
    }

    public String getExistingMaterialValidationError() {
        return Helpers.waitForVisibility(existingMaterialValidationError).getText();
    }

    public CreateMaterialModal setAuthorFirstName() {
        Helpers.waitForVisibility(authorFirstName).sendKeys(Helpers.randomElement(Arrays.firstNamesArray));
        return this;
    }

    public CreateMaterialModal setAuthorSurName() {
        Helpers.waitForVisibility(authorSurname).sendKeys(Helpers.randomElement(Arrays.surnamesArray));
        return this;
    }

    public CreateMaterialModal setPublisherName() {
        Helpers.waitForClickable(publisherName).sendKeys("Pegasus");
        return this;
    }

    public EditPortfolioPage clickCreateMaterialPortfolio() {
        Helpers.waitForClickable(createMaterialButton).click();
        return new EditPortfolioPage();
    }

    public CreateMaterialModal checkIllustrationIamAuthor() {
        $(illustrationAuthorCheckbox).shouldBe(Condition.visible).click();
        return this;
    }

    public CreateMaterialModal addIllustrationSource() {
        $(illustrationSource).shouldBe(Condition.visible).sendKeys("Allikas Automaattest");
        return this;
    }

    public CreateMaterialModal clickAdditionalData() {
        Helpers.waitForClickable(additionalData).click();
        return this;
    }

    public CreateMaterialModal addReviewLink() {
        Helpers.waitForVisibility(reviewLink).sendKeys(generateUrl());
        return this;
    }

    public CreateMaterialModal selectDefaultLicenseType() {
        $(licenseTypeSelect).shouldBe(Condition.visible).click();
        $(defaultLicenseTypeOption).shouldBe(Condition.visible).click();

        $(defaultLicenseTypeOption).waitUntil(disappears, 2000);

        return this;
    }

    public String getMaterialLanguage() {
        return Helpers.waitForVisibility(selectedLanguage).getText();
    }

    public MaterialPage clickUpdateMaterial() {
        Helpers.waitForVisibility(updateMaterialButton).click();
        return new MaterialPage();
    }

    public CreateMaterialModal uploadFile() {
        Helpers.waitForClickable(insertFile).click();
        Helpers.uploadFile();
        return this;
    }


    public CreateMaterialModal selectVocationalEducation() {
        Helpers.waitForClickable(educationalContext).click();
        Helpers.waitForClickable(By.xpath("(//md-option[@data-translate='VOCATIONALEDUCATION'])[2]")).click();
        return this;
    }

    public CreateMaterialModal selectPreschoolEducation() {
        Helpers.waitForClickable(educationalContext).click();
        $$(preschoolEducation).get(1).click();
        return this;
    }

    public CreateMaterialModal setIsSelfAuthored() {
        $(selfAuthorCheckbox).shouldBe(Condition.visible).click();
        return this;
    }

    public CreateMaterialModal selectSubjectAreaForVocationEducation(String subjectAreaTranslationKey) {
        $(subjectAreaForVocationalEducation).shouldBe(Condition.visible).click();
        $$("md-option[data-translate=\"" + subjectAreaTranslationKey + "\"]").last().click();
        $$("md-option[data-translate=\"" + subjectAreaTranslationKey + "\"]").last().waitUntil(disappears, 2000);
        return this;
    }

    public CreateMaterialModal selectSubjectAreaForPreschoolEducation(String subjectAreaTranslationKey) {
        $(subjectAreaForPreschoolEducation).shouldBe(Condition.visible).click();
        $$(By.cssSelector("md-option[data-translate=\"" + subjectAreaTranslationKey + "\"]")).last().shouldBe(Condition.visible).click();
        return this;
    }

    public MaterialPage createDefaultMaterial() {
        return setTitle("Default Material")
                .setRandomHyperLink()
                .selectDefaultLicenseType()
                .setIsSelfAuthored()
                .selectVocationalEducation()
                .selectSubjectAreaForVocationEducation(AGRICULTURE.getTranslationKey())
                .clickCreateMaterial();
    }

    public CreateMaterialModal setTitle(String title) {
        WebElement materialTitleWebElement = Helpers.waitForClickable(materialTitle);
        materialTitleWebElement.click();
        Helpers.sendKeysByChar(materialTitleWebElement, title);
        return this;
    }
}
