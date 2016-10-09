package ee.hm.dop.page;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import ee.hm.dop.components.AddPortfolioBasic;
import ee.hm.dop.components.ConfirmationDialogPopup;
import ee.hm.dop.helpers.PageHelpers;

public class PortfolioPage extends Page {

	private By menuBar = By.xpath("//md-icon[text()='more_vert']");
	private By editPortfolio = By.xpath("//button[@data-ng-click='editPortfolio()']");
	private By improperContent = By.xpath("//button[@data-ng-click='showConfirmationDialog()']");
	private By notImproperContent = By.xpath("//button[@data-ng-click='setNotImproper()']");
	private By addedTag = By.xpath("//a[@data-ng-click='getTagSearchURL($event, $chip.tag)']");
	private By recommendationList = By.xpath("//button[@data-ng-click='recommend()']");
	private By removeFromRecommendations = By.xpath("//button[@data-ng-click='removeRecommendation()']");
	private By fabButton = By.xpath("//md-fab-trigger");
	private By copyPortfolioButton = By.xpath("//button[@data-ng-click='copyPortfolio()']");
	private By portfolioTitle = By.xpath("//h1[@data-ng-bind='portfolio.title']");
	private By creatorName = By.xpath("//p[@data-ng-if='isNullOrZeroLength(material.authors)']");
	private By materialBox = By.xpath("//h4[@class='md-headline two-rows']");


	public PortfolioPage clickActionsMenu() {
		getDriver().findElement(menuBar).click();
		PageHelpers.waitForSeconds(1500);
		return this;
	}

	public PortfolioPage moveCursorToCopyPortfolio() {
		Actions builder = new Actions(getDriver());
		WebElement copyPortfolio = getDriver().findElement(fabButton);
		builder.moveToElement(copyPortfolio).perform();
		return this;
	}

	public AddPortfolioBasic clickCopyPortfolio() {
		PageHelpers.waitForVisibility(copyPortfolioButton);
		getDriver().findElement(copyPortfolioButton).click();
		return new AddPortfolioBasic();
	}

	public EditPortfolioPage clickEditPortfolio() {
		getDriver().findElement(editPortfolio).sendKeys(Keys.ENTER);
		PageHelpers.waitForSeconds(1500);
		getDriver().navigate().refresh();
		PageHelpers.waitForSeconds(1500);
		return new EditPortfolioPage();
	}

	public PortfolioPage addToRecommendationsList() {
		getDriver().findElement(recommendationList).sendKeys(Keys.ENTER);
		PageHelpers.waitForSeconds(3500);
		return this;
	}

	public ConfirmationDialogPopup clickNotifyImproperContent() {
		getDriver().findElement(improperContent).sendKeys(Keys.ENTER);
		PageHelpers.waitForSeconds(2500);
		return new ConfirmationDialogPopup();
	}

	public boolean removeFromRecommendationListIsDisplayed() {
		PageHelpers.waitForSeconds(1500);
		return getDriver().findElement(removeFromRecommendations).isDisplayed();

	}

	public boolean contentsIsNotImproperIsDisplayed() {
		PageHelpers.waitForSeconds(1500);
		return getDriver().findElement(notImproperContent).isDisplayed();

	}
	

	public String getTagText() {
		getDriver().navigate().refresh();
		return getDriver().findElement(addedTag).getText();

	}

	public boolean wordCopyIsAddedToPortfolioTitle() {
		return getDriver().findElement(portfolioTitle).getText().contains("(Copy)");

	}
	
	public String getCreatorName() {
		return getDriver().findElement(creatorName).getText();

	}

	public boolean isMaterialBoxIsDisplayed() {
		return getDriver().findElement(materialBox).isDisplayed();
	}

}
