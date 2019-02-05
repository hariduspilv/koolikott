package ee.netgroup.htm.page;

import com.codeborne.selenide.Condition;
import ee.netgroup.htm.components.ConfirmationModal;
import ee.netgroup.htm.helpers.Arrays;
import ee.netgroup.htm.helpers.Helpers;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static ee.netgroup.htm.util.JSWaiter.waitForJQueryAndAngularToFinishRendering;

public class FaqPage {

    private By editModeBtn = By.cssSelector("button[data-ng-click='$ctrl.editFaqPage()']");
    private By addFaqBtn = By.cssSelector("button[aria-label='Add FAQ']");
    private By editFaqBtn = By.id("faq-edit");
    private By faqBlockListInEditMode = By.xpath("//md-card[@class='portfolio-chapter card-lg faq-font _md']");
    private By estonianLanguage = By.cssSelector("button[aria-label='ET button']");
    private By englishLanguage = By.cssSelector("button[aria-label='EN button']");
    private By russianLanguage = By.cssSelector("button[aria-label='RU button']");
    private By questionFieldEst = By.id("add-question-est");
    private By answerFieldEst = By.id("add-answer-est");
    private By questionFieldEng = By.id("add-question-eng");
    private By answerFieldEng = By.id("add-answer-eng");
    private By questionFieldRus = By.id("add-question-rus");
    private By answerFieldRus = By.id("add-answer-rus");
    private By cancelAndCloseFaqBlock = By.cssSelector("button[aria-label='Cancel faq edit']");
    private By deleteFaqBtn = By.cssSelector("button[aria-label='Delete faq']");
    private By updateFaqBtn = By.cssSelector("button[aria-label='Uuenda']");
    private By addCreatedFaqBtn = By.cssSelector("button[aria-label='Lisa']");
    private By firstFaqTitle = By.xpath("(//h2[@class='chapter-title'])[1]");

    public FaqPage() {
        waitForJQueryAndAngularToFinishRendering();
    }

    public static FaqPage getFaqPage() { return new FaqPage(); }

    public FaqPage enterEditMode() {
        $(editModeBtn).shouldBe(Condition.visible).click();
        return this;
    }

    public FaqPage clickOpenAddNewFaqSection() {
        Helpers.waitForVisibility(addFaqBtn);
        $(addFaqBtn).shouldBe(Condition.visible).click();
        return this;
    }

    public FaqPage addRandomEstonianFaq() {
        $(estonianLanguage).click();
        $(questionFieldEst).shouldBe(Condition.visible).sendKeys(Helpers.randomElement(Arrays.materialTitlesArray));
        $(answerFieldEst).shouldBe(Condition.visible).sendKeys(Helpers.randomElement(Arrays.descriptionArray));
        return this;
    }

    public FaqPage addRandomEnglishFaq() {
        $(englishLanguage).click();
        $(questionFieldEng).sendKeys(Helpers.randomElement(Arrays.portfolioTitlesArray));
        $(answerFieldEng).shouldBe(Condition.visible).sendKeys(Helpers.randomElement(Arrays.descriptionArray));
        return this;
    }

    public FaqPage addSpecificQuestionInEnglish(String question) {
        $(englishLanguage).shouldBe(Condition.visible).click();
        $(questionFieldEng).shouldBe(Condition.visible).sendKeys(question);
        $(answerFieldEng).shouldBe(Condition.visible).sendKeys(Helpers.randomElement(Arrays.descriptionArray));
        return this;
    }

    public FaqPage addSpecificQuestionInEstonian(String question) {
        $(estonianLanguage).shouldBe(Condition.visible).click();
        $(questionFieldEst).shouldBe(Condition.visible).sendKeys(question);
        $(answerFieldEst).shouldBe(Condition.visible).sendKeys(Helpers.randomElement(Arrays.descriptionArray));
        return this;
    }

    public FaqPage editEstonianFaq(String text) {
        $(estonianLanguage).shouldBe(Condition.visible).click();
        $(questionFieldEst).shouldBe(Condition.visible).clear();
        $(questionFieldEst).sendKeys(text);
        return this;
    }

    public FaqPage addRandomRussianFaq() {
        $(russianLanguage).click();
        $(questionFieldRus).shouldBe(Condition.visible).sendKeys(Helpers.randomElement(Arrays.portfolioTitlesArray));
        $(answerFieldRus).shouldBe(Condition.visible).sendKeys(Helpers.randomElement(Arrays.descriptionArray));
        return this;
    }

    public FaqPage clickUpdateFaq() {
        $(updateFaqBtn).shouldBe(Condition.visible).click();
        return this;
    }

    public FaqPage clickAddCreatedFaq() {
        $(addCreatedFaqBtn).shouldBe(Condition.visible).click();
        return this;
    }

    public ConfirmationModal clickCancelAndCloseFaqBlock() {
        $(cancelAndCloseFaqBlock).shouldBe(Condition.visible).click();
        return new ConfirmationModal();
    }

    public ConfirmationModal clickDeleteFaqBlock() {
        $(deleteFaqBtn).shouldBe(Condition.visible).click();
        return new ConfirmationModal();
    }

    public FaqPage hoverOverFirst() {
        $$(faqBlockListInEditMode).first().hover();
        return this;
    }

    public FaqPage editFirstExistingFaq() {
        $$(faqBlockListInEditMode).first().hover();
        Helpers.waitForVisibility(editFaqBtn).click();
        return this;
    }

    public String getFirstFaqQuestion() {
        return $(firstFaqTitle).shouldBe(Condition.visible).getText();
    }
}
