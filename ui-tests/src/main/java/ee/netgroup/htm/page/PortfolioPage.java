package ee.netgroup.htm.page;

import com.codeborne.selenide.Condition;
import ee.netgroup.htm.components.*;
import ee.netgroup.htm.helpers.Constants;
import ee.netgroup.htm.helpers.Helpers;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static ee.netgroup.htm.util.JSWaiter.waitForJQueryAndAngularToFinishRendering;


public class PortfolioPage extends LearningObjectPage {

    private static final String additionalWord = "(Copy)";
    public static By materialBox = By.cssSelector("div.chapter-embed-card__caption");
    private static By materialTitle = By.cssSelector("h5[data-ng-bind='title']");
    private By visibilityButton = By.id("change-visibility");
    private By portfolioTitle = By.xpath("//h1[@data-ng-bind='portfolio.title']");
    private By deletePortfolio = By.xpath("//button[@data-ng-click='confirmPortfolioDeletion()']");
    private By editPortfolio = By.xpath("//button[@data-ng-click='editPortfolio()']");
    private By shareWithLinkIcon = By.xpath("//md-icon[text()='link']");
    private By improperContent = By.xpath("//span[@data-translate='REPORT_IMPROPER']");
    private By quoteTag = By.tagName("blockquote");
    private By insertTag = By.xpath("(//input[starts-with(@id, 'fl-input-')])");
    private String newTag = Helpers.generateNewTag();
    private By addedTag = By.xpath("//a[contains(@href, 'search/result?q=tag:\"" + newTag + "\"')]");
    private By addToRecommendations = By.xpath("//button[@data-ng-click='recommend()']");
    private By removeFromRecommendations = By.xpath("//button[@data-ng-click='removeRecommendation()']");
    private By tag = By.xpath("//a[@data-ng-click='$ctrl.getTagSearchURL($event, $chip.tag)']");
    private By educationalTaxon = By.xpath("//span[@data-translate='PRESCHOOLEDUCATION']");
    private By reportCommentButton = By.xpath("//button[@ng-click='$ctrl.reportComment($event)']");
    private By reportTagButton = By.xpath("//button[@ng-click='$ctrl.reportTag($event)']");
    private By autocompleteSystemTag = By.xpath("//span[@md-highlight-text='$ctrl.newTag.tagName']");
    private By newSystemTagNotification = By.xpath("//button[contains(text(), 'Ok')]");
    private By markAsReviewed = By.xpath("//button[@aria-label='Märgi ülevaadatuks']");
    private By narrowChapter = By.xpath("//div[@class='chapter-block is-narrow is-narrow-left']");
    private By subchapterTag = By.xpath("//h3[@class='subchapter']");
    private By firstChapterTitle = By.xpath("//h2[@class='chapter-title']");
    private By alignedMaterialBox = By.xpath("//div[@class='chapter-embed-card chapter-embed-card--material chapter-embed-card--float-right chapter-embed-card--loaded']");
    private By publicVisibility = By.xpath("//button[@data-ng-click='makePublic()']");
    private By addedTags = By.cssSelector("dop-tags .md-chips > md-chip");
    private By unreviewedNewLearningObjectToolbar = By.id("error-message-heading");
    private By portfolioDeletedToolbarVisible = By.xpath("//md-toolbar[@data-ng-if='show']");

    public PortfolioPage() {
        waitForJQueryAndAngularToFinishRendering();
    }

    public static PortfolioPage getPortfolioPage() {
        return new PortfolioPage();
    }

    public static String getMaterialTitle() {
        return $$(materialTitle).last().shouldBe(Condition.visible).getText();
    }

    public String getPortfolioTitle() {
        return Helpers.waitForVisibility(portfolioTitle).getText();
    }

    public PortfolioPage checkIfPortfolioTitleMatches(String title) {
        $(portfolioTitle).shouldHave(Condition.text(title));
            return this;
    }

    public PortfolioPage checkThatPortfolioTitleDoesNotMatch(String title) {
        $(portfolioTitle).shouldNotHave(Condition.text(title));
        return this;
    }

    public CreatePortfolioModal clickCopyPortfolio() {
        return FabButton.clickCopyPortfolio();
    }

    public boolean wordWasAddedToPortfolioTitle() {
        return Helpers.waitForVisibility(portfolioTitle).getText().contains(additionalWord);
    }

    public PortfolioPage clickActionsMenu() {
        $(Constants.actionsMenu).shouldBe(Condition.visible).click();
        return this;
    }

    public ConfirmationModal clickDeletePortfolio() {
        $(deletePortfolio).shouldBe(Condition.visible).click();
        return new ConfirmationModal();
    }

    public boolean isPortfolioDeletedToolbarVisible() {
        return Helpers.isWebElementVisible(portfolioDeletedToolbarVisible);
    }

    public EditPortfolioPage clickEditPortfolio() {
        Helpers.waitForClickable(editPortfolio).click();
        return new EditPortfolioPage();
    }

    public boolean isPortfolioPrivacyShareOnlyWithLink() {
        return Helpers.isWebElementVisible(shareWithLinkIcon);
    }

    public ReportImproperModal clickReportImproperContent() {
        $(improperContent).shouldBe(Condition.visible).click();
        return new ReportImproperModal();
    }

    public String getNotificationIsSentText() {
        System.out.println("PortfolioPage.getNotificationIsSentText");

        return $(NotificationToast.getElementLocator()).getText();
    }

    public boolean isDescriptionQuoted() {
        return $(quoteTag).isDisplayed();
    }

    public PortfolioPage addNewTag() {
        Helpers.waitForVisibility(insertTag).sendKeys(newTag);
        Helpers.waitForVisibility(insertTag).sendKeys(Keys.ENTER);
        return this;
    }

    public boolean isTagAddedToPortfolio() {
        return Helpers.isWebElementVisible(addedTag);
    }

    public PortfolioPage addToRecommendationsList() {
        Helpers.waitForClickable(addToRecommendations).click();
        return this;
    }

    public boolean isRemoveFromRecommendationsDisplayed() {
        return Helpers.isWebElementVisible(removeFromRecommendations);
    }

    public PortfolioPage removeFromRecommendationsList() {
        Helpers.waitForClickable(removeFromRecommendations).click();
        return this;
    }

    public boolean isAddToRecommendationsDisplayed() {
        return Helpers.isWebElementVisible(addToRecommendations);
    }

    public String getTagText() {
        return Helpers.waitForVisibility(tag).getText();
    }

    public String getMatchingTagText(String tagText) {
        return $$(tag).find(Condition.text(tagText)).getText();
    }


    public String getEducationalTaxonText() {
        return Helpers.waitForVisibility(educationalTaxon).getText();
    }

    public boolean isMaterialBoxDisplayed() {
        $(materialBox).scrollIntoView(false);
        return Helpers.isWebElementVisible(materialBox);
    }

    public PortfolioPage markNewPortfolioAsReviewed() {
        Helpers.waitForClickable(markAsReviewed).click();
        return this;
    }

    public PortfolioPage showPortfolioComments() {
        return Comments.showPortfolioComments();
    }

    public PortfolioPage addNewComment() {
        return Comments.addNewComment();
    }

    public String getCommentText() {
        return Comments.getCommentText();
    }

    public ReportImproperModal reportImproperComment() {
        Helpers.waitForClickable(reportCommentButton).click();
        return new ReportImproperModal();
    }

    public ReportImproperModal reportImproperTag() {
        Helpers.waitForClickable(reportTagButton).click();
        return new ReportImproperModal();
    }

    public PortfolioPage addNewSystemTag() {
        Helpers.waitForVisibility(insertTag).sendKeys(Constants.systemTag);
        Helpers.waitForClickable(autocompleteSystemTag).click();
        Helpers.waitForVisibility(newSystemTagNotification).sendKeys(Keys.ENTER);
        return this;
    }

    public String getChangedLOBannerText() {
        return $(Constants.bannerText).getText();
    }

    public boolean isChapterNarrow() {
        return Helpers.isWebElementVisible(narrowChapter);
    }

    public boolean isSubchapterDisplayed() {
        return getWebDriver().findElements(subchapterTag).size() == 1;
    }

    public String getFirstChapterTitle() {
        return Helpers.waitForVisibility(firstChapterTitle).getText();
    }

    public boolean isMediaFileAddedToChapter() {
        return getWebDriver().findElements(materialBox).size() == 1;
    }

    public boolean isMaterialAligned() {
        return getWebDriver().findElements(alignedMaterialBox).size() == 1;
    }

    public MyMaterialsPage clickMyMaterials() {
        return new LeftMenu().clickMyMaterials();
    }

    public PortfolioPage changeVisibility() {
        Helpers.waitForClickable(visibilityButton).click();
        return this;
    }

    public PortfolioPage setVisibilityToPublic() {
        $(publicVisibility).shouldBe(Condition.visible).click();
        return this;
    }

    public String getPublicPortfolioText() {
        return Helpers.waitForVisibility(Constants.toastText).getText();
    }

    public PortfolioPage markContentIsReviewed() {
        Helpers.waitForClickable(markAsReviewed).click();
        return this;
    }

    public boolean isUnreviewedToolbarHidden() {
        return !$(unreviewedNewLearningObjectToolbar).exists();

    }
}

