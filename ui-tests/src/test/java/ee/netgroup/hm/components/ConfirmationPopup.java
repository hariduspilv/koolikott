package ee.netgroup.hm.components;

import org.openqa.selenium.By;
import ee.netgroup.hm.page.LandingPage;
import ee.netgroup.hm.page.MaterialPage;
import ee.netgroup.hm.page.PortfolioPage;

public class ConfirmationPopup extends Component{
	
	private static By confirm = By.xpath("//button[@class='md-primary md-confirm-button md-button md-default-theme md-ink-ripple']");
	
	
	public LandingPage clickConfirm() {
		getDriver().findElement(confirm).click();
		return new LandingPage();
	}

	public PortfolioPage clickNotify() {
		getDriver().findElement(confirm).click();
		return new PortfolioPage();
	}

	public MaterialPage clickConfirmDeleteMaterial() {
		getDriver().findElement(confirm).click();
		return new MaterialPage();
	}

	public MaterialPage clickReportMaterial() {
		getDriver().findElement(confirm).click();
		return new MaterialPage();
	}

	

}
