package ee.netgroup.hm.page;

import org.openqa.selenium.By;

public class DeletedMateriasPage extends Page{
	
	private By deletedMaterial = By.cssSelector(".md-cell:nth-child(4)");

	public MaterialPage clickOpenMaterial() {
		getDriver().findElement(deletedMaterial).click();
		return new MaterialPage();
	}

}
