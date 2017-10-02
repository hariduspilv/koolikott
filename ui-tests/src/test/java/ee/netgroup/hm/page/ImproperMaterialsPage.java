package ee.netgroup.hm.page;

import org.openqa.selenium.By;

public class ImproperMaterialsPage extends Page{
	
	private By openMaterialButton = By.xpath("//button[@aria-label='Ava']");

	public MaterialPage clickOpenImproperMaterial() {
		getDriver().findElement(openMaterialButton).click();
		return new MaterialPage();
	}

}
