package ee.netgroup.htm.page;

import com.codeborne.selenide.Condition;
import ee.netgroup.htm.components.*;
import ee.netgroup.htm.helpers.Arrays;
import ee.netgroup.htm.helpers.Constants;
import ee.netgroup.htm.helpers.Helpers;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static org.openqa.selenium.Keys.ENTER;

public class MaterialPage extends LearningObjectPage {

    private By publisherName = By.xpath("//a[@data-ng-repeat='publisher in material.publishers']");
    private By selectMaterialBox = By.xpath("//div[@class='card-cover  audiotrack']");
    private By materialType = By.cssSelector("p [data-translate=AUDIO]");
    private By materialDescription = By.xpath("//div[@data-ng-bind-html='getCorrectLanguageString(material.descriptions)']");
    private By editMaterial = By.xpath("//button[@data-ng-click='edit()']");
    private By creatorName = By.xpath("//p[@data-ng-if='!isNullOrZeroLength(material.authors)']");
    private By deleteMaterial = By.xpath("//button[@data-ng-click='confirmMaterialDeletion()']");
    private By likeIcon = By.xpath("//div[@data-ng-click='$ctrl.like()']");
    private By isLiked = By.xpath("//span[@data-ng-bind='$ctrl.rating.likes']");
    private By selectedStar = By.xpath("//md-icon[@data-ng-if='$ctrl.hasFavorited']");
    private By unselectedStar = By.xpath("//div[@class='md-icon-button md-button favorite']");
    private By tagRow = By.xpath("(//input[starts-with(@id, 'fl-input-')])");
    private By addedTags = By.cssSelector("dop-tags .md-chips > md-chip");
    private By showMoreButton = By.xpath("//button[@ng-click='$ctrl.showMore()']");
    private By insertTag = By.xpath("(//input[starts-with(@id, 'fl-input-')])");
    private String newTag = Helpers.generateNewTag();
    private By reportTagButton = By.xpath("//button[@ng-click='$ctrl.reportTag($event)']");
    private By improperContent = By.xpath("//span[@data-translate='REPORT_IMPROPER']");
    private By declineButton = By.xpath("//button[1][@data-ng-click='button.onClick($ctrl)']");
    private By materialUrl = By.className("embedded-material__link");
    private By markAsReviewed = By.cssSelector(".error-message > div > div > button");
    private By acceptButton = By.xpath("//button[2][@data-ng-click='button.onClick($ctrl)']");
    private By addTagField = By.cssSelector("#addTagForm input");


    public static MaterialPage getMaterialPage() {
        return new MaterialPage();
    }

    public String getPublisherName() {
        return $(publisherName).shouldBe(Condition.visible).getText();
    }

    public String getMatchingPublisherName(String publisher) {
        return $$(publisherName).find(Condition.text(publisher)).getText();
    }

    public String getMaterialType() {
        Helpers.waitForVisibility(selectMaterialBox);
        Helpers.moveToElement(selectMaterialBox);
        return $(materialType).shouldBe(Condition.visible).getText();
    }

    public String getMaterialDescription() {
        return $(materialDescription).shouldBe(Condition.visible).getText();
    }

    public MaterialPage clickActionsMenu() {
        Helpers.waitForClickable(Constants.actionsMenu).click();
        return this;
    }

    public CreateMaterialModal clickEditMaterial() {
        Helpers.waitForVisibility(editMaterial).sendKeys(Keys.ENTER);
        return new CreateMaterialModal();
    }

    public String getCreatorName() {
        return Helpers.waitForVisibility(creatorName).getText();
    }

    public ConfirmationModal clickDeleteMaterial() {
        Helpers.waitForClickable(deleteMaterial).click();
        return new ConfirmationModal();
    }

    public String getDeletedBannerText() {
        return Helpers.waitForVisibility(Constants.bannerText).getText();
    }

    public MaterialPage likeMaterial() {
        Helpers.waitForClickable(likeIcon).click();
        return this;
    }

    public String getLikesNumber() {
        return Helpers.waitForVisibility(isLiked).getText();
    }

    public boolean starIsSelected() {
        return Helpers.waitForVisibility(selectedStar).isDisplayed();
    }

    public MaterialPage unselectStar() {
        Helpers.waitForVisibility(selectedStar);
        Helpers.waitForClickable(selectedStar).click();
        return this;
    }

    public boolean starIsUnselected() {
        return Helpers.waitForVisibility(unselectedStar).isDisplayed();
    }

    public MaterialPage insertTags() {
        WebElement tagRowWebElement = Helpers.waitForVisibility(tagRow);
        for (int i = 0; i < 13; i++) {
            tagRowWebElement.sendKeys(Helpers.randomElement(Arrays.tagsArray) + "");
            tagRowWebElement.sendKeys(Keys.ENTER);
        }
        return this;
    }

    public boolean showMoreButtonIsDisplayed() {
        return Helpers.waitForVisibility(showMoreButton).isDisplayed();
    }

    public String getUnreviewedBannerText() {
        return Helpers.waitForVisibility(Constants.bannerText).getText();
    }

    public String getNotificationIsSentText() {
        return Helpers.waitForVisibility(NotificationToast.getElementLocator()).getText();
    }

    public MaterialPage addNewTag() {
        Helpers.waitForVisibility(insertTag).sendKeys(newTag);
        Helpers.waitForVisibility(insertTag).sendKeys(Keys.ENTER);
        return this;
    }

    public ReportImproperModal reportImproperTag() {
        Helpers.waitForClickable(reportTagButton).click();
        return new ReportImproperModal();
    }

    public ReportImproperModal clickReportImproperContent() {
        $(improperContent).shouldBe(Condition.visible).click();
        return new ReportImproperModal();
    }

    public MaterialPage showMaterialComments() {
        return Comments.showMaterialComments();
    }

    public ReportImproperModal reportImproperComment() {
        return Comments.reportImproperComment();
    }

    public MaterialPage markAsReviewed() {
        Helpers.waitForClickable(markAsReviewed).click();
        return this;
    }

    public String getChangedLinkBannerText() {
        return Helpers.waitForVisibility(Constants.bannerText).getText();
    }

    public MaterialPage markChangesDeclined() {
        Helpers.waitForClickable(declineButton).click();
        return this;
    }

    public MaterialPage markChangesAccepted() {
        Helpers.waitForClickable(acceptButton).click();
        return this;
    }

    public String getMaterialUrlText() {
        return Helpers.waitForVisibility(materialUrl).getText();
    }


    public boolean isLearningObjectChanged() {
        WebElement materialChangedBannerHeader = Helpers.waitForVisibility(By.id("error-message-heading"));
        return materialChangedBannerHeader != null;
    }

    public String getMaterialTitle() {
        return Helpers.waitForVisibility(By.cssSelector(".learning-object-title > a > span")).getText();
    }

    public boolean hasSubjectArea(String subjectAreaName) {
        try {
            Helpers.waitForVisibility(By.cssSelector("span[data-translate='" + subjectAreaName + "']"));
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public int getNumberOfTags() {
        Helpers.waitForVisibility(By.tagName("dop-tags"));
        return getWebDriver().findElements(addedTags).size();
    }

    public MaterialPage addTag(String tag) {
        WebElement addTagElement = Helpers.waitForClickable(addTagField);
        addTagElement.sendKeys(tag);
        addTagElement.sendKeys(ENTER);
        return this;
    }
}
