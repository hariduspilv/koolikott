package ee.netgroup.hm.components;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import ee.netgroup.hm.helpers.Helpers;
import ee.netgroup.hm.page.MaterialPage;
import ee.netgroup.hm.page.Page;

public class EditMaterialPopUp extends Page{
	
	private By selectedLanguage = By.cssSelector("#add-material-language-select md-select-value.md-select-value div.md-text span");
	private By nextStep = By.xpath("//button[@data-ng-click='step.nextStep()']");
	private By insertTag = By.xpath("//input[contains(@placeholder, 'Lisa märksõna')]");
	private By updateMaterialButton = By.id("add-material-create-button");

	
	public String getMaterialLanguage() {
		return getDriver().findElement(selectedLanguage).getText();
	}

	public EditMaterialPopUp clickNextStep() {
		Helpers.waitForClickable(nextStep);
		getDriver().findElement(nextStep).click();
		return this;
	}

	public EditMaterialPopUp insertTagAndEnter() {
		getDriver().findElement(insertTag).sendKeys(Helpers.generateRegisterNumber(18));
		getDriver().findElement(insertTag).sendKeys(Keys.ENTER);
		Helpers.waitForMilliseconds(1000);
		return this;
	}

	public MaterialPage clickUpdateMaterial() {
		getDriver().findElement(updateMaterialButton).sendKeys(Keys.ENTER);
		Helpers.waitForMilliseconds(1000);
		return new MaterialPage();
	}

}
