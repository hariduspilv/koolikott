package ee.hm.dop.components;

import java.util.Random;

import org.openqa.selenium.By;

import ee.hm.dop.helpers.PageHelpers;
import ee.hm.dop.page.EditPortfolioPage;



public class AdditionalDataPart extends PageComponent {
	
	private By createMaterialButton = By.id("add-material-create-button");
	private By authorsName = By.id("material-author-0-name");
	private By authorsSurname= By.xpath("//input[@data-ng-model='author.surname']");
	private By publishersName = By.xpath("//input[@data-ng-model='material.publishers[0].name']");
	
	public AdditionalDataPart insertAuthorsName() {
		String[] namesArray = { "Amina", "Alfred", "Alma", "Almond", "Alvin", "Amanda", "Anastasia", "Ash", "Ashley",
				"Audrey", "Bailey", "Ballard", "Barbara", "Barclay", "Bartley", "Beatrice", "Beverly", "Aisha", "Akela",
				"Blossom", "Boston", "Bradley", "Brandon", "Byron", "Camellia", "Camilla", "Cannon", "Cassandra",
				"Abbigail", "Adelle", "Agatha", "Aliah", "Alison", "Alix", "Anna", "Belynda", "Djamila",
				"Beth", "Bridget", "Briony", "Brianna", "Cara", "Cedar'", "Charis", "Charleen", "Chloe", "Chris",
				"Cilla", "Clara", "Cornelia", "Cressida", "Dakota", "Danielle", "Daphne", "Darian", "Deborah",
				"Denice", "Gamila", "Diana", "Dina", "Donna", "Dorean", "Ebony", "Eda", "Eden", "Edith",
				"Eireen", "Eleanor", "Hala", "Elba", "Hana", "Elfrida", "Ella", "Eliza", "Elly", "Elmira", "Eloise",
				"Elvina", "Emelie", "Emerald", "Emmy", "Erica", "Erin", "Eve", "Fay", "Ismat", "Flor", "Frea",
				"Jamila", "Gabi", "Gabriella", "Gaila", "Geena", "Gemma", "Gwendolen", "Harper", "Haven",
				"Hazel", "Kismat", "Laila", "Helga", "Lamia", "Latifa", "Ida", "Layla", "Leena", "Lina",
				"Idelle", "Ilean", "India", "Malika", "Irma", "Irmagard", "Irene", "Iris", "Irma", "Isabel",
				"Isadora", "Isolde", "Iva", "Ivy", "Jade", "Jane", "Jasmin", "Jemma", "Jennifer", "Jinny",
				"Nora", "Julia", "June", "Karly", "Kelly", "Kenzie", "Keisha", "Kiara", "Kim", "Ranya", "Kimberly",
				"Klara", "Ladonna", "Lacy", "Lana", "Lara", "Laura", "Laureen", "Laura", "Lea", "Leanna", "Lee",
				"Lenora", "Leona", "Lilian", "Lilith", "Linda", "Sameera", "Lindsay", "Lora", "Loreen", "Sana",
				"Lyda", "Mabella", "Lyssa", "Madalyn", "Maddie", "Madge", "Shakira", "Maddison", "Mae",
				"Marcie", "Marian", "Tahira", "Mariel", "Marta", "Thalulah", "Melicent", "Thamina", "Meredith",
				"Milla", "Mira", "Miriam", "Mona", "Mya", "Nadia", "Natalie", "Nelle", "Nicole", "Yasmin",
				"Nina", "Noah", "Olive", "Ophelia", "Paula", "Retha", "Rhonda", "Zara", "Riona", "Rosa",
				"Rosheen", "Roxane", "Amal", "Amber", "Rubie", "Sabina", "Elham", "Sabrina", "Salome", "Iesha",
				"Samantha", "Ishtar", "Saranna", "Lila", "Scarlet", "Selina", "Shania", "Shannon", "Oma", "Sheree", "Sofia",
				"Rihanna", "Saffron", "Stepfanie", "Sybilla", "Tara", "Thea", "Tiana", "Tiara", "Trish", "Uma",
				"Valarie", "Vanessa", "Vianne", "Virginia", "Yolanda", "Zoe", "Aaron", "Adam", "Aleena", "Alika", "Alma",
				"Alvina", "Adrian", "Alban", "Alen", "Amelia", "Alex", "Alfie", "Allan", "Allister", "Aston",
				"Barret", "Bart", "Bennet", "Bradley", "Brennan", "Brian", "Anousha", "Brooke", "Bruno", "Ariana",
				"Aribah", "Cedric", "Chad", "Chandler", "Clifton", "Arya", "Colin", "Dallas", "Dan", "Dalton",
				"Daniel", "Dave", "David", "Dean", "Deon", "Earl", "Eamon", "Eber", "Edwin", "Elbie",
				"Elden", "Eliah", "Eliot", "Chandi", "Elvin", "Emil", "Emmet", "Esmond",
				"Evan", "Fabian", "Fred", "Gabriel", "Gail", "Gareth", "George", "Gerry", "Eliza",
				"Gilbert", "Glen", "Gordon", "Ermina", "Graham", "Griffin", "Hank", "Hardy", "Harry", "Harvey",
				"Hartley", "Haward", "Hecktor", "Herman", "Holden", "Humbert", "Imanuel", "Ingram", "Isador",
				"Jack", "James", "Jared", "Jason", "Jeff", "Joel", "John", "Joseph", "Julian", "Kay",
				"Keith", "Kendall", "Kenneth", "Kent", "Kevin", "Kian", "Kimberley", "Lambart", "Lanford",
				"Lark", "Larry", "Lenard", "Lennie", "Leon", "Leroi", "Lester", "Lewis", "Lionel",
				"Lucas", "Malone", "Mark", "Marlon", "Marvin", "Mason", "Matthias", "Max", "Mayson",
				"Melvin", "Merlin", "Milton", "Monty", "Morrissey", "Murphy", "Nash", "Ned", "Neil", "Neo",
				"Nevil", "Nick", "Noel", "Nolan", "Norman", "Norton", "Oberon", "Oliver", "Patrick", "Paul",
				"Percy", "Peter", "Maysa", "Phil", "Ralph", "Randy", "Raymond", "Redd", "Mishel", "Richard",
				"Mona", "Rocky", "Rolland", "Roman", "Ronald", "Ross", "Nadia", "Ryan", "Samuel", "Sander",
				"Sean", "Simon", "Nargis", "Stan", "Stephen", "Steve", "Taylor", "Thomas", "Tim",
				"Tommie", "Travis", "Troy", "Tyron", "Vergil", "Victor", "Vincent", "Walter", "William",
				"Wilmot",  "Sonia"};
		String randomName = namesArray[new Random().nextInt(namesArray.length)];
		getDriver().findElement(authorsName).clear();
		getDriver().findElement(authorsName).sendKeys(randomName);
		return this;
	}
	
	public AdditionalDataPart insertAuthorsSurname() {
		String[] namesArray = { "Smith", "Hall", "Stewart", "Johnson", "Allen", "Bennett", "Morris", "Wood", "Brown",
				"King", "Ross", "Davis", "Miller", "Morgan", "Coleman", "Hill", "Bell", "Moore", "Murphy",
				"Taylor", "Green", "Bailey", "Powell", "Adams", "Long", "River", "Baker", "White",
				"Nelson", "Carter", "Howard", "Martin", "Mitchell", "Simmons", "Gray", "Foster", "Lewis",
				"Collins", "Hayes", "Kelly", "Evans", "Ryan", "Graham", "Gibbs", "Hoffman", "Shelton", "Griffin",
				"York", "Berry", "Blair", "Lyon", "Russo", "Finch", "Maxwell", "Fanning", "Clifford", "Black",
				"Underwood", "Pearson", "Craig", "Lane", "Norris", "Donnelly", "May", "Finn", "Dalton", "Garrett",
				"Newman", "Chandler", "Melton", "Baxter", "Hill", "Carter", "Leblanc", "Larsen", "Snow", "Lloyd",
				"Day", "Lamont", "Flores", "Zion", "Woody", "Wolf", "Winston", "Wilton", "Topher", "Trafford",
				"Vortigern", "Walker", "Wayne", "Weston", "Shannon", "Shelby", "Shelton", "Sherwood", "Sky", "Storm",
				"Taffy", "Tyrrell", "Thomas", "Ray", "Rigby", "Ronald", "Ross", "Ruby"};
		String randomSurname = namesArray[new Random().nextInt(namesArray.length)];
		getDriver().findElement(authorsSurname).clear();
		getDriver().findElement(authorsSurname).sendKeys(randomSurname);
		return this;
	}
	
	public AdditionalDataPart insertPublishersName() {
		getDriver().findElement(publishersName).sendKeys("Pegasus");
		return this;
	}
	
	public EditPortfolioPage clickAddMaterial() {
		PageHelpers.waitForVisibility(createMaterialButton);
		getDriver().findElement(createMaterialButton).click();
		PageHelpers.waitForSeconds(3500);
		return new EditPortfolioPage();
	}


}
