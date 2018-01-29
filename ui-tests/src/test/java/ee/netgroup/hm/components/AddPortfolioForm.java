package ee.netgroup.hm.components;

import org.openqa.selenium.By;
import ee.netgroup.hm.helpers.Arrays;
import ee.netgroup.hm.helpers.Helpers;
import ee.netgroup.hm.page.EditPortfolioPage;

public class AddPortfolioForm extends Components{
	
	private By portfolioTitle = By.id("add-portfolio-title-input");
	private By educationalContext = By.xpath("(//md-select[@id='taxonEducationalSelect'])[2]");
	private By basicEducation = By.cssSelector("md-option[data-translate='PRESCHOOLEDUCATION']");
	private By subjectArea = By.xpath("(//md-select[contains(@id, 'taxonDomainSelect')])[2]");
	private By subject = By.cssSelector("md-option[data-translate='DOMAIN_ESTONIAN']");
	private By ageGroup = By.xpath("//form[1][contains(@name, 'targetGroupForm')]");
	private By age = By.xpath("(//md-option[contains(@value, 'ZERO_FIVE')])[2]");
	private By closeButton = By.xpath("//md-icon[@aria-label='close']");
	private By insertPhoto = By.xpath("//div[@ng-model='newPicture']"); 
	private By descriptionField = By.id("add-portfolio-description-input");
	private By createPortfolio = By.id("add-portfolio-create-button");
	private By savePortfolio = By.id("add-portfolio-edit-button");

	private By portfolioLicenseType = By.xpath("//md-select[@data-ng-model='newPortfolio.licenseType']");
	private By portfolioLicenseTypeOption = By.xpath("//md-option[@data-translate='LICENSETYPE_LONG_NAME_CCBYNCND']");
	private By portfolioIllustrationAuthor = By.id("newPortfolio-picture-author-");
	private By portfolioIllustrationSource = By.name("pictureSource");
	private By portfolioIllustrationLicenseType = By.id("pictureLicenseTypeSelect");
	private By portfolioIllustrationLicenseTypeOption = By.xpath("//md-option[@data-translate='LICENSETYPE_LONG_NAME_CCBYNCSA']");
	
	public AddPortfolioForm setPortfolioTitle() {
		Helpers.waitForMilliseconds(1000);
		getDriver().findElement(portfolioTitle).sendKeys(Helpers.randomElement(Arrays.portfolioTitlesArray));
		return this;
	}

	public AddPortfolioForm selectEducationalContext() {
		Helpers.waitForVisibility(educationalContext);
		getDriver().findElement(educationalContext).click();
		getDriver().findElements(basicEducation).get(1).click();
		Helpers.waitForMilliseconds(1000);
		return this;
	}

	public AddPortfolioForm selectSubjectArea() {
		getDriver().findElement(subjectArea).click();
		getDriver().findElement(subject).click();
		return this;
	}

	public AddPortfolioForm selectAgeGroup() {
		getDriver().findElement(ageGroup).click();
		getDriver().findElement(age).click();
		getDriver().findElements(closeButton).get(2).click();
		Helpers.waitForMilliseconds(1000);
		return this;
	}

	public AddPortfolioForm uploadPhoto() {
		Helpers.waitForVisibility(insertPhoto);
		getDriver().findElement(insertPhoto).click();
		Helpers.uploadFile();
		return this;
	}

	public AddPortfolioForm addDescription(){
		getDriver().findElement(descriptionField).sendKeys(Helpers.randomElement(Arrays.descriptionArray));
		return this;
	}

	public EditPortfolioPage clickCreatePortfolio() {
		getDriver().findElement(createPortfolio).click();
		return new EditPortfolioPage();
	}

	public AddPortfolioForm insertSpecificPortfolioTitle(String title) {
		Helpers.waitForVisibility(portfolioTitle);
		Helpers.waitForMilliseconds(1000);
		getDriver().findElement(portfolioTitle).sendKeys(title);
		Helpers.waitForMilliseconds(1000);
		return this;
	}

	public EditPortfolioPage clickSavePortfolio() {
		getDriver().findElement(savePortfolio).click();
		Helpers.waitForMilliseconds(1500);
		return new EditPortfolioPage();
	}

	public AddPortfolioForm setPortfolioLicenseType() {
		getDriver().findElement(portfolioLicenseType).click();
		getDriver().findElements(portfolioLicenseTypeOption).get(1).click();
		return this;
	}

	public AddPortfolioForm setPortfolioIllustrationAuthor() {
		getDriver().findElement(portfolioIllustrationAuthor).sendKeys(Helpers.randomElement(Arrays.surnamesArray));
		return this;
	}
	
	public AddPortfolioForm setPortfolioIllustrationSource() {
		getDriver().findElement(portfolioIllustrationSource).sendKeys("Allikas Automaattest");
		return this;
	}
	
	public AddPortfolioForm setPortfolioIllustrationLicenseType() {
		getDriver().findElement(portfolioIllustrationLicenseType).click();
		getDriver().findElements(portfolioIllustrationLicenseTypeOption).get(1).click();
		return this;
	}
	

}
