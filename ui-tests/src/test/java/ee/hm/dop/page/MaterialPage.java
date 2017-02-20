package ee.hm.dop.page;

import java.util.Random;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import ee.hm.dop.components.EditMaterialMainPart;
import ee.hm.dop.helpers.PageHelpers;

public class MaterialPage extends Page {

	private By creatorName = By.xpath("//p[@data-ng-if='isNullOrZeroLength(material.authors)']");
	private By menuBar = By.xpath("//md-icon[text()='more_vert']");
	private By editMaterial = By.xpath("//button[@data-ng-click='edit()']");
	private By addedTag = By.xpath("//a[@data-ng-click='$ctrl.getTagSearchURL($event, $chip.tag)']");
	private By publisherName = By.xpath("//span[@data-ng-repeat='publisher in material.publishers']");
	private By likeIcon = By.xpath("//div[@data-ng-click='$ctrl.like()']");
	private By isLiked = By.xpath("//span[@data-ng-bind='$ctrl.rating.likes']");
	private By tagRow = By.xpath("(//input[starts-with(@id, 'fl-input-')])");
	private By unselectedStar = By.xpath("//div[@class='md-icon-button md-button favorite']");
	private By selectedStar = By.xpath("//md-icon[@data-ng-if='$ctrl.hasFavorited']");
	private By showMoreButton = By.xpath("//button[@ng-click='$ctrl.showMore()']");
	private By materialDescription = By.xpath("//div[@data-ng-bind-html='getCorrectLanguageString(material.descriptions)']");
	private By materialType = By.cssSelector(".audiotrack");
	private By selectMaterialBox = By.xpath("//div[@class='card-cover  audiotrack']");

	public MaterialPage insertTags() {
		for (int i = 0; i < 13; i++) {
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
	
	public MaterialPage unselectStar() {
		getDriver().navigate().refresh();
		PageHelpers.waitForVisibility(selectedStar);
		getDriver().findElement(selectedStar).click();
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
		PageHelpers.waitForSeconds(2500);
		//PageHelpers.waitForVisibility(showMoreButton);
		if (getDriver().findElement(showMoreButton).isDisplayed()){
			getDriver().findElement(showMoreButton).click();
		}else{
			return this;
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
		PageHelpers.waitForVisibility(selectMaterialBox);
		Actions builder = new Actions(getDriver());
		WebElement selectMaterialElement = getDriver().findElement(selectMaterialBox);
		builder.moveToElement(selectMaterialElement).perform();
		PageHelpers.waitForVisibility(materialType);
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
	
	public int getTagsCount() {
		PageHelpers.waitForSeconds(3000);
		return getDriver().findElements(addedTag).size();
	}
	
	public boolean starIsSelected() {
		getDriver().navigate().refresh();
		PageHelpers.waitForVisibility(selectedStar);
		return getDriver().findElement(selectedStar).isDisplayed();

	}
	
	public boolean starIsUnselected() {
		PageHelpers.waitForVisibility(unselectedStar);
		return getDriver().findElement(unselectedStar).isDisplayed();

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
		PageHelpers.waitForVisibility(editMaterial);
		getDriver().findElement(editMaterial).sendKeys(Keys.ENTER);
		return new EditMaterialMainPart();

	}



}
