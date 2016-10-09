package ee.hm.dop.page;

import java.util.Random;

import org.openqa.selenium.By;
import ee.hm.dop.components.AddMaterialMainPart;
import ee.hm.dop.helpers.PageHelpers;

public class EditPortfolioPage extends Page {
	
	private By addedTag = By.xpath("//a[@data-ng-click='getTagSearchURL($event, $chip.tag)']");
	private By visibilityButton = By.xpath("//button[@ng-click='$mdOpenMenu($event)']");
	private By makePublik = By.xpath("//button/span[text()='Tee avalikuks']");
	private By newChapter = By.xpath("//button[@data-ng-click='addNewChapter()']");
	private By chapterTitle = By.xpath("//input[@placeholder='Sisesta peatüki pealkiri']");
	private By urlLink = By.xpath("//input[@data-ng-model='chapter.resourcePermalink']");
	private By addMaterial = By.xpath("//button[text()='Lisa materjal']");
	private By exitAndSave = By.xpath("//button[@data-ng-click='saveAndExitPortfolio()']");
	private By materialMessage = By.cssSelector("div.md-toast-content");
	private By arrowRight = By.cssSelector("#chapter-0 .chapter-arrow md-icon");
	private By addFileButton = By.xpath("//button[@data-ng-click='addMaterialFromFile()']");

	
	
	public boolean materialMessageIsDisplayedInPortfolio() {
		PageHelpers.waitForSeconds(1500);
		return getDriver().findElement(materialMessage).isDisplayed();

	}
	
	
	public String getTagText() {
		return getDriver().findElement(addedTag).getText();
		
	}
	
	public PortfolioPage clickExitAndSave() {
		getDriver().findElement(exitAndSave).click();
		PageHelpers.waitForSeconds(1500);
		return new PortfolioPage();

	}

	
	public EditPortfolioPage  setChapterTitle() {
		String[] titlesArray = { "Language Arts", "Mathematics", "Science", "Health", "Handwriting",
				"Personal Organization", "Physical Education (P.E.)", "Social Skills", "Career Planning",
				"Instrumental Music � specific instrument", "Movement or Eurythmy", "Handwork or handcrafts",
				"Life Lab or gardening", "Dramatics", "Art", "Greek and Roman History", "Martial Arts", "Ice Skating",
				"Music", "Dance", "Personal Finance and Investing", "Aerobics", "Yoga", "Figure skating",
				"Spanish or other foreign language", "Leadership", "Special Education Day Class", "Driver�s Education",
				"Resource Program", "Statistics", "World Religions", "Cycling", "Bowling", "Keyboarding",
				"Computer Repair", "Speech", "Adaptive P.E.", "Trigonometry", "Economics", "Physical Fitness",
				"Computer Graphics", "Occupational Therapy", "Astronomy", "Political Science", "Digital Arts",
				"Photoshop", "Programming", "Reading", "Meteorology", "Equine Science", "Gymnastics",
				"Outdoor Survival Skills", "Speech and Debate", "Animal Science", "Ecology", "Golf", "Rock Climbing",
				"Desktop Publishing", "English", "Oceanography", "Veterinary Science", "Environmental Science",
				"Web Design", "Basic Math", "Botany", "Geology", "Forensic Science", "Critical Thinking", "Rhetoric",
				"Pre-algebra", "Physics", "Chemistry", "Organic Chemistry", "Biology", "Zoology", "Basic Yard Care",
				"Financial Management", "Consumer Math", "Small Engine Mechanics", "Auto Mechanics", "Marine Biology",
				"Algebra", "Geometry", "Gardening", "Japanese", "German", "Latin", "Culinary Arts", "Life Science",
				"Earth Science", "Food Science", "Anthropology", "Home Management", "Physical Science",
				"Computer Aided Design {Digital Media}", "Photography", "Home Organization", "Social Studies",
				"Performing Arts", "Theatre Arts", "Genealogy", "Geography", "Ancient Civilizations", "Leather Working",
				"Philosophy", "Logic", "Medieval and Renaissance", "Sports", "Sculpture", "Ceramics", "Pottery",
				"Music History", "Music Theory", "Music Fundamentals", "U.S. History and Government",
				"French / Spanish / Latin", "Photojournalism", "Yearbook", "Art History", "Drawing", "Painting",
				"Computer Science or Lab", "Home Economics", "Instrumental Music", "Band", "Choir", "Drama",
				"Physical Education", "Woodshop", "Metal Shop", "Business Technology", "English as second language",
				"World Literature", "Ancient Literature", "Medieval Literature", "Renaissance Literature",
				"Modern Literature", "British Literature", "American Literature", "Composition", "Creative Writing",
				"Poetry", "Grammar", "Vocabulary", "Debate", "Journalism", "Publishing Skills" };
		String randomTitle = titlesArray[new Random().nextInt(titlesArray.length)];
		PageHelpers.waitForVisibility(chapterTitle);
		getDriver().findElement(chapterTitle).clear();
		getDriver().findElement(chapterTitle).sendKeys(randomTitle);
		return this;
	}
	
	
	public EditPortfolioPage checkArrow() {
		if (!getDriver().findElement(addFileButton).isDisplayed()){
			getDriver().findElement(arrowRight).click();
		}
		return this;
	}


	public EditPortfolioPage setUrlLink() {
		getDriver().findElement(urlLink).clear();
		getDriver().findElement(urlLink).sendKeys("http://a" + PageHelpers.generateUrl(30));
		PageHelpers.getScreenshot();
		return this;
	}

	
	public String isPortfolioChangedToPublic() {
		return getDriver().findElement(visibilityButton).getText();
	}
	
	public EditPortfolioPage clickVisibilityButton() {
		PageHelpers.waitForVisibility(visibilityButton);
		getDriver().findElement(visibilityButton).click();
		PageHelpers.waitForSeconds(1500);
		return this;

	}
	
	public EditPortfolioPage selectMakePublic() {
		getDriver().findElement(makePublik).click();
		PageHelpers.waitForSeconds(1500);
		return this;

	}
	
	public EditPortfolioPage addNewChapter() {
		PageHelpers.waitForVisibility(newChapter);
		getDriver().findElement(newChapter).click();
		PageHelpers.waitForSeconds(3500);
		return this;

	}
	
	public AddMaterialMainPart clickAddMaterial() {
		getDriver().findElement(addMaterial).click();
		return new AddMaterialMainPart();

	}
	
	public EditPortfolioPage setUrlLink(String url) {
		getDriver().findElement(urlLink).sendKeys(url);
		return this;

	}

}
