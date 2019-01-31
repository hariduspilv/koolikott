package ee.netgroup.htm.page;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ex.UIAssertionError;
import ee.netgroup.htm.components.*;
import ee.netgroup.htm.helpers.Arrays;
import ee.netgroup.htm.helpers.Constants;
import ee.netgroup.htm.helpers.Helpers;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static org.openqa.selenium.Keys.ENTER;

public class EditPortfolioPage extends LearningObjectPage {

    private static Logger logger = LoggerFactory.getLogger(EditPortfolioPage.class);
    private By visibilityButton = By.id("change-visibility");
    private By successAlertText = By.cssSelector("div[class='chapter-block medium-editor-element medium-editor-placeholder']");
    private By saveAndExit = By.cssSelector("button[data-ng-click='saveAndExitPortfolio()']");
    private By saveAndExitConfirmationControl = By.cssSelector("button[ng-click='dialog.hide()']");
    private By shareWithLink = By.xpath("//button/span[contains(text(),'lingiga')]");
    private By makePortfolioPrivateBtn = By.cssSelector("button[data-ng-if=\"getPortfolioVisibility() != 'PRIVATE'\"]");
    private By portfolioIsAlreadyPrivate = By.cssSelector("button[data-ng-if=\"getPortfolioVisibility() === 'PRIVATE'\"]");
    private By newChapter = By.xpath("//div[@data-ng-click='$ctrl.createChapter()']");
    private By boldButton = By.xpath("//button[@data-action='bold']");
    private By italicButton = By.xpath("//button[@data-action='italic']");
    private By quoteText = By.xpath("//button[@data-action='append-blockquote']");
    private By privacyPopUp = By.xpath("//md-dialog-content[@class='md-dialog-content']");
    private By chapterTitle = By.xpath("//input[@data-ng-model='chapter.title']");
    private By chapter = By.xpath("//div[@data-ng-class='$ctrl.getBlockClassNames($index)']");
    private By newMaterial = By.xpath("//div/label[text()='Uus materjal']");
    private By existingMaterial = By.xpath("//div/label[text()='Olemasolev materjal']");
    private By chapterWidth = By.xpath("//div/label[text()='Muuda laiust']");
    private By h3Button = By.xpath("//button[@data-action='append-h3']");
    private By moveChapterDown = By.xpath("//button[@data-ng-click='$ctrl.onMoveDown()']");
    private By newMedia = By.xpath("//div/label[@data-translate='ADD_MEDIA_TO_CHAPTER']");
    private By alignRight = By.cssSelector("button[class='medium-editor-action']");
    private By preferredMaterial = By.xpath("//div/label[@data-translate='ADD_PREFERRED_MATERIAL_TO_CHAPTER']");
    private By addTagField = By.cssSelector("#addTagForm input");
    private By createLinkButton = By.cssSelector("button[data-action='createLink']");
    private By insertLinkField = By.cssSelector("input[placeholder='http://']");
    private By confirmLink = By.cssSelector("a[class='medium-editor-toolbar-save']");

    public String getSuccessAlertText() {
        return $(successAlertText).shouldBe(Condition.visible).getAttribute("data-placeholder");
    }

    public static EditPortfolioPage getEditPortfolioPage() {
        return new EditPortfolioPage();
    }

    public EditPortfolioPage clickSaveAndExit() {
        Helpers.waitForClickable(saveAndExit).click();
        return new EditPortfolioPage();
    }

    public PortfolioPage clickSaveAndExitConfirmationControl() {
        Helpers.waitForClickable(saveAndExit).click();
        try {
            $(saveAndExitConfirmationControl).shouldBe(Condition.visible).click();
            logger.info("Confirmation control modal displayed - make portfolio public chosen");
        } catch (UIAssertionError ignored) {
            logger.warn("Confirmation control modal not displayed");
        }
        Helpers.waitUntilNotVisible(NotificationToast.getElementLocator());
        return new PortfolioPage();
    }

    public EditPortfolioPage markPortfolioAsPrivate() {
        try {
            $(makePortfolioPrivateBtn).shouldBe(Condition.visible).click();
            logger.info("Confirmation control modal displayed - make portfolio public chosen");
        } catch (UIAssertionError ignored) {
            $(portfolioIsAlreadyPrivate).shouldBe(Condition.visible).click();
            logger.warn("Confirmation control modal not displayed");
        }
        Helpers.waitUntilNotVisible(NotificationToast.getElementLocator());
        return this;
    }

    public PortfolioPage clickExitAndMakePortfolioPublic() {
        return PortfolioPrivacyModal.makePortfolioPublic();
    }

    public PortfolioPage clickExitAndStayPrivate() {
        return PortfolioPrivacyModal.clickExitAndStayPrivate();
    }

    public EditPortfolioPage clickVisibilityButton() {
        Helpers.waitForClickable(visibilityButton).click();
        return this;
    }

    public EditPortfolioPage selectShareWithLink() {
        Helpers.waitForClickable(shareWithLink).click();
        return this;
    }

    public EditPortfolioPage addNewChapter() {
        Helpers.scrollDownToView(newChapter);
        Helpers.waitForClickable(newChapter).click();
        return this;
    }

    public EditPortfolioPage addDescription() {
        $(chapter).clear();
        Helpers.waitForVisibility(chapter).sendKeys(Helpers.randomElement(Arrays.descriptionArray));
        return this;
    }

    public EditPortfolioPage setMaterialLinkAsDescription() {
        Helpers.waitForVisibility(chapter).clear();
        Helpers.waitForVisibility(chapter).sendKeys(Constants.materialWithCommentsUrl);
        clickToSelectDescription();
        Helpers.waitForClickable(createLinkButton).click();
        Helpers.waitForVisibility(insertLinkField).sendKeys(Constants.materialWithCommentsUrl);
        Helpers.waitForClickable(confirmLink).click();
        return this;
    }

    public EditPortfolioPage clickToSelectDescription() {
        Helpers.waitForVisibility(chapter).sendKeys(Keys.CONTROL + "a");
        return this;
    }

    public EditPortfolioPage clickToSelectBold() {
        Helpers.waitForClickable(boldButton).click();
        return this;
    }

    public EditPortfolioPage clickToSelectItalic() {
        Helpers.waitForClickable(italicButton).click();
        return this;
    }

    public EditPortfolioPage clickToSelectQuoteText() {
        Helpers.waitForClickable(quoteText).click();
        return this;
    }

    public EditPortfolioPage setChapterTitle() {
        Helpers.waitForVisibility(chapterTitle).clear();
        Helpers.waitForVisibility(chapterTitle).sendKeys(Helpers.randomElement(Arrays.portfolioTitlesArray));
        return this;
    }

    public CreateMaterialModal clickAddNewMaterial() {
        Helpers.waitForClickable(newMaterial).click();
        return new CreateMaterialModal();
    }

    public Search clickAddExistingMaterial() {
        Helpers.waitForClickable(existingMaterial).click();
        return new Search();
    }

    public EditPortfolioPage clickChangeChapterWidth() {
        Helpers.waitForClickable(chapterWidth).click();
        return this;
    }

    public EditPortfolioPage addSubchapterTitle() {
        Helpers.waitForVisibility(chapter).clear();
        Helpers.waitForVisibility(chapter).sendKeys(Helpers.randomElement(Arrays.portfolioTitlesArray));
        return this;
    }

    public EditPortfolioPage setSubchapterTitleH3() {
        Helpers.waitForClickable(h3Button).click();
        return this;
    }

    public EditPortfolioPage setSpecificChapterTitle(String string) {
        Helpers.waitForVisibility(chapterTitle).clear();
        Helpers.waitForVisibility(chapterTitle).sendKeys(string);
        return this;
    }

    public EditPortfolioPage setSecondChapterTitle(String string) {
        Helpers.waitForVisibility(chapterTitle);
        $$(chapterTitle).get(1).clear();
        $$(chapterTitle).get(1).sendKeys(string);
        return this;
    }

    public EditPortfolioPage clickMoveChapterDown() {
        $(moveChapterDown).scrollIntoView(false);
        Helpers.waitForClickable(moveChapterDown).click();
        return this;
    }

    public EditPortfolioPage clickCreateLinkAndInsertConstantsLink() {

        return this;
    }

    public MediaModal clickAddNewMedia() {
        Helpers.waitForClickable(newMedia).click();
        return new MediaModal();
    }

    public EditPortfolioPage clickAlignRight() {
        Helpers.moveToElement(PortfolioPage.materialBox);
        $$(alignRight).last().click();
        return this;
    }

    public Search clickAddPreferredMaterial() {
        Helpers.waitForClickable(preferredMaterial).click();
        return new Search();
    }

    public EditPortfolioPage addTag(String tag) {
        WebElement addTagElement = Helpers.waitForClickable(addTagField);
        addTagElement.sendKeys(tag);
        addTagElement.sendKeys(ENTER);
        return this;
    }

    public void closeEditModeIfOpen() {
        try {
            Helpers.waitForClickable(saveAndExit).click();
                $(saveAndExitConfirmationControl).shouldBe(Condition.visible).click();
                logger.info("Confirmation control modal displayed - make portfolio public chosen");

        } catch (UIAssertionError ignored) {
            logger.info("Edit Portfolio Mode was not open");
        }
        Helpers.waitUntilNotVisible(NotificationToast.getElementLocator());
    }

}
