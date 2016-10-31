package ee.hm.dop.components;

import java.util.Random;

import org.openqa.selenium.By;
import ee.hm.dop.helpers.PageHelpers;
import ee.hm.dop.page.EditPortfolioPage;


public class AddMaterialMainPart extends PageComponent {

	private By linkField = By.id("add-material-url-input");
	private By insertPhoto = By.xpath("//span/md-icon[text()='insert_photo']");
	private By inserTitle = By.name("title");
	private By materialTypeSelection = By.id("resourceTypeSelect");
	private By applicationType = By.xpath("//md-option[text()='Rakendus']");
	private By descriptionField = By.xpath("(//div[starts-with(@id, 'taTextElement')])");
	private By nextStep = By.xpath("//button[@data-ng-click='step.nextStep()']");
	private By createMaterialButton = By.id("show-errors-button");
	private By deletedMaterialValidationError = By.xpath("//div[@class='md-input-message-animation']");
	private By uploadFile = By.xpath("//span[@data-ng-show='!material.uploadedFile']");
	private By languageSelection = By.id("add-material-language-select");
	private By existingMaterialValidationError = By.cssSelector("div.md-input-message-animation > span");
	private By russianLanguage = By.xpath("/html/body/div[5]/md-select-menu/md-content/md-option[5]");

	
	public String getValidationError() {
		PageHelpers.waitForVisibility(deletedMaterialValidationError);
		return getDriver().findElement(deletedMaterialValidationError).getText();
	}
	
	
	public AddMaterialMainPart clickToSelectMaterialType() {
		getDriver().findElement(materialTypeSelection).click();
		getDriver().findElement(applicationType).click();
		return this;

	}

	public AddMaterialMainPart clickToSelectMaterialLanguage() {
		getDriver().findElement(languageSelection).click();
		getDriver().findElement(russianLanguage).click();
		return this;

	}

	public MaterialTaxonomyPart clickNextStep() {
		PageHelpers.waitForVisibility(nextStep);
		getDriver().findElement(nextStep).click();
		return new MaterialTaxonomyPart();

	}

	public EditPortfolioPage clickAddMaterialButton() {
		getDriver().findElement(createMaterialButton).click();
		PageHelpers.waitForSeconds(4500);
		return new EditPortfolioPage();

	}

	public boolean isLinkDisplayed() {
		return getDriver().findElement(linkField).isDisplayed();

	}

	public AddMaterialMainPart setHyperLink() {
		getDriver().findElement(linkField).sendKeys("http://a" + PageHelpers.generateUrl(30));
		return this;
	}
	
	public AddMaterialMainPart insertDeletedMaterialUrl() {
		getDriver().findElement(linkField).sendKeys("https://www.gfycat.com/gifs/search/pug/detail/RingedWarpedCrayfish");
		return this;
	}
	
	public AddMaterialMainPart insertExistingMaterialUrl() {
		getDriver().findElement(linkField).sendKeys("https://test.ee");
		return this;
	}
	
	public String getExistingMaterialValidationError() {
		PageHelpers.waitForVisibility(existingMaterialValidationError);
		return getDriver().findElement(existingMaterialValidationError).getText();

	}

	public AddMaterialMainPart checkHyperLink() {
		PageHelpers.waitForVisibility(linkField);
		getDriver().findElement(linkField).sendKeys("");
		return this;
	}


	public AddMaterialMainPart setMaterialTitle() {
		String[] titlesArray = { "We Were Meant To Collide", "Strange drawnings", "20th Century Drama",
				"The Different Types Of People There Are On The Internet",
				"This Is Why Having More Money Just Brings New Problems",
				"For Every Student Struggling To Balance School And Work, This Is How You Do It",
				"Strong Is Beautiful But So Is Weakness", "How To Drink At Home By Yourself", "Anime",
				"How Many Cats Is Too Many Cats", "Counting Scars", "No One Said It Was Easy",
				"Helpful Tips On Productivity That Will End Your Procrastination",
				"What Does the Future of the Internet Look Like?", "The Laws of Gold Destroyed!",
				"Getting To Know The Monsters That Live Inside Us",
				"What It's Like To Actually To Be From The Jersey Shore", "How to Live in New York City ",
				"Notes From Subway", "Don't Wake Up Alone On A Saturday Morning", "Water has also been Weaponized",
				"As the Arctic roasts, Alaska bakes in one of its warmest winters ever", "Illuminati Card Game",
				"10 Everyday Things Only Extroverted Introverts Will Understand",
				"Surreal crooked trees shaped by Antarctic winds",
				"How To Ruin Your Life (Without Even Noticing That You Are)", "Dust Older Than The Solar System",
				"The Long-Ago Shamans of Death Valley: Vision Quests and Magical Rites", "Moderate earthquake",
				"Estonian schools piloting open source software",
				"Study reveals surprising facts about diets of Medieval children in England",
				"Urban Camouflage: Can You Hide from Technology and the System?", "Get started on a personal project",
				"Environmental Racism Is Not Just A Flint Problem",
				"What Is a Subway Metro Planner and How Can It Help You?",
				"This Is Why You Can�t Get Over Your Breakup According To Science", "Swans on the lake",
				"Coconut Oil Is Better Than Any Toothpaste", "You Need To Stop Waiting For Tomorrow",
				"If You Create, You Are An Artist", "When Travel Is A Part Of You",
				"You don’t have to do what everyone else is doing",
				"How Do We Protect Communications Networks From Cyber Attacks?", "How to laugh",
				"Instant Gratification: How It's Ruining Our Lives And How We Can Stop It",
				"No age will give you the answers", "This Is The Reward Of Traveling Solo",
				"To Enjoy The Rainbow, You Must First Enjoy The Rain",
				"Just A Few Aliens Trying To Live The American Dream",
				"This Is Why It’s Important To Consider Your Western Footprint",
				"The Truth About People Who Are Always Running Away", "This Is Why Nature Is The Best Form Of Therapy",
				"Black Women Are More Than Their Strength", "Rethinking Environmentalism ",
				"This Is Why Deleting Facebook Is The Best Decision You'll Ever Make", "Is Hong Kong still safe?",
				"Why Everyone Should Be Part Of A Third Culture", "Does Capitalism Make Us Unhappy?",
				"The Truth About Insomnia", "Why Baseball Is Interesting", "Light transcends time.",
				"How to Start a Post-Rock Band", "Who're You Calling A Nihilist?", "Quidditch: In Real Life",
				"Forever Lazy: Unfashionable Fashion Strikes Again", "Why Egypt Isn�t The Next Tunisia",
				"Why The Internet Chose Cats", "How to be Jewish", "When Faith in Graffiti Reigned",
				"The Art of Flight", "Modern Logic Puzzles", "The Planet Nibiru, the Truths and the Half-Truths",
				"Yellowstone Strange Light", "Organize your bookshelves by color",
				"What was the bravest thing you have ever done?",
				"This Is Why You Should Start Drinking Ginger Tea And How To Make It The Right Way",
				"How many times in our life do the seconds matter?", "The Realm of Buried Giants",
				"Mysterious Cosmic Radio Bursts Found To Repeat", "Carnival of Space #447",
				"Extraterrestrial Contact: Fears", "This cafe has taken the internet by storm.",
				"Decoding Digital Hieroglyphics: The Meaning of Emojis in Evidence" };
		String randomTitle = titlesArray[new Random().nextInt(titlesArray.length)];
		getDriver().findElement(inserTitle).sendKeys(randomTitle);
		return this;
	}

	public AddMaterialMainPart setMaterialTitleInRussian() {
		getDriver().findElement(inserTitle).sendKeys("НЛО вчера и сегодня");
		return this;
	}

	public AddMaterialMainPart addDescriptionInRussian() {
		PageHelpers.waitForVisibility(descriptionField);
		PageHelpers.waitForSeconds(1500);
		getDriver().findElement(descriptionField).sendKeys("Кириллица" + PageHelpers.getDate(0, "dd/MM/yyyy"));
		return this;
	}

	public AddMaterialMainPart addDescription() {
		String[] descriptionArray = { "The road to feminism is simple: it’s about awareness.",
				"You know, fear is a funny thing on an investigation. There seems to be a difference between imagination running away from you, what you call “true fear”, and what I think could be the change in the environment from a spirit presence.",
				"Her stories touch a raw nerve every woman will resonate with—about how hard it is for us to be taken seriously.",
				"You learn to be inspired by challenges, not afraid of them.", "Take a ghost tour. Instead of walking through a haunted house, visit a location that’s actually haunted.",
				"When you get an adrenaline rush from cleaning your apartment.", "There will always be an ending to a nice movie, to a good book, to a wonderful story.",
				"The greatest gift is getting to be yourself day in and day out.", "More information within.",
				"What matters now is who you are in this moment and what you’re doing right now.",
				"Whenever I follow this rule, my life gets exponentially better very quickly.",
				"Remind yourself that Churchill lead a country while battling depression.", "It’s not that they dislike people. They just don’t like to be surrounded all the time.",
				"A record number of murders have occurred in Anchorage this year, with over half of them unsolved.",
				"When all else fails, just breathe. You are surviving. And sometimes, that is all you can do. And that’s ok.",
				"In a Romeo and Juliet-esque situation, there was some kind of apparent feud between the family of the bride and groom.",
				"When you have a lot to juggle, the most valuable thing in the world is energy.", "The world needs more heroes that we can root for, not villains we can tolerate.",
				"Dreams are an aspiration of who we truly are. ", "Most people aren’t willing to feel difficult emotions on a regular basis.",
				"It’s the least I can do. Keep the conversation going.", "Is today Monday? It is.",
				"Perfection, the ultimate goal. We all want it. We chase it. And we’re disappointed when we don’t get there. ",
				"This is not a political post. Or a revolutionary one. Or a socialist one. Or a whatever. It’s just facts. ",
				"I am Bipolar, and I am capable – but this does not mean I want to be Bipolar. My disorder isn’t a means to an end, a method of manipulation, or a way to demand the attention of those around me.",
				"School starts tomorrow, which means it’s time to say goodbye", "Mental illness doesn’t need to be worn like this season’s jeans.",
				"The quality of your rest. This is the main determinant of your long-term productivity and efficiency of your work. Good rest is a concrete foundation and prerequisite for exceptional performance.",
				"Here’s an unpopular truth: people who post less are actually those who lead amazing lives. And guess what? They don’t need to prove it.",
				"Entry Level means years of experience. When you read the title “entry level” for a position, you automatically assume that this may be an option for you.",
				"Music note: You know how to play at least one instrument, and you always have your earbuds in.",
				"Nothing is worth it if you aren’t happy", "Then you look back, maybe months, maybe years later and you see how much was happening, how you just didn’t notice it.",
				"I woke up to the sound of knocking on glass. I assumed it was the window, but as I checked, I heard it again, from the mirror.",
				"I think you should stop worrying about reasons why it won’t work and only focus on the reasons why it will.",
				"New iPhones. Each new model is basically the same as the previous one", "Halloween is one of the most fun days of the year and with the ease of ordering costume essentials on eBay there’s no excuse not to get into the holiday spirit.",
				"Delete your time-wasting apps. The ones you always get stuck playing when you should be working.",
				"New York City isn’t a magical place where you instantaneously find yourself.",
				"Class of 2020, no matter where you are, stay focused and enjoy every moment. This isn’t just a phase. It’s your future, your dreams and your life.",
				"How can you be sure that your red isn’t someone else’s blue?", "There are ancient things living in the woods, evil things. They are angry that we have forgotten them and we do not worship them anymore, that we do not give them sacrifices anymore",
				"There are plenty of fish in the sea, and as long as you keep focusing on you and your own growth, the right ones will come to you.",
				"It’s okay to be scared. Being scared means we’re concerned enough to realize that there might be something wrong, that there might be something worth fixing.",
				"Most people are not strategic. They are reactive." };
		String randomDescription = descriptionArray[new Random().nextInt(descriptionArray.length)];
		getDriver().findElement(descriptionField).sendKeys(randomDescription);
		return this;
	}

	public AddMaterialMainPart uploadPhoto() {
		PageHelpers.waitForVisibility(insertPhoto);
		getDriver().findElement(insertPhoto).click();
		String[] picsArray = { "c:\\Images\\helmet.jpg", "c:\\Images\\hobbit.jpg", "c:\\Images\\lill.jpg",
				"c:\\Images\\ballet.jpg", "c:\\Images\\coast.jpg", "c:\\Images\\cat1.jpg", "c:\\Images\\coffee.jpg",
				"c:\\Images\\berry.jpg", "c:\\Images\\art.jpg", "c:\\Images\\girl.jpg", "c:\\Images\\autumn.jpg",
				"c:\\Images\\bluberries.jpg", "c:\\Images\\sunset.jpg", "c:\\Images\\flowers.jpg",
				"c:\\Images\\macro.jpg", "c:\\Images\\caravagio.jpg", "c:\\Images\\win.jpg", "c:\\Images\\fyodor.jpg",
				"c:\\Images\\planet.jpg", "c:\\Images\\pictorial.jpg", "c:\\Images\\winter.jpg", "c:\\Images\\two.jpg",
				"c:\\Images\\mask.jpg", "c:\\Images\\legolas.jpg", "c:\\Images\\mushroom.jpg", "c:\\Images\\anime.jpg",
				"c:\\Images\\pug1.jpg", "c:\\Images\\dali.jpg", "c:\\Images\\cats.jpg", "c:\\Images\\zombie.jpg",
				"c:\\Images\\sun.jpg", "c:\\Images\\stars.jpg", "c:\\Images\\cup.jpg", "c:\\Images\\fog.jpg",
				"c:\\Images\\dragons.jpg", "c:\\Images\\jump.jpg", "c:\\Images\\beard.jpg", "c:\\Images\\yellow.jpg",
				"c:\\Images\\dragons.jpg", "c:\\Images\\land.jpg", "c:\\Images\\legend.jpg", "c:\\Images\\pug.jpg",
				"c:\\Images\\radars.jpg", "c:\\Images\\roses.jpg", "c:\\Images\\sand.jpg", "c:\\Images\\sea.jpg",
				"c:\\Images\\skyscrapers.jpg", "c:\\Images\\snowflake.jpg", "c:\\Images\\space.jpg",
				"c:\\Images\\umbrella.jpg", "c:\\Images\\woo.jpg", "c:\\Images\\evening.jpg", "c:\\Images\\aika.jpg",
				"c:\\Images\\solar.jpg", "c:\\Images\\foliage.jpg", "c:\\Images\\london.jpg", "c:\\Images\\dress.jpg",
				"c:\\Images\\cup1.jpg", "c:\\Images\\rail.jpg", "c:\\Images\\fields.jpg", "c:\\Images\\giacint.jpg",
				"c:\\Images\\sport.jpg", "c:\\Images\\fish.jpg", "c:\\Images\\palma.jpg", "c:\\Images\\watch.jpg",
				"c:\\Images\\legs.jpg", "c:\\Images\\lens.jpg", "c:\\Images\\dog.jpg", "c:\\Images\\italy.jpg",
				"c:\\Images\\art1.jpg", "c:\\Images\\rembrandt.jpg", "c:\\Images\\flamenco.jpg",
				"c:\\Images\\beatles.jpg", "c:\\Images\\closeup.jpg", "c:\\Images\\ninja.jpg", "c:\\Images\\lana.jpg",
				"c:\\Images\\jeans.jpg", "c:\\Images\\field.jpg", "c:\\Images\\cateyes.jpg",
				"c:\\Images\\ranuculus.jpg", "c:\\Images\\phones.jpg", "c:\\Images\\dog14.jpg", "c:\\Images\\mist.jpg",
				"c:\\Images\\spices.jpg", "c:\\Images\\asfalt.jpg", "c:\\Images\\battle.jpg", "c:\\Images\\clocks.jpg",
				"c:\\Images\\jerusalem.jpg", "c:\\Images\\pencil.jpg", "c:\\Images\\laptop.jpg",
				"c:\\Images\\hamburg.jpg", "c:\\Images\\grass.jpg", "c:\\Images\\industrial.jpg",
				"c:\\Images\\monochrome.jpg", "c:\\Images\\winter1.jpg", "c:\\Images\\art.jpg", "c:\\Images\\urban.jpg",
				"c:\\Images\\moon.jpg", "c:\\Images\\spring.jpg", "c:\\Images\\piers.jpg", "c:\\Images\\mirror.jpg",
				"c:\\Images\\jar.jpg", "c:\\Images\\cats.jpg", "c:\\Images\\dance.jpg", "c:\\Images\\phones1.jpg",
				"c:\\Images\\ocean.jpg", "c:\\Images\\winter_sun.jpg", "c:\\Images\\cityscape.jpg",
				"c:\\Images\\sheep.jpg", "c:\\Images\\snow_lake.jpg", "c:\\Images\\liberty.jpg" };
		String randomFile = picsArray[new Random().nextInt(picsArray.length)];
		PageHelpers.uploadFile(randomFile);
		PageHelpers.waitForSeconds(1500);
		return this;
	}

	public AddMaterialMainPart uploadFile() {
		PageHelpers.waitForVisibility(uploadFile);
		getDriver().findElement(uploadFile).click();
		PageHelpers.uploadFile1("c:\\files\\test2.docx");
		PageHelpers.waitForSeconds(1500);
		return this;
	}

}
