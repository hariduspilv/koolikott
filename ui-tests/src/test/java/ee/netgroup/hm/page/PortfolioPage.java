package ee.netgroup.hm.page;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import ee.netgroup.hm.components.AddPortfolioForm;
import ee.netgroup.hm.components.ConfirmationPopup;
import ee.netgroup.hm.components.fabButton;
import ee.netgroup.hm.helpers.Constants;
import ee.netgroup.hm.helpers.Helpers;
import ee.netgroup.hm.tests.PortfolioTests;

public class PortfolioPage extends Page{
	
	private By portfolioTitle = By.xpath("//h1[@data-ng-bind='portfolio.title']");
	private By deletePortfolio = By.xpath("//button[@data-ng-click='confirmPortfolioDeletion()']");
	private By editPortfolio = By.xpath("//button[@data-ng-click='editPortfolio()']");
	private By shareWithLinkIcon = By.xpath("//md-icon[text()='link']");
	private By improperContent = By.xpath("//button[@data-ng-click='showConfirmationDialog()']");
	private By preTag = By.tagName("pre");
	private By insertTag = By.xpath("(//input[starts-with(@id, 'fl-input-')])");
	private String newTag = Helpers.generateNewTag();
	private By addedTag = By.xpath("//a[contains(@href, \"search/result?q=tag:'"+newTag+"'\")]");
	private By addToRecommendations = By.xpath("//button[@data-ng-click='recommend()']");
	private By removeFromRecommendations = By.xpath("//button[@data-ng-click='removeRecommendation()']");
	private By tag = By.xpath("//a[@data-ng-click='$ctrl.getTagSearchURL($event, $chip.tag)']");
	private By educationalTaxon = By.xpath("//span[@data-translate='PRESCHOOLEDUCATION']");
	private By materialBox = By.cssSelector("div.pointer.layout-row");
	private By doneButton = By.xpath("//button[2][@data-ng-click='button.onClick()']");
	private By restoreButton = By.xpath("//button[@data-ng-click='button.onClick()']");
	private By errorBanner = By.xpath("//div[@class='error-message-body flex']");
	
	
	public AddPortfolioForm clickCopyPortfolio() {
		return fabButton.clickCopyPortfolio();
	}

	public boolean wordWasAddedToPortfolioTitle() {
		return getDriver().findElement(portfolioTitle).getText().contains(PortfolioTests.addWordToTitle);
	}

	public PortfolioPage clickActionsMenu() {
		Helpers.waitForVisibility(Constants.actionsMenu);
		getDriver().findElement(Constants.actionsMenu).click();
		return this;
	}

	public ConfirmationPopup clickDeletePortfolio() {
		Helpers.waitForClickable(deletePortfolio);
		getDriver().findElement(deletePortfolio).click();
		return new ConfirmationPopup();
	}

	public EditPortfolioPage clickEditPortfolio() {
		Helpers.waitForSeconds(1000);
		Helpers.waitForClickable(editPortfolio);
		getDriver().findElement(editPortfolio).click();
		return new EditPortfolioPage();
	}

	public boolean isPortfolioPrivacyShareOnlyWithLink() {
        return getDriver().findElement(shareWithLinkIcon).isDisplayed();
	}

	public ConfirmationPopup clickNotifyImproperContent() {
		Helpers.waitForClickable(improperContent);
		getDriver().findElement(improperContent).click();
		return new ConfirmationPopup();
	}
	
	public String isPortfolioReportedAsImproper() {
		return getDriver().findElement(Constants.toastText).getText();
	}

	public PortfolioPage markContentAsNotImproper() {
		//Helpers.waitForClickable(doneButton);
		Helpers.moveToElement(doneButton);
		getDriver().findElement(doneButton).sendKeys(Keys.ENTER);;
		//getDriver().findElement(doneButton).click();
		Helpers.waitForSeconds(5000);
		return this;
	}

	public boolean isContentProper() {
		Helpers.waitForSeconds(1000);
		return getDriver().findElements(errorBanner).size() < 1;
	}

	public boolean getPreFormattedTextTag() {
		return getDriver().findElement(preTag).isDisplayed();
	}

	public PortfolioPage addNewTag() {
		Helpers.waitForVisibility(insertTag);
		getDriver().findElement(insertTag).sendKeys(newTag);
		Helpers.waitForSeconds(1000);
		getDriver().findElement(insertTag ).sendKeys(Keys.ENTER);
		Helpers.waitForSeconds(2000);
		return this;
	}

	public boolean isTagAddedToPortfolio() {
		return getDriver().findElement(addedTag).isDisplayed();
	}

	public PortfolioPage addToRecommendationsList() {
		Helpers.waitForClickable(addToRecommendations);
		getDriver().findElement(addToRecommendations).click();
		Helpers.waitForSeconds(1000);
		return this;
	}

	public boolean isRemoveFromRecommendationsDisplayed() {
		Helpers.waitForSeconds(1000);
		return getDriver().findElement(removeFromRecommendations).isDisplayed();
	}

	public PortfolioPage removeFromRecommendationsList() {
		Helpers.waitForClickable(removeFromRecommendations);
		getDriver().findElement(removeFromRecommendations).click();
		Helpers.waitForSeconds(1000);
		return this;
	}

	public boolean isAddToRecommendationsDisplayed() {
		Helpers.waitForSeconds(1000);
		return getDriver().findElement(addToRecommendations).isDisplayed();
	}

	public String getTagText() {
		return getDriver().findElement(tag).getText();
	}

	public String getEducationalTaxonText() {
		return getDriver().findElement(educationalTaxon).getText();
	}

	public boolean isMaterialBoxDisplayed() {
		return getDriver().findElement(materialBox).isDisplayed();
	}

	public PortfolioPage markContentIsReviewed() {
		Helpers.waitForClickable(removeFromRecommendations);
		getDriver().findElement(removeFromRecommendations).click();
		return this;
	}

	public PortfolioPage restoreDeletedPortfolio() {
		Helpers.waitForClickable(restoreButton);
		getDriver().findElement(restoreButton).click();
		Helpers.waitForSeconds(3000);
		return this;
	}

	public boolean isPortfolioRestored() {
		Helpers.waitForSeconds(1000);
		return getDriver().findElements(errorBanner).size() < 1;
	}


}
