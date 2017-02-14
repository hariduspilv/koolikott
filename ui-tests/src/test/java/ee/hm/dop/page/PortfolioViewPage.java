package ee.hm.dop.page;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import ee.hm.dop.components.AddPortfolioBasic;
import ee.hm.dop.components.ConfirmationDialogPopup;
import ee.hm.dop.helpers.PageHelpers;

public class PortfolioViewPage extends Page {

	private By menuBar = By.xpath("//md-icon[text()='more_vert']");
	private By editPortfolio = By.xpath("//button[@data-ng-click='editPortfolio()']");
	private By improperContent = By.xpath("//button[@data-ng-click='showConfirmationDialog()']");
	private By removeButton = By.xpath("//button[@data-ng-click='confirmPortfolioDeletion()']");
	private By notImproperContentButton = By.xpath("//button[@data-ng-click='setNotImproperLearningObject()']");
	private By addedTag = By.xpath("//a[@data-ng-click='getTagSearchURL($event, $chip.tag)']");
	private By recommendationList = By.xpath("//button[@data-ng-click='recommend()']");
	private By removeFromRecommendations = By.xpath("//button[@data-ng-click='removeRecommendation()']");
	private By fabButton = By.id("add-portfolio");
	private By preTag = By.tagName("pre");
	private By shareWithLinkIcon = By.xpath("//md-icon[text()='link']");
	private By copyPortfolioButton = By.xpath("//button[@data-ng-click='copyPortfolio()']");
	private By portfolioTitle = By.xpath("//h1[@data-ng-bind='portfolio.title']");
	private By creatorName = By.xpath("//p[@data-ng-if='isNullOrZeroLength(material.authors)']");
	private By materialBox = By.cssSelector("div.pointer.layout-row");
	private By notImproperContent = By.cssSelector("span.reason > span");
	private By insertTag = By.xpath("(//input[starts-with(@id, 'fl-input-')])");
			


	public PortfolioViewPage clickActionsMenu() {
		PageHelpers.waitForVisibility(menuBar);
		//getDriver().navigate().refresh();
		getDriver().findElement(menuBar).click();
		PageHelpers.waitForSeconds(1500);
		return this;
	}
	


	public PortfolioViewPage moveCursorToCopyPortfolio() {
		Actions builder = new Actions(getDriver());
		PageHelpers.waitForVisibility(fabButton);
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
		PageHelpers.waitForVisibility(editPortfolio);
		getDriver().findElement(editPortfolio).sendKeys(Keys.ENTER);
		PageHelpers.waitForSeconds(1500);
		return new EditPortfolioPage();
	}

	public PortfolioViewPage addToRecommendationsList() {
		getDriver().findElement(recommendationList).sendKeys(Keys.ENTER);
		PageHelpers.waitForSeconds(2500);
		return this;
	}

	public ConfirmationDialogPopup clickNotifyImproperContent() {
		getDriver().findElement(improperContent).sendKeys(Keys.ENTER);
		PageHelpers.waitForSeconds(2500);
		return new ConfirmationDialogPopup();
	}
	
	public ConfirmationDialogPopup clickRemovePortfolio() {
		getDriver().findElement(removeButton).sendKeys(Keys.ENTER);
		PageHelpers.waitForSeconds(2500);
		return new ConfirmationDialogPopup();
	}

	public boolean removeFromRecommendationListIsDisplayed() {
		PageHelpers.waitForSeconds(1500);
		return getDriver().findElement(removeFromRecommendations).isDisplayed();

	}
	
	public PortfolioViewPage setContentIsNotImproper() {
		PageHelpers.waitForVisibility(notImproperContentButton);
		getDriver().findElement(notImproperContentButton).click();
		PageHelpers.waitForSeconds(1500);
		return this;
	}
	
	public PortfolioViewPage insertTagAndEnter1() {
		PageHelpers.waitForSeconds(1000);
		getDriver().findElement(insertTag).sendKeys(PageHelpers.getDate(0, "dd/MM/yyyy"));
		getDriver().findElement(insertTag ).sendKeys(Keys.ENTER);
		PageHelpers.waitForSeconds(1000);
		return this;

	}


	public boolean contentsIsNotImproper() {
        return getDriver().findElement(notImproperContent).isDisplayed();

	}
	
	public boolean isPortfolioPrivacyShareOnlyWithLink() {
		PageHelpers.waitForVisibility(shareWithLinkIcon);
        return getDriver().findElement(shareWithLinkIcon).isDisplayed();

	}

	public String getTagText() {
		return getDriver().findElement(addedTag).getText();

	}
	
	public boolean getPreTag() {
		PageHelpers.waitForVisibility(preTag);
		return getDriver().findElement(preTag).isDisplayed();

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
