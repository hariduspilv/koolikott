package ee.hm.dop.components;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import ee.hm.dop.helpers.PageHelpers;



public class FabButton extends PageComponent {
	
	private By fabButton = By.id("add-portfolio");
	private By addMaterialButton = By.id("add-material");
	private By addMaterialToExistingPortfolio = By.xpath("//button[@data-ng-click='showAddMaterialsToPortfolioDialog()']");
	
	public AddPortfolioBasic clickAddPortfolio() {
		PageHelpers.waitForSeconds(1500);
		PageHelpers.waitForVisibility(fabButton);
		getDriver().findElement(fabButton).click();
		return new AddPortfolioBasic();
	}
	
	public FabButton moveCursorToAddPortfolio() {
		PageHelpers.waitForSeconds(2500);
		PageHelpers.waitForVisibility(fabButton);
		Actions builder = new Actions(getDriver());
		WebElement addPortfolio = getDriver().findElement(fabButton);
		builder.moveToElement(addPortfolio).perform();
		return this;
	}
	
	public FabButton moveCursorToAddMaterial() {
		PageHelpers.waitForVisibility(addMaterialButton);
		Actions builder = new Actions(getDriver());
		WebElement addMaterial = getDriver().findElement(addMaterialButton);
		builder.moveToElement(addMaterial).perform();
		return this;
	}
	
	public AddMaterialMainPart clickAddMaterial() {
		getDriver().findElement(addMaterialButton).click();
		return new AddMaterialMainPart();
		
	}
	
	
	public FabButton moveCursorToAddMaterialToExistingPortfolio() {
		Actions builder = new Actions(getDriver());
		WebElement addMaterialToPortfolio = getDriver().findElement(fabButton);
		builder.moveToElement(addMaterialToPortfolio).perform();
		PageHelpers.waitForVisibility(addMaterialToExistingPortfolio);
		return this;
	}
	
	public ChooseExistingPortfolioModal clickToAddMaterialToExistingPortfolio() {
		getDriver().findElement(addMaterialToExistingPortfolio).click();
		return new ChooseExistingPortfolioModal();
	}
}
