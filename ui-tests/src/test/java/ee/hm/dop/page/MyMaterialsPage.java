package ee.hm.dop.page;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import ee.hm.dop.helpers.PageHelpers;

public class MyMaterialsPage extends Page {
	
	private By addMaterialMessage = By.cssSelector("h3");
	private By materialBox = By.xpath("//div/md-icon[text()='radio_button_unchecked']");
	private By selectMaterial1 = By.xpath("//div/md-icon[text()='check_circle']");
	private By searchResultMaterial = By.xpath("//h4[@data-ng-bind='getCorrectLanguageTitle(material)']");
	private By starIcon = By.xpath("//div[@aria-label='Lisa lemmikuks']");
	private By selectMaterialBox = By.xpath("//md-card[@data-ng-click='navigateTo(material, $event)']");
	private By materialMessage = By.cssSelector("div.md-toast-content");
	
	public boolean getAddMaterialMessageText() {
		PageHelpers.waitForVisibility(addMaterialMessage);
		return getDriver().findElement(addMaterialMessage).getText().contains("Materjali lisamiseks");

	}
	
	public MyMaterialsPage clickToSelectMaterial() {
		PageHelpers.waitForSeconds(2500);
		Actions builder = new Actions(getDriver());
		WebElement selectMaterialElement = getDriver().findElement(materialBox);
		builder.moveToElement(selectMaterialElement).perform();
		getDriver().findElement(selectMaterial1).click();
		return this;
	}
	
	public MaterialPage openMaterial() {
		PageHelpers.waitForSeconds(2000);
		PageHelpers.waitForVisibility(searchResultMaterial);
		getDriver().findElement(searchResultMaterial).click();
		PageHelpers.waitForSeconds(1500);
		return new MaterialPage();

	}
	
	public MyMaterialsPage clickToSelectStar() {
		PageHelpers.waitForSeconds(1500);
		PageHelpers.waitForVisibility(selectMaterialBox);
		Actions builder = new Actions(getDriver());
		WebElement selectMaterialElement = getDriver().findElement(selectMaterialBox);
		builder.moveToElement(selectMaterialElement).perform();
		getDriver().findElement(starIcon).click();
		return this;
	}
	
	public boolean successMessageIsDisplayed() {
		return getDriver().findElement(materialMessage).isDisplayed();

	}
	
	


}
