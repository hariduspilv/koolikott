package ee.hm.dop.components;

import org.openqa.selenium.By;


public class ChooseExistingPortfolioModal extends PageComponent {
	private By clickToCheckPortfolio = By.xpath("//button[@ng-click='chooseChapter(portfolio)']");
	
	public ChoosePortfolioChapterModal choosePortfolio() {
		getDriver().findElement(clickToCheckPortfolio).click();
		return new ChoosePortfolioChapterModal();
	}

}
