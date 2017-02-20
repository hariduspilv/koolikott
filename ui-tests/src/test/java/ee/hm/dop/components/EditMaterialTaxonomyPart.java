package ee.hm.dop.components;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;


import ee.hm.dop.helpers.PageHelpers;
import ee.hm.dop.page.MaterialPage;

public class EditMaterialTaxonomyPart extends PageComponent {
	
	private By insertTag = By.xpath("//input[contains(@placeholder, 'Lisa märksõna')]");
	private By updateMaterialButton = By.id("add-material-create-button");
	
	public EditMaterialTaxonomyPart insertTagAndEnter() {
		getDriver().findElement(insertTag).sendKeys(PageHelpers.generateRegisterNumber(18));
		getDriver().findElement(insertTag).sendKeys(Keys.ENTER);
		PageHelpers.waitForSeconds(2500);
		return this;

	}
	
	public MaterialPage clickUpdateMaterial() {
		getDriver().findElement(updateMaterialButton).sendKeys(Keys.ENTER);
		PageHelpers.waitForSeconds(3500);
		return new MaterialPage();

	}

}
