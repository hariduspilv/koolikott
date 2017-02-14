package ee.hm.dop.page;

import java.util.Random;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import ee.hm.dop.components.AddMaterialMainPart;
import ee.hm.dop.helpers.PageHelpers;

public class EditPortfolioPage extends Page {

	private By visibilityButton = By.xpath("//button[@ng-click='$mdOpenMenu($event)']");
	private By shareWithLink = By.xpath("//button/span[text()='Jaga ainult lingiga']");
	private By newChapter = By.xpath("//button[@data-ng-click='addNewChapter()']");
	private By chapterTitle = By.xpath("//input[@placeholder='Sisesta peatüki pealkiri']");
	private By urlLink = By.xpath("//input[@data-ng-model='chapter.resourcePermalink']");
	private By exitAndSave = By.xpath("//button[@data-ng-click='saveAndExitPortfolio()']");
	private By materialMessage = By.cssSelector("div.md-toast-content");
	private By arrowRight = By.cssSelector("#chapter-0 .chapter-arrow md-icon");
	private By addFileButton = By.xpath("//button[@data-ng-click='addMaterialFromFile()']");
	private By actionsMenu = By.id("material-menu");
	private By boldButton = By.name("bold");
	private By italicButton = By.name("italics");
	private By ulButton = By.name("ul");
	private By olButton = By.name("ol");
	private By preButton = By.name("pre");
	private By quoteButton = By.name("quote");
	private By insertLink = By.name("insertLink");
	private By updateMaterial = By.id("add-portfolio-edit-button");
	private By descriptionField = By.xpath("(//div[starts-with(@id, 'taTextElement')])");
	private By addNewMaterialButton = By.xpath("//button[@data-ng-click='openMenu($mdOpenMenu, $event)']");
	private By newMaterialSelection = By.xpath("//button/span[text()='Uus']");
	private By successAlertText = By.cssSelector("p.md-toolbar-text");


	public EditPortfolioPage clickToSelectDescription() {
		PageHelpers.waitForVisibility(descriptionField);
		getDriver().findElement(descriptionField).sendKeys(Keys.CONTROL + "a");
		PageHelpers.waitForSeconds(1500);
		return this;

	}

	public AddMaterialMainPart clickAddMaterial() {
		PageHelpers.waitForVisibility(addNewMaterialButton);
		getDriver().findElement(addNewMaterialButton).click();
		PageHelpers.waitForVisibility(newMaterialSelection);
		getDriver().findElement(newMaterialSelection).click();
		return new AddMaterialMainPart();

	}

	public EditPortfolioPage clickToSelectBold() {
		PageHelpers.waitForVisibility(boldButton);
		getDriver().findElement(boldButton).click();
		return this;

	}

	public EditPortfolioPage clickToRefreshMaterial() {
		getDriver().findElement(updateMaterial).click();
		return new EditPortfolioPage();

	}

	public EditPortfolioPage clickToSelectItalic() {
		getDriver().findElement(italicButton).click();
		return this;

	}

	public EditPortfolioPage clickToInsertLink() {
		getDriver().findElement(insertLink).click();
		PageHelpers.uploadFile1("https://test.ee");
		return this;

	}

	public EditPortfolioPage clickToSelectUl() {
		getDriver().findElement(ulButton).click();
		return this;

	}

	public EditPortfolioPage clickToSelectQuote() {
		getDriver().findElement(quoteButton).click();
		return this;

	}

	public EditPortfolioPage clickToSelectPre() {
		getDriver().findElement(preButton).click();
		return this;

	}

	public EditPortfolioPage clickToSelectOl() {
		getDriver().findElement(olButton).click();
		return this;

	}

	public boolean materialMessageIsDisplayedInPortfolio() {
		PageHelpers.waitForSeconds(1500);
		return getDriver().findElement(materialMessage).isDisplayed();

	}

	public EditPortfolioPage clickActionsMenu() {
		getDriver().findElement(actionsMenu).click();
		return this;
	}

	public String getSuccessAlertText() {
		return getDriver().findElement(successAlertText).getText();

	}

	public EditPortfolioPage addDescription() {
		String[] descriptionArray = { "The road to feminism is simple: it’s about awareness.",
				"You know, fear is a funny thing on an investigation. There seems to be a difference between imagination running away from you, what you call “true fear”, and what I think could be the change in the environment from a spirit presence.",
				"Her stories touch a raw nerve every woman will resonate with—about how hard it is for us to be taken seriously.",
				"You learn to be inspired by challenges, not afraid of them.",
				"When you get an adrenaline rush from cleaning your apartment.",
				"The greatest gift is getting to be yourself day in and day out.", "More information within.",
				"What matters now is who you are in this moment and what you’re doing right now.",
				"Whenever I follow this rule, my life gets exponentially better very quickly.",
				"Remind yourself that Churchill lead a country while battling depression.",
				"A record number of murders have occurred in Anchorage this year, with over half of them unsolved.",
				"When all else fails, just breathe. You are surviving. And sometimes, that is all you can do. And that’s ok.",
				"In a Romeo and Juliet-esque situation, there was some kind of apparent feud between the family of the bride and groom.",
				"When you have a lot to juggle, the most valuable thing in the world is energy.",
				"Dreams are an aspiration of who we truly are. ",
				"It’s the least I can do. Keep the conversation going.",
				"Perfection, the ultimate goal. We all want it. We chase it. And we’re disappointed when we don’t get there. ",
				"This is not a political post. Or a revolutionary one. Or a socialist one. Or a whatever. It’s just facts. ",
				"I am Bipolar, and I am capable – but this does not mean I want to be Bipolar. My disorder isn’t a means to an end, a method of manipulation, or a way to demand the attention of those around me.",
				"School starts tomorrow, which means it’s time to say goodbye",
				"The quality of your rest. This is the main determinant of your long-term productivity and efficiency of your work. Good rest is a concrete foundation and prerequisite for exceptional performance.",
				"Here’s an unpopular truth: people who post less are actually those who lead amazing lives. And guess what? They don’t need to prove it.",
				"Entry Level means years of experience. When you read the title “entry level” for a position, you automatically assume that this may be an option for you.",
				"Music note: You know how to play at least one instrument, and you always have your earbuds in.",
				"Nothing is worth it if you aren’t happy",
				"I woke up to the sound of knocking on glass. I assumed it was the window, but as I checked, I heard it again, from the mirror.",
				"I think you should stop worrying about reasons why it won’t work and only focus on the reasons why it will.",
				"New iPhones. Each new model is basically the same as the previous one",
				"Delete your time-wasting apps. The ones you always get stuck playing when you should be working.",
				"New York City isn’t a magical place where you instantaneously find yourself.",
				"Class of 2020, no matter where you are, stay focused and enjoy every moment. This isn’t just a phase. It’s your future, your dreams and your life.",
				"How can you be sure that your red isn’t someone else’s blue?",
				"There are plenty of fish in the sea, and as long as you keep focusing on you and your own growth, the right ones will come to you.",
				"It’s okay to be scared. Being scared means we’re concerned enough to realize that there might be something wrong, that there might be something worth fixing.",
				"Most people are not strategic. They are reactive." };
		String randomDescription = descriptionArray[new Random().nextInt(descriptionArray.length)];
		getDriver().findElement(descriptionField).clear();
		getDriver().findElement(descriptionField).sendKeys(randomDescription);
		PageHelpers.waitForSeconds(1500);
		return this;
	}

	

	public PortfolioViewPage clickExitAndSave() {	
		getDriver().findElement(exitAndSave).click();
		PageHelpers.waitForSeconds(1500);
		return new PortfolioViewPage();

	}	

	public EditPortfolioPage setChapterTitle() {
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
		if (!getDriver().findElement(addFileButton).isDisplayed()) {
			getDriver().findElement(arrowRight).click();
		}
		return this;
	}


	public EditPortfolioPage clickVisibilityButton() {
		PageHelpers.waitForVisibility(visibilityButton);
		getDriver().findElement(visibilityButton).click();
		PageHelpers.waitForSeconds(1500);
		return this;

	}

	public EditPortfolioPage selectShareWithLink() {
		getDriver().findElement(shareWithLink).click();
		PageHelpers.waitForSeconds(1500);
		return this;

	}

	public EditPortfolioPage addNewChapter() {
		PageHelpers.waitForVisibility(newChapter);
		getDriver().findElement(newChapter).click();
		PageHelpers.waitForSeconds(3500);
		return this;

	}

	public EditPortfolioPage setUrlLink(String url) {
		getDriver().findElement(urlLink).sendKeys(url);
		return this;

	}

}
