package ee.netgroup.hm.components;

import org.openqa.selenium.By;
import ee.netgroup.hm.helpers.Helpers;
import ee.netgroup.hm.page.MaterialPage;
import ee.netgroup.hm.page.PortfolioPage;

public class ReportImproperPopUp extends Component{

	private By reportContent = By.name("LO_CONTENT");
	private static By reportDescription = By.xpath("//textarea[@data-ng-model='data.reportingText']");
	private static By notify = By.xpath("//button[@data-ng-click='sendReport()']");


	public ReportImproperPopUp setImproperReason() {
		Helpers.waitForVisibility(reportContent);
		getDriver().findElement(reportContent).click();
		return this;
	}

	public ReportImproperPopUp setImproperDescription() {
		Helpers.waitForVisibility(reportDescription);
		getDriver().findElement(reportDescription).click();
		getDriver().findElement(reportDescription).sendKeys("Automaat test raporteeris selle ebasobivaks");
		Helpers.waitForMilliseconds(3000);
		return this;
	}

	public PortfolioPage clickNotify() {
		getDriver().findElement(notify).click();
		return new PortfolioPage();
	}

	public MaterialPage clickNotifyMaterial() {
		getDriver().findElement(notify).click();
		return new MaterialPage();
	}
}
