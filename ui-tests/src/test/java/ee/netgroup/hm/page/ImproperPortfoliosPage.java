package ee.netgroup.hm.page;

import org.openqa.selenium.By;
import ee.netgroup.hm.helpers.Helpers;

public class ImproperPortfoliosPage extends Page{
	
	private By orderPortfolio = By.cssSelector("th.md-column.md-sort > span");
	private By openPortfolioButton = By.xpath("//button[@aria-label='Ava']");
	

	public ImproperPortfoliosPage clickToOrderImproperPortfolios() {
		Helpers.waitForClickable(orderPortfolio);
		getDriver().findElement(orderPortfolio).click();
		return this;
	}
	
	public PortfolioPage clickOpenImproperPortfolio() {
		getDriver().findElement(openPortfolioButton).click();
		return new PortfolioPage();
	}

}
