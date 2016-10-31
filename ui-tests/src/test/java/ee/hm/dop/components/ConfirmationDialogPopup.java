package ee.hm.dop.components;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;

import ee.hm.dop.helpers.PageHelpers;
import ee.hm.dop.page.PortfolioViewPage;

public class ConfirmationDialogPopup extends PageComponent {
	
	private By notifyButton = By.xpath("//button[@ng-click='dialog.hide()']");
	
	public PortfolioViewPage confirmImproperContent() {
		getDriver().findElement(notifyButton).sendKeys(Keys.ENTER);
		PageHelpers.waitForSeconds(2500);
		return new PortfolioViewPage();
	}

}
