package ee.netgroup.hm.page;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import ee.netgroup.hm.components.MaterialModal;
import ee.netgroup.hm.components.MediaModal;
import ee.netgroup.hm.components.PortfolioPrivacyModal;
import ee.netgroup.hm.components.Search;
import ee.netgroup.hm.helpers.Arrays;
import ee.netgroup.hm.helpers.Helpers;

public class EditPortfolioPage extends Page{
	
	private By successAlertText = By.xpath("//div[@class='chapter-block medium-editor-element medium-editor-placeholder']");
	private By saveAndExit = By.xpath("//button[@data-ng-click='saveAndExitPortfolio()']");
	public static By visibilityButton = By.id("change-visibility");
	private By shareWithLink = By.xpath("//button/span[contains(text(),'inult lingiga')]");
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
	private By alignRight = By.xpath("//button[@class='medium-editor-action']");
	private By preferredMaterial = By.xpath("//div/label[@data-translate='ADD_PREFERRED_MATERIAL_TO_CHAPTER']");
	
	
	public String getSuccessAlertText() {
		return getDriver().findElement(successAlertText).getAttribute("data-placeholder");
	}

	public EditPortfolioPage clickSaveAndExit() {
		Helpers.waitForMilliseconds(1000);
		Helpers.waitForClickable(saveAndExit);
		getDriver().findElement(saveAndExit).click();
		Helpers.waitForMilliseconds(1000);
		return new EditPortfolioPage();
	}
	
	public PortfolioPage clickSaveAndExitConfirmationControl() {
		Helpers.waitForMilliseconds(1000);
		Helpers.waitForClickable(saveAndExit);
		getDriver().findElement(saveAndExit).click();
		if (Helpers.elementExists(privacyPopUp) == true){ 	
			return PortfolioPrivacyModal.makePortfolioPublic();
		}
		return new PortfolioPage();
	}
	
	public PortfolioPage makePortfolioPublic() {
		return PortfolioPrivacyModal.makePortfolioPublic();
	}
	
	public PortfolioPage clickExitAndStayPrivate() {
		return PortfolioPrivacyModal.clickExitAndStayPrivate();
	}

	public EditPortfolioPage clickVisibilityButton() {
		Helpers.waitForClickable(visibilityButton);
		getDriver().findElement(visibilityButton).click();
		Helpers.waitForMilliseconds(1000);
		return this;
	}

	public EditPortfolioPage selectShareWithLink() {
		Helpers.waitForClickable(shareWithLink);
		Helpers.waitForMilliseconds(1000);
		getDriver().findElement(shareWithLink).click();
		return this;
	}

	public EditPortfolioPage addNewChapter() {
		Helpers.scrollDownToView(newChapter);
		Helpers.waitForClickable(newChapter);
		getDriver().findElement(newChapter).click();
		return this;
	}

	public EditPortfolioPage addDescription() {
		getDriver().findElement(chapter).clear();
		getDriver().findElement(chapter).sendKeys(Helpers.randomElement(Arrays.descriptionArray));
		return this;
	}

	public EditPortfolioPage clickToSelectDescription() {
		Helpers.waitForVisibility(chapter);
		getDriver().findElement(chapter).sendKeys(Keys.CONTROL + "a");
		Helpers.waitForMilliseconds(1000);
		return this;
	}

	public EditPortfolioPage clickToSelectBold() {
		Helpers.waitForMilliseconds(1000);
		getDriver().findElement(boldButton).click();
		Helpers.waitForMilliseconds(1000);
		return this;
	}
	
	public EditPortfolioPage clickToSelectItalic() {
		getDriver().findElement(italicButton).click();
		Helpers.waitForMilliseconds(1000);
		return this;
	}

	public EditPortfolioPage clickToSelectQuoteText() {
		getDriver().findElement(quoteText).click();
		return this;
	}

	public EditPortfolioPage setChapterTitle() {
		Helpers.waitForVisibility(chapterTitle);
		getDriver().findElement(chapterTitle).clear();
		getDriver().findElement(chapterTitle).sendKeys(Helpers.randomElement(Arrays.portfolioTitlesArray));
		return this;
	}
	
	public MaterialModal clickAddNewMaterial() {
		Helpers.waitForVisibility(newMaterial);
		getDriver().findElement(newMaterial).click();
		return new MaterialModal();
	}

	public Search clickAddExistingMaterial() {
		Helpers.waitForVisibility(existingMaterial);
		getDriver().findElement(existingMaterial).click();
		return new Search();
	}

	public EditPortfolioPage clickChangeChapterWidth() {
		Helpers.waitForVisibility(chapterWidth);
		getDriver().findElement(chapterWidth).click();
		return this;
	}

	public EditPortfolioPage addSubchapterTitle() {
		getDriver().findElement(chapter).clear();
		getDriver().findElement(chapter).sendKeys(Helpers.randomElement(Arrays.portfolioTitlesArray));
		return this;
	}

	public EditPortfolioPage setSubchapterTitleH3() {
		getDriver().findElement(h3Button).click();
		Helpers.waitForMilliseconds(1000);
		return this;
	}

	public EditPortfolioPage setSpecificChapterTitle(String string) {
		Helpers.waitForVisibility(chapterTitle);
		getDriver().findElement(chapterTitle).clear();
		getDriver().findElement(chapterTitle).sendKeys(string);
		return this;
	}

	public EditPortfolioPage setSecondChapterTitle(String string) {
		Helpers.waitForVisibility(chapterTitle);
		getDriver().findElements(chapterTitle).get(1).clear();
		getDriver().findElements(chapterTitle).get(1).sendKeys(string);
		return this;
	}

	public EditPortfolioPage clickMoveChapterDown() {
		getDriver().findElement(moveChapterDown).click();		
		return this;
	}

	public MediaModal clickAddNewMedia() {
		Helpers.waitForVisibility(newMedia);
		getDriver().findElement(newMedia).click();
		return new MediaModal();
	}

	public EditPortfolioPage clickAlignRight() {
		Helpers.moveToElement(PortfolioPage.materialBox);
		getDriver().findElements(alignRight).get(1).click();
		Helpers.waitForMilliseconds(1000);
		return this;
	}

	public Search clickAddPreferredMaterial() {
		Helpers.waitForVisibility(preferredMaterial);
		getDriver().findElement(preferredMaterial).click();
		return new Search();
	}

	
}
