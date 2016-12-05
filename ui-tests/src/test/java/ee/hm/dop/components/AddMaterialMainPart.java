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
		String[] titlesArray = { "ALTERNATE UNIVERSE", "ALIEN RACE OF COLORED WAX", "TRENDING PURPLE",
				"AFRICAN CONGLOMERATE", "AFRAID OF THE FUTURE", "AGE OF AQUARIUS", "AGORAPHOBIA",
				"ABANDON IN PLACE", "ABSOLUTE PANIC", "ACCORDING TO HINDU MYTH", "AKEELAH AND THE BEE", 
				"ALIENS ARE COMING", "ALL ABOUT THE OWLS", "ALL NIGHT LONG", "ALL THINGS CONSIDERED",
				"THE ALLEGORY OF THE TRILOBITE", "ALTERNATIVE ENERGY SOURCES", "ALWAYS DELICIOUS",
				"AN APPLE A DAY", "ANAGRAMS", "ANCIENT CHINESE SECRET", "ANTI STAR TREK", "APOLLO 1",
				"ARCHITECTURAL STRUCTURE", "BACK IN TIME", "BALLROOM DANCING", "BAPHOMET", "BE LIKE NEWTON",
				"BERSERKERS", "BEYOND THE MECHANICAL UNIVERSE", "BOILING WATER", "CAN-DO ATTITUDE", "CANADIAN EDEN",
				"CATALYST", "CIVIL PROTECTION", "COLD SALVATION", "COMPARE AND CONTRAST", "COSMIC RAYS",
				"COSTUMES FROM CATS", "THE CRISIS ON INFINITE EARTHS", "DAYLIGHT SAVINGS TIME", "DEEP IMPACT",
				"DEEP SPACE AMBASSADOR", "DEFINITELY YES", "DEFORMITY", "DEFYING PHYSICAL LAW", "DIAGONALIZATION",
				"DNA DOESN'T LIE", "DNA EVIDENCE", "DRAWING THE LINE", "DREAM BLENDER", "EGYPTIAN STORK",
				"ENOUGH WITH THE ZOMBIES", "ERGONOMIC KEYBOARD", "EVERY SNOWFLAKE IS DIFFERENT",
				"FAGGOT DRAGON", "FALLOUT SHELTER", "FREEDOM ISN'T FREE", "FROM THE MIND OF MINOLTA",
				"FROSTY THE SNOWMAN", "GENDER ROLES", "GENERIC NAMES", "GLOBAL GEOPOLITICS IN THE THERMONUCLEAR AGE",
				"GLOBAL WARMING", "GRAPES OF WRATH", "GREAT THINGS ABOUT BEING", "HEAVY METAL POISONING",
				"HINDU LABYRINTH", "HOLIDAY DECORATIONS", "HOME INVASION", "IDEAS WORTH", "THE IMPACT ON JUPITER",
				"IMAGINE THE POSSIBILITIES", "INSIDE MACINTOSH", "INTELLIGENT DESIGN", "JIGSAW PUZZLE", "LIKE A FOUNTAIN",
				"LILITH FAIR", "THE LION IN WINTER", "LOCKED BOX", "LOCKER ROOM", "LOGIC GATES", "LONDON KEYS",
				"LORD ALIVE", "LORD OF THE RINGS GOES COMMERCIAL", "LUNAR ECLIPSE", "LYING ABOUT AT LEAST ONE OF THESE THINGS",
				"MAGNETIC NORTH", "MAKE IT END", "MAN IN THE MOON", "MASS EFFECT CONCLUSION", "MATH IS HARD", "MAY DAY",
				"A MAZE OF TWISTY LITTLE PASSAGES", "MAZE TIME","MEDITATION RETREAT", "MENTAL HEALTH BREAK", 
				"MERCURY ORBITZ", "MILLION MAN MARCH", "MISSED HALLOWEEN", "MORE THAN YOU COULD POSSIBLY BELIEVE", 
				"MOST DISTURBING UNIVERSE", "MOUNTAIN DEW PYRAMID", "THE NASA UNICORN", "NATURE'S GOLD", 
				"NEED TO KNOW BASIS","NEPTUNE SPEAKS", "NEURODIVERSE CULTURE", "A NEW BARYON HAS BEEN DISCOVERED", 
				"NIGHT LANDING", "NOT A FIGHTER", "NUCLEAR SECRETS", "OCEAN SOUNDS", "OLD SCHOOL", "ONE OF THESE DAYS",
				"OPENING STATEMENT", "OPEN WINDOWS", "OPTICAL ILLUSION", "OUT OF THE CELLAR", "PART OF THE SOLUTION",
				"PERSONAL TIME MACHINE", "PHONE BOOTH", "PLUTO SYMBOL", "POLAR VORTEX", "POSSIBLE RADIATION HAZARD",
				"PROTESTER CHANT", "PROTESTING THE WAR", "RADIO TELESCOPE", "THE RAINDROP REVOLUTION", "READING RAINBOW",
				"RELIGIOUS TOLERANCE", "RIGHT THING, WRONG NAME", "ROOT CAUSES", "SAMURAI'S PRIDE", "SCIENTIFIC METHOD",
				"SEARCHING NEAR AND FAR", "SELF-INVOKING GODWIN'S LAW", "SEMANTIC ARGUMENTS", "SET IT FREE", 
				"SEWING MACHINE", "SKEPTICAL ACTIVIST", "SOCIAL NETWORKS", "STAIRWAY TO HEAVEN", "STAY GREEN", 
				"STILL LIFE", "STOPPING TRAFFIC", "TACTICAL INTERVENTION", "TEMPORAL PRIME DIRECTIVE",
				"TEST DRIVE", "THIRD ROCK FROM THE SUN", "THOSE BEES", "TIGER WOODS","TIME ZONES", 
				"TO THE LIONS", "TORTURE DEVICE", "TRUE FORM", "UNBELIEVABLE TOOL", "VISITING JAPAN", "WE ARE COLOR BLIND",
				"WE MUST BELIEVE IN MAGIC", "WHAT BUGS IS HOLDING", "WHAT TO DO IN THE EVENT OF A BIOLOGICAL ATTACK",
				"WILD THING", "WORDS ARE HARD", "WRATH OF THE KING",
				"WRONG DIRECTION", "YODA REMINISCES" };
		String randomTitle = titlesArray[new Random().nextInt(titlesArray.length)];
		getDriver().findElement(inserTitle).sendKeys(randomTitle);
		return this;
	}

	public AddMaterialMainPart setMaterialTitleInRussian() {
		getDriver().findElement(inserTitle).sendKeys("Катарина Тест от" + " " + PageHelpers.getDate(0, "dd/MM/yyyy"));
		return this;
	}

	public AddMaterialMainPart addDescriptionInRussian() {
		PageHelpers.waitForVisibility(descriptionField);
		PageHelpers.waitForSeconds(1500);
		getDriver().findElement(descriptionField).sendKeys(PageHelpers.getDate(0, "dd/MM/yyyy"));
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
				"c:\\Images\\ocean.jpg", "c:\\Images\\winter_sun.jpg",
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
