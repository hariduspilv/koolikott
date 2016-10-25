package ee.hm.dop.page;

import java.util.Random;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import ee.hm.dop.components.AddPortfolioBasic;
import ee.hm.dop.components.ChooseExistingPortfolioModal;
import ee.hm.dop.components.EditMaterialMainPart;
import ee.hm.dop.helpers.PageHelpers;

public class MaterialPage extends Page {

	private By creatorName = By.xpath("//p[@data-ng-if='isNullOrZeroLength(material.authors)']");
	private By menuBar = By.xpath("//md-icon[text()='more_vert']");
	private By editMaterial = By.xpath("//button[@data-ng-click='edit()']");
	private By addedTag = By.xpath("//a[@data-ng-click='getTagSearchURL($event, $chip.tag)']");
	private By publisherName = By.xpath("//span[@data-ng-repeat='publisher in material.publishers']");
	private By likeIcon = By.cssSelector("div.rating-button.md-caption > md-icon.material-icons");
	private By isLiked = By.cssSelector("div.rating-button.md-caption > span");
	private By tagRow = By.name("tag");
	private By fabButton = By.id("add-portfolio");
	private By addMaterialToExistingPortfolio = By.xpath("//button[@data-ng-click='showAddMaterialsToPortfolioDialog()']");
	private By materialBox = By.xpath("//div/md-icon[text()='radio_button_unchecked']");
	private By selectMaterial1 = By.xpath("//div/md-icon[text()='check_circle']");
	private By searchResultMaterial = By.xpath("//h4[@data-ng-bind='getCorrectLanguageTitle(material)']");
	private By showMoreButton = By.xpath("//button[@ng-click='showMore()']");
	private By materialDescription = By.xpath("//div[@data-ng-bind-html='getCorrectLanguageString(material.descriptions)']");
	private By materialType = By.xpath("//md-card[@id='material-card']/md-card-content/div/div/div[2]/div[3]/p/span");

	public MaterialPage insertTags() {
		for (int i = 0; i < 12; i++) {
			String[] tagsArray = { "life", "school", "followback", "art", "fashion", "sky", "beauty", "noir", "ink",
					"design", "craft", "beatles", "antique", "monoart", "architect", "linedesign", "style", "classical",
					"vintage", "vector", "focus", "exposure", "usa", "sport", "brand", "fancy", "france", "bag", "free",
					"musthave", "look", "foundation", "lookbook", "december", "every", "can", "people", "movingforward",
					"decoration", "cold", "morning", "sleepy", "plot", "story", "readinglist", "imagine", "trip",
					"festival", "instrument", "musicnotes", "key", "rock", "mozart", "green", "summer", "reflection",
					"sand", "seaside", "landscape", "bloom", "mountains", "ocean", "skyfall", "cloud9", "outside",
					"worldwide", "winter", "forest", "fall", "foliage", "orange", "crowd", "team", "workout", "yoga",
					"geometry", "composition", "urban", "grammar", "home", "music", "done", "friends", "reality",
					"algebra", "workshop", "more", "unknown", "future", "contest", "famous", "addict", "memories",
					"road", "motivation", "plants", "textbook", "abstract", "acorn", "acronym", "ad lib", "adept",
					"adobe", "aeon", "aerostat", "africa", "agenda", "agile", "aircraft", "alpha" };
			String randomTitle = tagsArray[new Random().nextInt(tagsArray.length)];
			getDriver().findElement(tagRow).sendKeys(randomTitle + "");
			getDriver().findElement(tagRow).sendKeys(Keys.ENTER);
			PageHelpers.waitForSeconds(1000);

		}
		return this;
	}
	
	public MaterialPage moveCursorToAddMaterialToExistingPortfolio() {
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
	
	public AddPortfolioBasic clickAddPortfolio() {
		PageHelpers.waitForSeconds(1500);
		PageHelpers.waitForVisibility(fabButton);
		getDriver().findElement(fabButton).click();
		return new AddPortfolioBasic();
	}
	
	public MaterialPage openSearchResultMaterial() {
		PageHelpers.waitForSeconds(2000);
		PageHelpers.waitForVisibility(searchResultMaterial);
		getDriver().findElement(searchResultMaterial).click();
		PageHelpers.waitForSeconds(1500);
		return new MaterialPage();

	}
	
	public MaterialPage clickToSelectMaterial() {
		PageHelpers.waitForSeconds(2500);
		Actions builder = new Actions(getDriver());
		WebElement selectMaterialElement = getDriver().findElement(materialBox);
		builder.moveToElement(selectMaterialElement).perform();
		getDriver().findElement(selectMaterial1).click();
		return this;
	}

	public boolean showMoreButtonIsDisplayed() {
		PageHelpers.waitForSeconds(1500);
		return getDriver().findElement(showMoreButton).isDisplayed();

	}

	public String getCreatorName() {
		return getDriver().findElement(creatorName).getText();

	}
	
	
	public MaterialPage checkShowButton() {
		PageHelpers.waitForVisibility(showMoreButton);
		if (getDriver().findElement(showMoreButton).isDisplayed()){
			getDriver().findElement(showMoreButton).click();
		}
		return this;
	}
	

	public String getLikesNumber() {
		return getDriver().findElement(isLiked).getText();

	}

	public String getPublisherName() {
		PageHelpers.waitForSeconds(2500);
		return getDriver().findElement(publisherName).getText();

	}
	
	public String getMaterialType() {
		PageHelpers.waitForSeconds(2500);
		return getDriver().findElement(materialType).getText();

	}
	
	public String getMaterialLanguage() {
		PageHelpers.waitForSeconds(2500);
		return getDriver().findElement(materialType).getText();

	}
	
	public String getMaterialDescription() {
		PageHelpers.waitForSeconds(2500);
		return getDriver().findElement(materialDescription).getText();

	}

	public String getTagText() {
		return getDriver().findElement(addedTag).getText();

	}
	

	public MaterialPage selectActionFromMenu() {
		getDriver().findElement(menuBar).click();
		return this;

	}

	public MaterialPage likeMaterial() {
		PageHelpers.waitForSeconds(4500);
		getDriver().findElement(likeIcon).click();
		PageHelpers.waitForSeconds(1500);
		return this;
	}
	

	public EditMaterialMainPart clickEditMaterial() {
		getDriver().findElement(editMaterial).sendKeys(Keys.ENTER);
		return new EditMaterialMainPart();

	}

}
