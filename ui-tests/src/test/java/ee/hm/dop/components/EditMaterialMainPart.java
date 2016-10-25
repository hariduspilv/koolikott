package ee.hm.dop.components;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;

import ee.hm.dop.helpers.PageHelpers;
import ee.hm.dop.page.MaterialPage;

public class EditMaterialMainPart extends PageComponent {
	
	private By nextStep = By.xpath("//button[@data-ng-click='step.nextStep()']");
	private By selectedLanguage = By.cssSelector("span > div.md-text > span");
	private By boldButton = By.name("bold");
	private By italicButton = By.name("italics");
	private By ulButton = By.name("ul");
	private By olButton = By.name("ol");
	private By preButton = By.name("pre");
	private By quoteButton = By.name("quote");
	private By insertLink = By.name("insertLink");
	private By refreshMaterial = By.id("add-material-create-button");
	private By materialDescription = By.xpath("(//div[starts-with(@id, 'taTextElement')])");
	
	public EditMaterialTaxonomyPart clickNextStep() {
		PageHelpers.waitForSeconds(1500);
		getDriver().findElement(nextStep).sendKeys(Keys.ENTER);
		return new EditMaterialTaxonomyPart();

	}
	
	public EditMaterialMainPart clickToSelectDescription() {
		PageHelpers.waitForVisibility(materialDescription);
		getDriver().findElement(materialDescription).sendKeys(Keys.CONTROL + "a");
		return this;

	}
	
	public EditMaterialMainPart clickToSelectBold() {
		PageHelpers.waitForVisibility(boldButton);
		getDriver().findElement(boldButton).click();
		return this;

	}
	
	public MaterialPage clickToRefreshMaterial() {
		getDriver().findElement(refreshMaterial).click();
		return new MaterialPage();

	}
	
	public EditMaterialMainPart clickToSelectItalic() {
		getDriver().findElement(italicButton).click();
		return this;

	}
	
	public EditMaterialMainPart clickToInsertLink() {
		getDriver().findElement(insertLink).click();
		PageHelpers.uploadFile1("c:\\files\\test2.docx");
		return this;

	}
	
	public EditMaterialMainPart clickToSelectUl() {
		getDriver().findElement(ulButton).click();
		return this;

	}
	
	public EditMaterialMainPart clickToSelectQuote() {
		getDriver().findElement(quoteButton).click();
		return this;

	}
	
	public EditMaterialMainPart clickToSelectPre() {
		getDriver().findElement(preButton).click();
		return this;

	}
	
	public EditMaterialMainPart clickToSelectOl() {
		getDriver().findElement(olButton).click();
		return this;

	}
	
	
	public String getMaterialLanguage() {
		return getDriver().findElement(selectedLanguage).getText();
	}


}
