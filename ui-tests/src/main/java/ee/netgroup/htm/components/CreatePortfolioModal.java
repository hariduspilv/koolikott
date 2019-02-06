package ee.netgroup.htm.components;

import com.codeborne.selenide.Condition;
import ee.netgroup.htm.helpers.Arrays;
import ee.netgroup.htm.helpers.Helpers;
import ee.netgroup.htm.page.EditPortfolioPage;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static ee.netgroup.htm.enums.VocationalEducationSubjectArea.AGRICULTURE;

public class CreatePortfolioModal {

    private static final String additionalWord = "(Copy)";
    private By portfolioTitle = By.id("add-portfolio-title-input");
    private By educationalContext = By.xpath("(//md-select[@id='taxonEducationalSelect'])[2]");
    private By preschoolEducation = By.cssSelector("md-option[data-translate='PRESCHOOLEDUCATION']");
    private By subjectAreaForPreschoolEducation = By.cssSelector("form[name='taxonForm'] > md-input-container > md-select#taxonDomainForPreschooleducationSelect");
    private By subject = By.cssSelector("md-option[data-translate='DOMAIN_ESTONIAN']");
    private By ageGroup = By.xpath("//form[@name='targetGroupForm']");
    private By age = By.xpath("(//md-option[contains(@value, 'ZERO_FIVE')])[2]");
    private By closeButton = By.cssSelector("md-icon[aria-label='close']");
    private By insertPhoto = By.xpath("//div[@ng-model='newPicture']");
    private By descriptionField = By.id("add-portfolio-description-input");
    private By createPortfolio = By.id("add-portfolio-create-button");
    private By savePortfolio = By.id("add-portfolio-edit-button");
    private By licenseSelect = By.name("licenseType");
    private By defaultLicenseTypeOption = By.xpath("(//md-option[@data-translate='LICENSETYPE_LONG_NAME_CCBY'])[2]");
    private By portfolioIllustrationAuthor = By.id("newPortfolio-picture-author-");
    private By portfolioIllustrationSource = By.name("pictureSource");
    private By portfolioIllustrationLicenseType = By.id("pictureLicenseTypeSelect");
    private By portfolioIllustrationLicenseTypeOption = By.cssSelector("md-option[data-translate='LICENSETYPE_LONG_NAME_CCBYNCSA']");

    public CreatePortfolioModal setTitle() {
        Helpers.waitForVisibility(portfolioTitle).sendKeys(Helpers.randomElement(Arrays.portfolioTitlesArray));
        return this;
    }

    public CreatePortfolioModal setTitle(String title) {
        Helpers.sendKeysByChar(Helpers.waitForVisibility(portfolioTitle), title);
        return this;
    }

    public CreatePortfolioModal selectEducationalContext_Preschool() {
        Helpers.waitForClickable(educationalContext).click();
        $$(preschoolEducation).get(1).click();
        return this;
    }

    public CreatePortfolioModal selectSubjectAreaForPreschoolEducation(String subjectAreaTranslationKey) {
        $(subjectAreaForPreschoolEducation).shouldBe(Condition.visible).click();
        $$(By.cssSelector("md-option[data-translate=\"" + subjectAreaTranslationKey + "\"]")).last().shouldBe(Condition.visible).click();
        return this;
    }

    public CreatePortfolioModal selectDefaultSubjectAreaForPreschoolEducation() {
        $(subjectAreaForPreschoolEducation).shouldBe(Condition.visible).click();
        $(subject).shouldBe(Condition.visible).click();
        return this;
    }

    public CreatePortfolioModal selectDefaultAgeGroup() {
        $$(ageGroup).last().shouldBe(Condition.visible).click();
        Helpers.waitForClickable(age).click();
        $$(closeButton).last().click();
        return this;
    }

    public CreatePortfolioModal uploadPhoto() {
        Helpers.waitForVisibility(insertPhoto);
        Helpers.waitForClickable(insertPhoto).click();
        Helpers.uploadFile();
        return this;
    }

    public CreatePortfolioModal setDescription() {
        Helpers.waitForVisibility(descriptionField).sendKeys(Helpers.randomElement(Arrays.descriptionArray));
        return this;
    }

    public EditPortfolioPage clickCreate() {
        $(createPortfolio).shouldBe(Condition.visible).click();
        return new EditPortfolioPage();
    }

    public CreatePortfolioModal insertSpecificPortfolioTitle() {
        Helpers.waitForVisibility(portfolioTitle).sendKeys(additionalWord);
        return this;
    }

    public EditPortfolioPage clickSave() {
        $(savePortfolio).shouldBe(Condition.visible).click();
        return new EditPortfolioPage();
    }

    public CreatePortfolioModal setDefaultLicenseType() {
        Helpers.waitForClickable(licenseSelect).click();
        Helpers.waitForClickable(defaultLicenseTypeOption).click();
        return this;
    }

    public CreatePortfolioModal setPortfolioIllustrationAuthor() {
        Helpers.waitForVisibility(portfolioIllustrationAuthor).sendKeys(Helpers.randomElement(Arrays.surnamesArray));
        return this;
    }

    public CreatePortfolioModal setPortfolioIllustrationSource() {
        Helpers.waitForVisibility(portfolioIllustrationSource).sendKeys("Allikas Automaattest");
        return this;
    }

    public CreatePortfolioModal setPortfolioIllustrationLicenseType() {
        Helpers.waitForClickable(portfolioIllustrationLicenseType).click();
        $$(portfolioIllustrationLicenseTypeOption).last().shouldBe(Condition.visible).click();
        return this;
    }

    public CreatePortfolioModal selectSubjectAreaForVocationEducation(String subjectAreaTranslationKey) {
        Helpers.waitForClickable(By.cssSelector("form[name='taxonForm'] > md-input-container > md-select#taxonDomainForVocationaleducationSelect")).click();
        Helpers.waitForClickable(By.xpath("(//md-option[@data-translate=\"" + subjectAreaTranslationKey + "\"])[2]")).click();
        return this;
    }

    public CreatePortfolioModal selectVocationalEducation() {
        Helpers.waitForClickable(educationalContext).click();
        Helpers.waitForClickable(By.xpath("(//md-option[@data-translate='VOCATIONALEDUCATION'])[2]")).click();
        return this;
    }

    public EditPortfolioPage createDefaultPortfolio() {
        return setTitle("Default Kogumik")
                .setDefaultLicenseType()
                .selectVocationalEducation()
                .selectSubjectAreaForVocationEducation(AGRICULTURE.getTranslationKey())
                .clickCreate();
    }

}
