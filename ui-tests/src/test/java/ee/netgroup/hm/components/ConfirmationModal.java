package ee.netgroup.hm.components;

import org.openqa.selenium.By;
import ee.netgroup.hm.page.LandingPage;
import ee.netgroup.hm.page.MaterialPage;

public class ConfirmationModal extends Components{
	
	private static By confirm = By.xpath("//button[@class='md-primary md-confirm-button md-button md-ink-ripple md-default-theme']");
	
	
	public LandingPage clickConfirm() {
		getDriver().findElement(confirm).click();
		return new LandingPage();
	}

	public MaterialPage clickConfirmDeleteMaterial() {
		getDriver().findElement(confirm).click();
		return new MaterialPage();
	}


	

}
