package ee.netgroup.hm.page;

import org.openqa.selenium.By;

public class BrokenMateriasPage extends Page{

	private By sortByReportedDate = By.xpath("//th[@md-order-by='bySubmittedAt']");
	private By brokenMaterial = By.cssSelector(".md-cell:nth-child(4)");
	
	
	public BrokenMateriasPage clickSortByReportedDate() {
		getDriver().findElement(sortByReportedDate).click();
		return this;
	}

	public MaterialPage clickOpenMaterial() {
		getDriver().findElement(brokenMaterial).click();
		return new MaterialPage();
	}

}
