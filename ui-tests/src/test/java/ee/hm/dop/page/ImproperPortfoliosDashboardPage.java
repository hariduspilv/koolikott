package ee.hm.dop.page;

import org.openqa.selenium.By;

import ee.hm.dop.helpers.PageHelpers;

public class ImproperPortfoliosDashboardPage extends Page {
	
	private By openPortfolioButton = By.xpath("//button[@aria-label='Ava']");
	private By orderPortfolio = By.cssSelector("th.md-column.md-sort > span");
	
	
	public ImproperPortfoliosDashboardPage clickToOrderImproperPortfolios() {
		PageHelpers.waitForSeconds(1500);
		PageHelpers.waitForVisibility(orderPortfolio);
		getDriver().findElement(orderPortfolio).click();
		return this;
	}
	
	
	public PortfolioViewPage clickOpenPortfolioFromDashboard() {
		PageHelpers.waitForSeconds(1500);
		getDriver().findElement(openPortfolioButton).click();
		return new PortfolioViewPage();
	}

}
