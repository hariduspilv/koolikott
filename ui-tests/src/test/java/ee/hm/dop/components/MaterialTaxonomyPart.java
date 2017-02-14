package ee.hm.dop.components;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import ee.hm.dop.helpers.PageHelpers;
import ee.hm.dop.page.MaterialPage;

public class MaterialTaxonomyPart extends PageComponent {

	private By targerGroup = By
			.xpath("//md-select[contains(@data-ng-model, 'selectedTargetGroup')][contains(@aria-invalid,'true')]");
	private By selectedTargetGroup = By.xpath("(//md-option[contains(@value, 'ZERO_FIVE')])[2]");
	private By selectedTargetGroup1 = By.xpath("/html/body/div[5]/md-select-menu/md-content/md-option[2]/div[1]/span");
	private By createMaterialButton = By.id("add-material-create-button");
	private By createMaterialButton1 = By.id("create-material-button");
	private By keyCompetences = By.xpath("//input[contains(@ng-model, '$mdAutocompleteCtrl.scope.searchText')]");
	private By CrossCurricularThemes = By.xpath(
			"/html/body/div[4]/md-dialog/form/md-dialog-content/md-tabs/md-tabs-content-wrapper/md-tab-content[2]/div/md-content/div[3]/div[3]/md-chips/md-chips-wrap/div/div/md-autocomplete/md-autocomplete-wrap/input");	
	private By nextStep = By.xpath("//button[@data-ng-click='step.nextStep()']");
	private By closeButton = By.xpath("//button[@data-ng-click='closeSelect()']");
	private By educationalContext = By.xpath("(//md-select[@id='taxonEducationalSelect'])[2]");
	private By basicEducation = By.cssSelector("md-option[data-translate='PRESCHOOLEDUCATION']");
	private By subjectArea = By.xpath("(//md-select[contains(@id, 'taxonDomainSelect')])[2]");
	private By subject = By.cssSelector("md-option[data-translate='DOMAIN_ESTONIAN']");

	
	public MaterialTaxonomyPart selectTargetGroup() {
		getDriver().findElement(targerGroup).click();
		getDriver().findElement(selectedTargetGroup).click();
		getDriver().findElements(closeButton).get(2).click();
		return this;

	}
	
	public MaterialTaxonomyPart selectEducation() {
		PageHelpers.waitForVisibility(educationalContext);
		getDriver().findElement(educationalContext).click();
		PageHelpers.waitForSeconds(1000);
		getDriver().findElements(basicEducation).get(1).click();
		return this;

	}
	
	public MaterialTaxonomyPart selectSubjectArea() {
		getDriver().findElement(subjectArea).click();
		PageHelpers.waitForSeconds(1000);
		getDriver().findElement(subject).click();
		return this;
	}

	public MaterialTaxonomyPart selectTargetGroup1() {
		getDriver().findElement(targerGroup).click();
		getDriver().findElement(selectedTargetGroup1).click();
		PageHelpers.waitForSeconds(1500);
		return this;

	}
	
	public AdditionalDataPart clickNextStep() {
		getDriver().findElement(nextStep).click();
		return new AdditionalDataPart();

	}

	public MaterialTaxonomyPart selectKeyCompetences() {
		WebElement competencesInput = getDriver().findElement(keyCompetences);
		competencesInput.sendKeys("Suhtluspädevus");
		PageHelpers.waitForSeconds(1500);
		String listId = competencesInput.getAttribute("aria-owns");
		getDriver().findElement(By.cssSelector("#" + listId + " li.selected")).click();
		PageHelpers.waitForSeconds(1500);
		return this;

	}

	public MaterialTaxonomyPart selectCrossCurricularThemes() {
		WebElement themesInput = getDriver().findElement(CrossCurricularThemes);
		themesInput.sendKeys("Teabekeskkond");
		PageHelpers.waitForSeconds(1500);
		String listId = themesInput.getAttribute("aria-owns");
		getDriver().findElement(By.cssSelector("#" + listId + " li.selected")).click();
		PageHelpers.waitForSeconds(1500);
		return this;

	}

	public MaterialPage clickAddMaterial() {
		getDriver().findElement(createMaterialButton).click();
		PageHelpers.waitForSeconds(3500);
		return new MaterialPage();
	}

	public MaterialPage clickAddMaterial1() {
		getDriver().findElement(createMaterialButton1).click();
		PageHelpers.waitForSeconds(3500);
		return new MaterialPage();
	}

}
