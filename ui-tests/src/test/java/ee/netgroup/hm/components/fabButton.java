package ee.netgroup.hm.components;

import org.openqa.selenium.By;
import ee.netgroup.hm.helpers.Constants;
import ee.netgroup.hm.helpers.Helpers;

public class fabButton extends Component{
	
	private static By addPortfolio = By.id("add-portfolio");
	private static By copyPortfolioButton = By.xpath("//button[@data-ng-click='copyPortfolio()']");
	private static By addMaterialButton = By.id("add-material");

	
	public static AddPortfolioForm clickAddPortfolio() {
		Helpers.waitForVisibility(addPortfolio);
		getDriver().findElement(addPortfolio).click();
		return new AddPortfolioForm();
	}
	
	public static AddPortfolioForm clickCopyPortfolio() {
		Helpers.waitForMilliseconds(3000);
		Helpers.waitUntilNotVisible(Constants.toastText);
		Helpers.moveToElement(addPortfolio);
		Helpers.waitForMilliseconds(1000);
		getDriver().findElement(copyPortfolioButton).click();
		return new AddPortfolioForm();
	}

	public static AddMaterialPopUp clickAddMaterial() {
		Helpers.moveToElement(addPortfolio);
		Helpers.waitForVisibility(addMaterialButton);
		getDriver().findElement(addMaterialButton).click();
		return new AddMaterialPopUp();
	}





}
