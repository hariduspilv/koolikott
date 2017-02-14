package ee.hm.dop.page;

import org.openqa.selenium.By;
import ee.hm.dop.helpers.PageHelpers;


public class MyPortfoliosPage extends Page {

	private By addedPortfolio = By.xpath("//h3[@data-ng-bind='$ctrl.learningObject.title']");
	private By addPortfolioMessage = By.cssSelector("h3");
	private By addPortfolioButton = By.id("add-portfolio");

	public PortfolioViewPage openPortfolio() {
		PageHelpers.waitForVisibility(addedPortfolio);
		getDriver().findElement(addedPortfolio).click();
		return new PortfolioViewPage();
	}
	
	public boolean getAddPortfolioMessageText() {
		PageHelpers.waitForVisibility(addPortfolioButton);
		PageHelpers.waitForSeconds(1500);
		return getDriver().findElement(addPortfolioMessage).getText().contains("Kogumiku lisamiseks");

	}
	

}
