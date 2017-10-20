package ee.netgroup.hm.components;

import org.openqa.selenium.By;
import ee.netgroup.hm.helpers.Arrays;
import ee.netgroup.hm.helpers.Helpers;
import ee.netgroup.hm.page.EditPortfolioPage;

public class AddPortfolioForm extends Component{
	
	private By portfolioTitle = By.id("add-portfolio-title-input");
	private By educationalContext = By.xpath("(//md-select[@id='taxonEducationalSelect'])[2]");
	private By basicEducation = By.cssSelector("md-option[data-translate='PRESCHOOLEDUCATION']");
	private By subjectArea = By.xpath("(//md-select[contains(@id, 'taxonDomainSelect')])[2]");
	private By subject = By.cssSelector("md-option[data-translate='DOMAIN_ESTONIAN']");
	private By ageGroup = By.xpath("//md-select[contains(@data-ng-model, 'selectedTargetGroup')][contains(@aria-invalid,'true')]");
	private By age = By.xpath("(//md-option[contains(@value, 'ZERO_FIVE')])[2]");
	private By closeButton = By.xpath("//button[@data-ng-click='closeSelect()']");
	private By insertPhoto = By.cssSelector("p.text-small.text-light > span");
	private By openSummaryField = By.xpath("//input[@data-ng-click='openSummary()']");
	private By descriptionField = By.xpath("(//div[starts-with(@id, 'taTextElement')])");
	private By createPortfolio = By.id("add-portfolio-create-button");
	private By savePortfolio = By.id("add-portfolio-edit-button");

	
	public AddPortfolioForm setPortfolioTitle() {
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
		getDriver().findElements(closeButton).get(1).click();
		return this;
	}

	public AddPortfolioForm uploadPhoto() {
		Helpers.waitForVisibility(insertPhoto);
		getDriver().findElement(insertPhoto).click();
		Helpers.uploadFile();
		return this;
	}

	public AddPortfolioForm addDescription() {
		getDriver().findElement(openSummaryField).click();
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
	
	

}
