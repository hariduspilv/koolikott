package ee.netgroup.hm.components;

import org.openqa.selenium.By;
import ee.netgroup.hm.page.PortfolioPage;

public class ReportImproperPopUp extends Component{
	
	//private static By reportContent = By.xpath("//div[@class='md-container md-ink-ripple']");
	private By reportContent = By.name("LO_CONTENT");
	private static By reportDescription = By.xpath("//textarea[@data-ng-model='data.reportingText']");
	private static By notify = By.xpath("//button[@class='md-primary md-button md-ink-ripple']");

	public ReportImproperPopUp setReportingReason() {
		getDriver().findElement(reportContent).click();
		getDriver().findElement(reportDescription).click();
		getDriver().findElement(reportDescription).sendKeys("Automaat test raporteeris selle ebasobivaks");
		return this;
	}

	public PortfolioPage clickNotify() {
		getDriver().findElement(notify).click();
		return new PortfolioPage();
	}
}
