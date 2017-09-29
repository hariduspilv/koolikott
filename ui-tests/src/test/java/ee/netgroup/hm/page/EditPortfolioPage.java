package ee.netgroup.hm.page;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import ee.netgroup.hm.components.AddMaterialPopUp;
import ee.netgroup.hm.components.PortfolioPrivacyPopUp;
import ee.netgroup.hm.components.Search;
import ee.netgroup.hm.helpers.Arrays;
import ee.netgroup.hm.helpers.Helpers;

public class EditPortfolioPage extends Page{
	
	private By successAlertText = By.cssSelector("p.md-toolbar-text");
	private By saveAndExit = By.xpath("//button[@data-ng-click='saveAndExitPortfolio()']");
	private By visibilityButton = By.xpath("//button[@ng-click='$mdOpenMenu($event)']");
	private By shareWithLink = By.xpath("//button/span[contains(text(),'inult lingiga')]");
	private By newChapter = By.xpath("//button[@data-ng-click='addNewChapter()']");
	private By descriptionField = By.xpath("(//div[starts-with(@id, 'taTextElement')])"); //TODO: et võtaks kõige viimase
	private By boldButton = By.name("bold");
	private By italicButton = By.name("italics");
	private By bulletedList = By.name("ul");
	private By numberedList = By.name("ol");
	private By preFormattedText = By.name("pre");
	private By privacyPopUp = By.xpath("//md-dialog-content[@class='md-dialog-content']");
	private By chapterTitle = By.xpath("//input[@data-ng-model='$ctrl.chapter.title']");
	private By addMaterialButton = By.xpath("//button[@data-ng-click='$ctrl.openMenu($mdOpenMenu, $event)']");
	private By newMaterial = By.xpath("//button/span[text()='Uus']");
	private By existingMaterial = By.xpath("//button/span[text()='Koolikotist']");
	

	public String getSuccessAlertText() {
		return getDriver().findElement(successAlertText).getText();
	}

	public EditPortfolioPage clickSaveAndExit() {
		Helpers.waitForSeconds(1000);
		Helpers.waitForClickable(saveAndExit);
		getDriver().findElement(saveAndExit).click();
		Helpers.waitForSeconds(1000);
		return new EditPortfolioPage();
	}
	
	public PortfolioPage clickSaveAndExitConfirmationControl() {
		Helpers.waitForSeconds(1000);
		Helpers.waitForClickable(saveAndExit);
		getDriver().findElement(saveAndExit).click();
		if (Helpers.elementExists(privacyPopUp) == true){ 	
			return PortfolioPrivacyPopUp.makePortfolioPublic();
		}
		return new PortfolioPage();
	}
	
	public PortfolioPage makePortfolioPublic() {
		return PortfolioPrivacyPopUp.makePortfolioPublic();
	}
	
	public PortfolioPage clickExitAndStayPrivate() {
		return PortfolioPrivacyPopUp.clickExitAndStayPrivate();
	}

	public EditPortfolioPage clickVisibilityButton() {
		Helpers.waitForClickable(visibilityButton);
		getDriver().findElement(visibilityButton).click();
		Helpers.waitForSeconds(1000);
		return this;
	}

	public EditPortfolioPage selectShareWithLink() {
		Helpers.waitForClickable(shareWithLink);
		Helpers.waitForSeconds(1000);
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
		getDriver().findElement(descriptionField).clear();
		getDriver().findElement(descriptionField).sendKeys(Helpers.randomElement(Arrays.descriptionArray));
		return this;
	}

	public EditPortfolioPage clickToSelectDescription() {
		Helpers.waitForVisibility(descriptionField);
		getDriver().findElement(descriptionField).sendKeys(Keys.CONTROL + "a");
		Helpers.waitForSeconds(1000);
		return this;
	}

	public EditPortfolioPage clickToSelectBold() {
		Helpers.waitForVisibility(boldButton);
		Helpers.waitForSeconds(1000);
		getDriver().findElement(boldButton).click();
		Helpers.waitForSeconds(1000);
		return this;
	}
	
	public EditPortfolioPage clickToSelectItalic() {
		getDriver().findElement(italicButton).click();
		Helpers.waitForSeconds(1000);
		return this;
	}
	
	public EditPortfolioPage clickToSelectBulletedList() {
		getDriver().findElement(bulletedList).click();
		Helpers.waitForSeconds(1000);
		return this;
	}
		
	public EditPortfolioPage clickToSelectNumberedList() {
		getDriver().findElement(numberedList).click();
		return this;
	}

	public EditPortfolioPage clickToSelectPreFormattedText() {
		getDriver().findElement(preFormattedText).click();
		return this;
	}

	public EditPortfolioPage setChapterTitle() {
		Helpers.waitForVisibility(chapterTitle);
		getDriver().findElement(chapterTitle).clear();
		getDriver().findElement(chapterTitle).sendKeys(Helpers.randomElement(Arrays.portfolioTitlesArray));
		return this;
	}

	public EditPortfolioPage clickAddMaterial() {
		Helpers.waitForVisibility(addMaterialButton);
		getDriver().findElement(addMaterialButton).click();
		return this;
	}
	
	public AddMaterialPopUp clickAddNewMaterial() {
		Helpers.waitForVisibility(newMaterial);
		getDriver().findElement(newMaterial).click();
		return new AddMaterialPopUp();
	}

	public Search clickAddExistingMaterial() {
		Helpers.waitForVisibility(existingMaterial);
		getDriver().findElement(existingMaterial).click();
		return new Search();
	}


	
}
