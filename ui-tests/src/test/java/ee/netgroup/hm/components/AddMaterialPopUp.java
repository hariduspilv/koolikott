package ee.netgroup.hm.components;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import ee.netgroup.hm.helpers.Arrays;
import ee.netgroup.hm.helpers.Constants;
import ee.netgroup.hm.helpers.Helpers;
import ee.netgroup.hm.page.EditPortfolioPage;
import ee.netgroup.hm.page.MaterialPage;

public class AddMaterialPopUp extends Component{
	
	private By linkField = By.id("add-material-url-input");
	private By inserTitle = By.name("title");
	private By insertPhoto = By.xpath("//span/md-icon[text()='insert_photo']");
	private By descriptionField = By.xpath("(//div[starts-with(@id, 'taTextElement')])");
	private By nextStep = By.xpath("//button[@data-ng-click='step.nextStep()']");
	private By educationalContext = By.xpath("(//md-select[@id='taxonEducationalSelect'])[2]");
	private By basicEducation = By.cssSelector("md-option[data-translate='PRESCHOOLEDUCATION']");
	private By subjectArea = By.xpath("(//md-select[contains(@id, 'taxonDomainSelect')])[2]");
	private By subject = By.cssSelector("md-option[data-translate='DOMAIN_ESTONIAN']");
	private By targetGroup = By.xpath("//md-select[contains(@data-ng-model, 'selectedTargetGroup')][contains(@aria-invalid,'true')]");
	private By selectedTargetGroup = By.xpath("(//md-option[contains(@value, 'ZERO_FIVE')])[2]");
	private By closeButton = By.xpath("//button[@data-ng-click='closeSelect()']");
	private By createMaterialButton = By.id("add-material-create-button");
	private By deletedMaterialValidationError = By.xpath("//div[@class='md-input-message-animation']");
	private By existingMaterialValidationError = By.cssSelector("div.md-input-message-animation > span");
	private By authorFirstName = By.id("material-author-0-name");
	private By authorSurname= By.xpath("//input[@data-ng-model='author.surname']");
	private By publisherName = By.xpath("//input[@data-ng-model='material.publishers[0].name']");
	private By insertTag = By.xpath("(//input[starts-with(@id, 'input-')])");
	
	
	public AddMaterialPopUp setHyperLink() {
		getDriver().findElement(linkField).sendKeys("http://a" + Helpers.generateUrl(30));
		return this;
	}

	public AddMaterialPopUp setMaterialTitle() {
		getDriver().findElement(inserTitle).sendKeys(Helpers.randomElement(Arrays.materialTitlesArray));
		return this;
	}

	public AddMaterialPopUp uploadPhoto() {
		Helpers.waitForClickable(insertPhoto);
		getDriver().findElement(insertPhoto).click();
		Helpers.uploadFile();
		return this;
	}

	public AddMaterialPopUp addDescription() {
		getDriver().findElement(descriptionField).sendKeys(Helpers.randomElement(Arrays.descriptionArray));
		return this;
	}

	public AddMaterialPopUp clickNextStep() {
		Helpers.waitForClickable(nextStep);
		getDriver().findElement(nextStep).click();
		return this;
	}

	public AddMaterialPopUp selectEducation() {
		Helpers.waitForVisibility(educationalContext);
		getDriver().findElement(educationalContext).click();
		getDriver().findElements(basicEducation).get(1).click();
		return this;
	}

	public AddMaterialPopUp selectSubjectArea() {
		getDriver().findElement(subjectArea).click();
		getDriver().findElement(subject).click();
		return this;
	}

	public AddMaterialPopUp selectTargetGroup() {
		getDriver().findElement(targetGroup).click();
		getDriver().findElement(selectedTargetGroup).click();
		getDriver().findElements(closeButton).get(2).click();
		return this;
	}

	public MaterialPage clickCreateMaterial() {
		getDriver().findElement(createMaterialButton).click();
		return new MaterialPage();
	}

	public AddMaterialPopUp insertDeletedMaterialUrl() {
		getDriver().findElement(linkField).sendKeys(Constants.deletedMaterial);
		return this;
	}

	public String getValidationError() {
		Helpers.waitForVisibility(deletedMaterialValidationError);
		return getDriver().findElement(deletedMaterialValidationError).getText();
	}

	public AddMaterialPopUp insertExistingMaterialUrl() {
		getDriver().findElement(linkField).sendKeys(Constants.existingMaterial);
		return this;
	}
	
	public String getExistingMaterialValidationError() {
		Helpers.waitForVisibility(existingMaterialValidationError);
		return getDriver().findElement(existingMaterialValidationError).getText();
	}

	public AddMaterialPopUp setAuthorFirstName() {
		getDriver().findElement(authorFirstName).clear();
		getDriver().findElement(authorFirstName).sendKeys(Helpers.randomElement(Arrays.firstNamesArray));
		return this;
	}

	public AddMaterialPopUp setAuthorSurName() {
		getDriver().findElement(authorSurname).clear();
		getDriver().findElement(authorSurname).sendKeys(Helpers.randomElement(Arrays.SurnamesArray));
		return this;
	}

	public AddMaterialPopUp setPublisherName() {
		getDriver().findElement(publisherName).sendKeys("Pegasus");
		return this;
	}

	public EditPortfolioPage clickCreateMaterialPortfolio() {
		getDriver().findElement(createMaterialButton).click();
		return new EditPortfolioPage();
	}

	public AddMaterialPopUp insertTagAndEnter() {
		getDriver().findElement(insertTag).sendKeys(Helpers.generateRegisterNumber(18));
		getDriver().findElement(insertTag).sendKeys(Keys.ENTER);
		Helpers.waitForSeconds(1000);
		return this;
	}

}
