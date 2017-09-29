package ee.netgroup.hm.page;

import org.openqa.selenium.By;

public class DeletedPortfoliosPage extends Page {
	
	private By deletedPortfolio = By.cssSelector(".md-cell:nth-child(4)");

	public PortfolioPage clickOpenPortfolio() {
		getDriver().findElement(deletedPortfolio).click();
		return new PortfolioPage();
	}

}
