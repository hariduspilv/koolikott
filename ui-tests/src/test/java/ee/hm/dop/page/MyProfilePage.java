package ee.hm.dop.page;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import ee.hm.dop.components.AddMaterialMainPart;
import ee.hm.dop.components.ChooseExistingPortfolioModal;
import ee.hm.dop.components.AddPortfolioBasic;
import ee.hm.dop.helpers.PageHelpers;


public class MyProfilePage extends Page {

	private By userName = By.xpath("//strong");
	private By addMaterialButton = By.id("add-material");
	private By addedMaterial = By.xpath("//h4[@data-ng-bind='getCorrectLanguageTitle(material)']");
	private By addedPortfolio = By.xpath("//h4[@data-ng-bind='portfolio.title']");
	private By fabButton = By.id("add-portfolio");
	private By selectMaterial = By.xpath("//div/md-icon[text()='radio_button_unchecked']");
	private By selectMaterial1 = By.xpath("//div/md-icon[text()='check_circle']");
	private By starIcon = By.xpath("//div/md-icon[text()='star_outline']");
	private By addMaterialToExistingPortfolio = By.xpath("//button[@data-ng-click='showAddMaterialsToPortfolioDialog()']");
	private By materialMessage = By.cssSelector("div.md-toast-content");


	public String getUserName() {
		return getDriver().findElement(userName).getText();
	}

	public MyProfilePage moveCursorToAddPortfolio() {
		PageHelpers.waitForSeconds(2500);
		PageHelpers.waitForVisibility(fabButton);
		Actions builder = new Actions(getDriver());
		WebElement addPortfolio = getDriver().findElement(fabButton);
		builder.moveToElement(addPortfolio).perform();
		return this;
	}
	
	public boolean successMessageIsDisplayed() {
		return getDriver().findElement(materialMessage).isDisplayed();

	}

	public MyProfilePage moveCursorToAddMaterial() {
		Actions builder = new Actions(getDriver());
		WebElement addMaterial = getDriver().findElement(addMaterialButton);
		builder.moveToElement(addMaterial).perform();
		return this;
	}
	

	
	public MyProfilePage moveCursorToAddMaterialToExistingPortfolio() {
		Actions builder = new Actions(getDriver());
		WebElement addMaterialToPortfolio = getDriver().findElement(fabButton);
		builder.moveToElement(addMaterialToPortfolio).perform();
		PageHelpers.waitForVisibility(addMaterialToExistingPortfolio);
		return this;
	}
	
	public MyProfilePage clickToSelectMaterial() {
		PageHelpers.waitForSeconds(2500);
		Actions builder = new Actions(getDriver());
		WebElement selectMaterialElement = getDriver().findElement(selectMaterial);
		builder.moveToElement(selectMaterialElement).perform();
		getDriver().findElement(selectMaterial1).click();
		return this;
	}
	
	public MyProfilePage clickToSelectStar() {
		PageHelpers.waitForSeconds(3500);
		Actions builder = new Actions(getDriver());
		WebElement selectMaterialElement = getDriver().findElement(selectMaterial);
		builder.moveToElement(selectMaterialElement).perform();
		getDriver().findElement(starIcon).click();
		return this;
	}
	
	public ChooseExistingPortfolioModal clickToAddMaterialToExistingPortfolio() {
		getDriver().findElement(addMaterialToExistingPortfolio).click();
		return new ChooseExistingPortfolioModal();
	}
	

	public AddMaterialMainPart clickAddMaterial() {
		getDriver().findElement(addMaterialButton).click();
		return new AddMaterialMainPart();
	}

	public AddPortfolioBasic clickAddPortfolio() {
		PageHelpers.waitForVisibility(fabButton);
		getDriver().findElement(fabButton).click();
		return new AddPortfolioBasic();
	}

	public MaterialPage openMaterialPage() {
		PageHelpers.waitForVisibility(addedMaterial);
		getDriver().findElement(addedMaterial).click();
		return new MaterialPage();
	}
	

	public PortfolioPage openPortfolio() {
		getDriver().findElement(addedPortfolio).click();
		return new PortfolioPage();
	}

}
