package ee.hm.dop.page;

import org.openqa.selenium.By;

import ee.hm.dop.helpers.PageHelpers;

public class MyMaterialsPage extends Page {
	
	private By addMaterialMessage = By.cssSelector("h3");
	
	public boolean getAddMaterialMessageText() {
		PageHelpers.waitForVisibility(addMaterialMessage);
		return getDriver().findElement(addMaterialMessage).getText().contains("Materjali lisamiseks");

	}

}
