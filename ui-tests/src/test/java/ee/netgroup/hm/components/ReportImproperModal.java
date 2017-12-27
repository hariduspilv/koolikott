package ee.netgroup.hm.components;

import org.openqa.selenium.By;
import ee.netgroup.hm.helpers.Helpers;
import ee.netgroup.hm.page.MaterialPage;
import ee.netgroup.hm.page.PortfolioPage;

public class ReportImproperModal extends Components{
	
	private By reportContent = By.name("LO_CONTENT");
	private static By reportDescription = By.xpath("//textarea[@data-ng-model='data.reportingText']");
	private static By notify = By.xpath("//button[@data-ng-click='sendReport()']");

	
	public ReportImproperModal setImproperReason() {
		Helpers.waitForMilliseconds(1000);
		Helpers.waitForClickable(reportContent);
		getDriver().findElement(reportContent).click();
		return this;
	}
	
	public ReportImproperModal setImproperDescription() {
		Helpers.waitForVisibility(reportDescription);
		getDriver().findElement(reportDescription).click();
		Helpers.waitForMilliseconds(1000);
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

	public String getLogginInIsRequiredText() {
		return LoginModal.getLogginInIsRequiredText();
	}

	public LoginModal getLogInPopUp() {
		return LoginModal.getLogInPopUp();
	}

	public static ReportImproperModal getReportImproperPopUp() {
		return new ReportImproperModal();
	}


}
