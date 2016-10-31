package ee.hm.dop.page;

import org.openqa.selenium.By;
import ee.hm.dop.helpers.PageHelpers;


public class MyPortfoliosPage extends Page {

	private By addedPortfolio = By.xpath("//h4[@data-ng-bind='portfolio.title']");
	private By deletedPortfolioToast = By.cssSelector("span.md-toast-text");
	private By addPortfolioMessage = By.cssSelector("h3");

	public PortfolioViewPage openPortfolio() {
		PageHelpers.waitForVisibility(addedPortfolio);
		getDriver().findElement(addedPortfolio).click();
		return new PortfolioViewPage();
	}
	
	public boolean getAddPortfolioMessageText() {
		PageHelpers.waitForVisibility(addPortfolioMessage);
		return getDriver().findElement(addPortfolioMessage).getText().contains("Kogumiku lisamiseks");

	}
	
	public String isPortfolioDeletedToastVisible() {
		return getDriver().findElement(deletedPortfolioToast).getText();
	}

}
