package ee.netgroup.hm.components;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import ee.netgroup.hm.helpers.Arrays;
import ee.netgroup.hm.helpers.Constants;
import ee.netgroup.hm.helpers.Helpers;
import ee.netgroup.hm.page.EditPortfolioPage;
import ee.netgroup.hm.page.MaterialPage;

public class MaterialPopUp extends Component{
	
	private By linkField = By.id("add-material-url-input");
	private By inserTitle = By.name("title");
	private By insertPhoto = By.xpath("//div[@ng-model='newPicture']"); 
	private By descriptionField = By.id("add-material-description-input");
	private By educationalContext = By.name("taxonForm");
	private By basicEducation = By.cssSelector("md-option[data-translate='PRESCHOOLEDUCATION']");
	private By subjectArea = By.xpath("(//md-select[contains(@id, 'taxonDomainSelect')])[2]");
	private By subject = By.cssSelector("md-option[data-translate='DOMAIN_ESTONIAN']");
	private By targetGroup = By.xpath("//form[contains(@name, 'targetGroupForm')]");
	private By selectedTargetGroup = By.xpath("(//md-option[contains(@value, 'ZERO_FIVE')])[2]");
	private By closeButton = By.xpath("//md-icon[@aria-label='close']");
	private By createMaterialButton = By.id("create-material-button");
	private By deletedMaterialValidationError = By.xpath("//div[@class='md-input-message-animation']");
	private By existingMaterialValidationError = By.cssSelector("div.md-input-message-animation > span");
	private By authorFirstName = By.id("material-author-0-name");
	private By authorSurname= By.name("surname");
	private By publisherName = By.xpath("//input[@data-ng-model='material.publishers[0].name']");
	public static String newMaterialUrl = Helpers.generateUrl();
	private By illustrationAuthorCheckbox = By.xpath("//md-checkbox[@data-ng-click='$ctrl.setAuthorToUser()']");
	private By illustrationSource = By.name("pictureSource");
	private By additionalData = By.id("open-additional-info");
	private By reviewLink = By.name("peerReview");
	private By licenseType = By.id("licenseTypeSelect");
	private By licenseTypeOption = By.xpath("//md-option[@data-translate='LICENSETYPE_CCBY']");
	private By selectedLanguage = By.cssSelector("#add-material-language-select md-select-value.md-select-value div.md-text span");
	private By updateMaterialButton = By.id("create-material-button");
	private By insertFile = By.xpath("//span[@data-translate='FILE_WORD']");
	
	
	public MaterialPopUp setHyperLink() {
		getDriver().findElement(linkField).clear();
		getDriver().findElement(linkField).sendKeys("http://a" + Helpers.generateRandomUrl());
		return this;
	}
	
	public MaterialPopUp setNewHyperLink() {
		getDriver().findElement(linkField).clear();
		getDriver().findElement(linkField).sendKeys(newMaterialUrl);
		Helpers.waitForMilliseconds(2000);
		return this;
	}

	public MaterialPopUp setMaterialTitle() {
		getDriver().findElement(inserTitle).sendKeys(Helpers.randomElement(Arrays.materialTitlesArray));
		return this;
	}

	public MaterialPopUp uploadIllustration() {
		Helpers.waitForClickable(insertPhoto);
		getDriver().findElement(insertPhoto).click();
		Helpers.uploadFile();
		return this;
	}

	public MaterialPopUp addDescription() {
		getDriver().findElement(descriptionField).sendKeys(Helpers.randomElement(Arrays.descriptionArray));
		return this;
	}

	public MaterialPopUp selectEducation() {
		Helpers.waitForVisibility(educationalContext);
		getDriver().findElement(educationalContext).click();
		getDriver().findElements(basicEducation).get(1).click();
		Helpers.waitForMilliseconds(1000);
		return this;
	}

	public MaterialPopUp selectSubjectArea() {
		getDriver().findElement(subjectArea).click();
		getDriver().findElement(subject).click();
		Helpers.waitForMilliseconds(1000);
		return this;
	}

	public MaterialPopUp selectTargetGroup() {
		//Helpers.waitForClickable(targetGroup);
		getDriver().findElements(targetGroup).get(1).click();
		getDriver().findElement(selectedTargetGroup).click();
		getDriver().findElements(closeButton).get(2).click();
		return this;
	}

	public MaterialPage clickCreateMaterial() {
		Helpers.moveToElement(createMaterialButton);
		getDriver().findElement(createMaterialButton).click();
		return new MaterialPage();
	}

	public MaterialPopUp insertDeletedMaterialUrl() {
		getDriver().findElement(linkField).sendKeys(Constants.deletedMaterial);
		return this;
	}

	public String getValidationError() {
		Helpers.waitForVisibility(deletedMaterialValidationError);
		return getDriver().findElement(deletedMaterialValidationError).getText();
	}

	public MaterialPopUp insertExistingMaterialUrl() {
		getDriver().findElement(linkField).sendKeys(Constants.existingMaterial);
		return this;
	}
	
	public String getExistingMaterialValidationError() {
		Helpers.waitForVisibility(existingMaterialValidationError);
		return getDriver().findElement(existingMaterialValidationError).getText();
	}

	public MaterialPopUp setAuthorFirstName() {
		Helpers.waitForMilliseconds(1000);
		getDriver().findElement(authorFirstName).clear();
		getDriver().findElement(authorFirstName).sendKeys(Helpers.randomElement(Arrays.firstNamesArray));
		Helpers.waitForMilliseconds(1000);
		return this;
	}

	public MaterialPopUp setAuthorSurName() {
		getDriver().findElement(authorSurname).clear();
		getDriver().findElement(authorSurname).sendKeys(Helpers.randomElement(Arrays.SurnamesArray));
		return this;
	}

	public MaterialPopUp setPublisherName() {
		getDriver().findElement(publisherName).sendKeys("Pegasus");
		return this;
	}

	public EditPortfolioPage clickCreateMaterialPortfolio() {
		getDriver().findElement(createMaterialButton).click();
		return new EditPortfolioPage();
	}

	public MaterialPopUp checkIllustrationIamAuthor() {
		getDriver().findElement(illustrationAuthorCheckbox).click();
		return this;
	}

	public MaterialPopUp addIllustrationSource() {
		getDriver().findElement(illustrationSource).sendKeys("Allikas Automaattest");
		return this;
	}

	public MaterialPopUp clickAdditionalData() {
		getDriver().findElement(additionalData).click();
		return this;
	}

	public MaterialPopUp addReviewLink() {
		getDriver().findElement(reviewLink).sendKeys(Helpers.generateUrl());
		return this;
	}

	public MaterialPopUp addMaterialLicenseType() {
		getDriver().findElement(licenseType).click();
		getDriver().findElements(licenseTypeOption).get(1).click();
		return this;
	}
	
	public String getMaterialLanguage() {
		return getDriver().findElement(selectedLanguage).getText();
	}

	public MaterialPage clickUpdateMaterial() {
		getDriver().findElement(updateMaterialButton).sendKeys(Keys.ENTER);
		Helpers.waitForMilliseconds(1000);
		return new MaterialPage();
	}

	public MaterialPopUp uploadFile() {
		Helpers.waitForClickable(insertFile);
		getDriver().findElement(insertFile).click();
		Helpers.uploadFile();
		return this;
	}
	

}
