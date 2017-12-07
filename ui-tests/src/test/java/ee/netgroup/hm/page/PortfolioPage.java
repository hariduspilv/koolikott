package ee.netgroup.hm.page;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import ee.netgroup.hm.components.AddPortfolioForm;
import ee.netgroup.hm.components.Comments;
import ee.netgroup.hm.components.ConfirmationPopup;
import ee.netgroup.hm.components.ReportImproperPopUp;
import ee.netgroup.hm.components.fabButton;
import ee.netgroup.hm.helpers.Constants;
import ee.netgroup.hm.helpers.Helpers;
import ee.netgroup.hm.tests.PortfolioTests;

public class PortfolioPage extends Page{
	
	private By portfolioTitle = By.xpath("//h1[@data-ng-bind='portfolio.title']");
	private By deletePortfolio = By.xpath("//button[@data-ng-click='confirmPortfolioDeletion()']");
	private By editPortfolio = By.xpath("//button[@data-ng-click='editPortfolio()']");
	private By shareWithLinkIcon = By.xpath("//md-icon[text()='link']");
	private By improperContent = By.xpath("//span[@data-translate='REPORT_IMPROPER']");
	private By quoteTag = By.tagName("blockquote");
	private By insertTag = By.xpath("(//input[starts-with(@id, 'fl-input-')])");
	private String newTag = Helpers.generateNewTag();
	private By addedTag = By.xpath("//a[contains(@href, 'search/result?q=tag:\""+newTag+"\"')]"); 
	private By addToRecommendations = By.xpath("//button[@data-ng-click='recommend()']");
	private By removeFromRecommendations = By.xpath("//button[@data-ng-click='removeRecommendation()']");
	private By tag = By.xpath("//a[@data-ng-click='$ctrl.getTagSearchURL($event, $chip.tag)']");
	private By educationalTaxon = By.xpath("//span[@data-translate='PRESCHOOLEDUCATION']");
	private By materialBox = By.cssSelector("div.chapter-embed-card__caption");
	private By reportCommentButton = By.xpath("//button[@ng-click='$ctrl.reportComment($event)']");
	private By reportTagButton = By.xpath("//button[@ng-click='$ctrl.reportTag($event)']");
	private By autocompleteSystemTag = By.xpath("//span[@md-highlight-text='$ctrl.newTag.tagName']");
	private By newSystemTagNotification = By.xpath("//button[contains(text(), 'Ok')]");
	private By markAsReviewed = By.xpath("//button[@aria-label='Märgi ülevaadatuks']");
	private By narrowChapter = By.xpath("//div[@class='chapter-block is-narrow is-narrow-left']");
	private By subchapterTag = By.xpath("//h3[@class='subchapter']");
	private By firstChapterTitle = By.xpath("//h2[@class='chapter-title']");
	
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
		Helpers.waitForMilliseconds(1000);
		Helpers.waitForClickable(editPortfolio);
		getDriver().findElement(editPortfolio).click();
		return new EditPortfolioPage();
	}

	public boolean isPortfolioPrivacyShareOnlyWithLink() {
        return getDriver().findElement(shareWithLinkIcon).isDisplayed();
	}

	public ReportImproperPopUp clickReportImproperContent() {
		Helpers.waitForClickable(improperContent);
		getDriver().findElement(improperContent).click();
		return new ReportImproperPopUp();
	}
	
	public String getNotificationIsSentText() {
		return getDriver().findElement(Constants.toastText).getText();
	}

	public boolean getQuoteTextTag() {
		return getDriver().findElement(quoteTag).isDisplayed();
	}

	public PortfolioPage addNewTag() {
		Helpers.waitForVisibility(insertTag);
		getDriver().findElement(insertTag).sendKeys(newTag);
		Helpers.waitForMilliseconds(1000);
		getDriver().findElement(insertTag).sendKeys(Keys.ENTER);
		Helpers.waitForMilliseconds(2000);
		return this;
	}

	public boolean isTagAddedToPortfolio() {
		return getDriver().findElement(addedTag).isDisplayed();
	}

	public PortfolioPage addToRecommendationsList() {
		Helpers.waitForClickable(addToRecommendations);
		getDriver().findElement(addToRecommendations).click();
		Helpers.waitForMilliseconds(1000);
		return this;
	}

	public boolean isRemoveFromRecommendationsDisplayed() {
		Helpers.waitForMilliseconds(1000);
		return getDriver().findElement(removeFromRecommendations).isDisplayed();
	}

	public PortfolioPage removeFromRecommendationsList() {
		Helpers.waitForClickable(removeFromRecommendations);
		Helpers.waitForMilliseconds(1000);
		getDriver().findElement(removeFromRecommendations).click();
		Helpers.waitForMilliseconds(1000);
		return this;
	}

	public boolean isAddToRecommendationsDisplayed() {
		Helpers.waitForMilliseconds(1000);
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

	public PortfolioPage markNewPortfolioAsReviewed() {
		Helpers.waitForClickable(markAsReviewed);
		getDriver().findElement(markAsReviewed).click();
		return this;
	}

	public PortfolioPage showPortfolioComments() {
		return Comments.showPortfolioComments();
	}
	
	public PortfolioPage addNewComment() {
		return Comments.addNewComment();
	}

	public String getCommentText() {
		return Comments.getCommentText();
	}

	public ReportImproperPopUp reportImproperComment() {
		getDriver().findElement(reportCommentButton).click();
		return new ReportImproperPopUp();
	}

	public ReportImproperPopUp reportImproperTag() {
		getDriver().findElement(reportTagButton).click();
		return new ReportImproperPopUp();
	}

	public PortfolioPage addNewSystemTag() {
		Helpers.waitForVisibility(insertTag);
		getDriver().findElement(insertTag).sendKeys(Constants.systemTag);
		Helpers.waitForMilliseconds(1000);
		getDriver().findElement(autocompleteSystemTag).click();
		Helpers.waitForMilliseconds(2000);
		getDriver().findElement(newSystemTagNotification).sendKeys(Keys.ENTER);
		Helpers.waitForMilliseconds(5000);
		return this;
	}

	public String getChangedLOBannerText() {
		return getDriver().findElement(Constants.bannerText).getText();
	}

	public boolean isChapterNarrow() {
		return getDriver().findElements(narrowChapter).size() == 1;
	}

	public boolean isSubchapterDisplayed() {
		return getDriver().findElements(subchapterTag).size() == 1;
	}

	public String isChapterOrderChanged() {
		return getDriver().findElement(firstChapterTitle).getText();
	}



}

