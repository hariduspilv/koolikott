package ee.netgroup.hm.page;

import org.openqa.selenium.By;
import ee.netgroup.hm.components.AddMaterialsToPortfolioToolbar;
import ee.netgroup.hm.helpers.Constants;
import ee.netgroup.hm.helpers.Helpers;

public class MyMaterialsPage extends Page{
	
	private By materialMessage = By.cssSelector("span.md-toast-text");
	private By selectMaterialBox = By.xpath("//md-card[@data-ng-click='$ctrl.navigateTo($ctrl.learningObject, $event)']");
	private By starIcon = By.xpath("//div[@class='md-icon-button md-button favorite']");
	private static By addMaterialMessage = By.cssSelector("h3");

	public MaterialPage openMaterial() {
		getDriver().findElement(Constants.firstMaterial).click();
		return new MaterialPage();
	}

	public String getSuccessMessage() {
		Helpers.waitForVisibility(materialMessage);
		return getDriver().findElement(materialMessage).getText();
	}

	public AddMaterialsToPortfolioToolbar clickToSelectMaterial() {
		return AddMaterialsToPortfolioToolbar.clickToSelectMaterial();
	}

	public MyMaterialsPage clickToSelectStar() {
		Helpers.waitForVisibility(selectMaterialBox);
		Helpers.moveToElement(selectMaterialBox);
		Helpers.waitForVisibility(starIcon);
		getDriver().findElement(starIcon).click();
		return this;
	}

	public static boolean getAddMaterialMessageText() {
		Helpers.waitForMilliseconds(1000);
		return getDriver().findElement(addMaterialMessage).getText().contains("Materjali lisamiseks");
	}



}
