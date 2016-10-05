package ee.hm.dop.page;

import org.openqa.selenium.By;

import ee.hm.dop.helpers.PageHelpers;

public class DashboardPage extends Page {
	
	private By openPortfolioButton = By.xpath("//a[starts-with(@href, '/portfolio?id=9')]"); 
	private By orderPortfolio = By.xpath("//md-card[@id='improper-portfolios-table']/md-table-container/table/thead/tr/th[3]/span");
	
	
	public DashboardPage clickToOrderImproperPortfolios() {
		PageHelpers.waitForSeconds(3500);
		getDriver().findElement(orderPortfolio).click();
		return this;
	}
	
	
	public PortfolioPage clickOpenPortfolioFromDashboard() {
		PageHelpers.waitForSeconds(1500);
		getDriver().findElement(openPortfolioButton).click();
		return new PortfolioPage();
	}

}
