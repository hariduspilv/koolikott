package ee.hm.dop.page;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import ee.hm.dop.components.AddMaterialMainPart;
import ee.hm.dop.components.AddPortfolioBasic;
import ee.hm.dop.helpers.PageHelpers;


public class MyProfilePage extends Page {

	private By userName = By.xpath("//strong");
	private By addMaterialButton = By.id("add-material");
	private By addedMaterial = By.xpath("//h4[@data-ng-bind='getCorrectLanguageTitle(material)']");
	private By addedPortfolio = By.xpath("//h4[@data-ng-bind='portfolio.title']");
	private By fabButton = By.id("add-portfolio");
	private By selectPortfolio = By.xpath("//div[@data-learning-object='portfolio']");
	private By starIcon = By.xpath("//div[@aria-label='Lisa lemmikuks']");
	private By materialMessage = By.cssSelector("div.md-toast-content");
	private By deletedPortfolioToast = By.cssSelector("span.md-toast-text");


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
	

	
	public MyProfilePage clickToSelectStar() {
		PageHelpers.waitForSeconds(3500);
		Actions builder = new Actions(getDriver());
		WebElement selectMaterialElement = getDriver().findElement(selectPortfolio);
		builder.moveToElement(selectMaterialElement).perform();
		getDriver().findElement(starIcon).click();
		return this;
	}
	

	public AddMaterialMainPart clickAddMaterial() {
		getDriver().findElement(addMaterialButton).click();
		return new AddMaterialMainPart();
	}

	public AddPortfolioBasic clickAddPortfolio() {
		PageHelpers.waitForSeconds(1500);
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
		PageHelpers.waitForVisibility(addedPortfolio);
		getDriver().findElement(addedPortfolio).click();
		return new PortfolioPage();
	}
	
	public String isPortfolioDeletedToastVisible() {
		return getDriver().findElement(deletedPortfolioToast).getText();
	}

}
