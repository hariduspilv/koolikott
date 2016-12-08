package ee.hm.dop.components;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import ee.hm.dop.helpers.PageHelpers;


public class EditMaterialMainPart extends PageComponent {
	
	private By nextStep = By.xpath("//button[@data-ng-click='step.nextStep()']");
	private By selectedLanguage = By.cssSelector("#add-material-language-select md-select-value.md-select-value div.md-text span");
	
	public EditMaterialTaxonomyPart clickNextStep() {
		PageHelpers.waitForSeconds(1500);
		getDriver().findElement(nextStep).sendKeys(Keys.ENTER);
		return new EditMaterialTaxonomyPart();

	}	
	
	public String getMaterialLanguage() {
		return getDriver().findElement(selectedLanguage).getText();
	}


}
