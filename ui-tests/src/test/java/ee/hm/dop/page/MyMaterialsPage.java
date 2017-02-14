package ee.hm.dop.page;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import ee.hm.dop.helpers.PageHelpers;

public class MyMaterialsPage extends Page {
	
	private By addMaterialMessage = By.cssSelector("h3");
	private By materialBox = By.xpath("//div[@class='card-hover material description']");
	private By checkedCircle = By.xpath("//div/md-icon[text()='check_circle']");
	private By uncheckedCircle = By.xpath("//div/md-icon[text()='check']");
	private By searchResultMaterial = By.xpath("//h3[@data-ng-bind='$ctrl.getCorrectLanguageTitle($ctrl.learningObject)']");
	private By starIcon = By.xpath("//md-icon[@data-ng-click='$ctrl.favorite($event)']");
	private By selectMaterialBox = By.cssSelector("h3.two-rows");
	private By materialMessage = By.cssSelector("span.md-toast-text");
	private By addPortfolioButton = By.id("add-portfolio");
	private By selectPortfolioDropdown = By.xpath("(//md-select[@aria-label='Vali kogumik'])");
	private By selectFirstPortfolio = By.xpath("/html/body/div[4]/md-select-menu/md-content/md-option[1]");
	private By selectChapterDropdown = By.xpath("(//md-select[@aria-label='Vali peatükk'])");
	private By selectFirstChapter = By.xpath("/html/body/div[5]/md-select-menu/md-content/md-option[1]");
	private By doneButton = By.xpath("//md-icon[text()='done']");
	
	public boolean getAddMaterialMessageText() {
		PageHelpers.waitForVisibility(addPortfolioButton);
		PageHelpers.waitForSeconds(1500);
		return getDriver().findElement(addMaterialMessage).getText().contains("Materjali lisamiseks");

	}
	
	public MyMaterialsPage clickToSelectMaterial() {
		PageHelpers.waitForSeconds(2500);
		Actions builder = new Actions(getDriver());
		WebElement selectMaterialBox = getDriver().findElement(materialBox);
		builder.moveToElement(selectMaterialBox).perform();
		WebElement selectUncheckedMaterialCircle = getDriver().findElement(uncheckedCircle);
		builder.moveToElement(selectUncheckedMaterialCircle).perform();
		getDriver().findElement(checkedCircle).click();
		return this;
	}
	
	public MyMaterialsPage selectPortfolio() {
		PageHelpers.waitForVisibility(selectPortfolioDropdown);
		getDriver().findElement(selectPortfolioDropdown).click();
		getDriver().findElement(selectFirstPortfolio).click();
		return this;
	}
	
	public MyMaterialsPage clickDone() {
		getDriver().findElement(doneButton).click();
		return this;
	}
	
	public MyMaterialsPage selectChapter() {
		PageHelpers.waitForVisibility(selectChapterDropdown );
		getDriver().findElement(selectChapterDropdown).click();
		getDriver().findElement(selectFirstChapter).click();
		return this;
	}
	
	
	public MaterialPage openMaterial() {
		PageHelpers.waitForSeconds(2000);
		PageHelpers.waitForVisibility(searchResultMaterial);
		getDriver().findElement(searchResultMaterial).click();
		PageHelpers.waitForSeconds(1500);
		return new MaterialPage();

	}
	
	public MyMaterialsPage clickToSelectStar() {
		PageHelpers.waitForSeconds(1500);
		PageHelpers.waitForVisibility(selectMaterialBox);
		Actions builder = new Actions(getDriver());
		WebElement selectMaterialElement = getDriver().findElement(selectMaterialBox);
		builder.moveToElement(selectMaterialElement).perform();
		getDriver().findElement(starIcon).click();
		return this;
	}
	
	public String getSuccessMessage() {
		return getDriver().findElement(materialMessage).getText();

	}
	
	


}
