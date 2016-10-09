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
		String[] namesArray = { "Amina", "Abida", "Abla", "Ablah", "Adara", "Adhara", "'Adiya", "Aesha", "Afaf",
				"Aida", "Badr", "Badriyah", "Bahiga", "Bahija", "Bahilu", "Alitta", "Aidah", "Aisha", "Akela",
				"Akilah", "Alath", "Alilat", "Bahiyya", "Bailu", "Basima", "Basira", "Basma", "Batul",
				"Benat-Allah", "Borak", "Budur", "Chiba", "Dalal", "Dhat-Baadan", "Dhat-Hami", "Dima", "Djamila",
				"Duha", "El-Borak", "Fadia", "Fadila", "Fahima", "Fairuz'", "Faiza", "Fakhr", "Fakhiriya", "Farah",
				"Farida", "Fariha", "Farrah", "Fathiyya", "Fatima", "Almas", "Alozza", "Aludra", "Amal'", "Amani",
				"Ameena", "Amira", "Anisa", "Arrafa", "Asma", "Faten", "Fayza", "Fidda", "Firuza", "Fizza",
				"Galila", "Gamila", "Gazbiyya", "Ghada", "Ghadir", "Ghufran", "Habiba", "Hadia", "Hadiya", "Hafsah",
				"Haifa", "Hajar", "Hala", "Halima", "Hana", "Hanifa", "Haqiqah", "Hasina", "Hawa", "Hayfa", "Heva",
				"Hiba", "Hikmat", "Hoda", "Ibitihaj", "Ibtisam", "Ihab", "Ihsan", "Iman", "Ismat", "Isra", "Jalila",
				"Jamila", "Janan", "Jinan", "Jumahan", "Karam", "Karima", "Khadija", "Khatijah", "Khayriyya",
				"I'tidal", "Kismat", "Laila", "Lalla", "Lamia", "Latifa", "Lawahiz", "Layla", "Leena", "Lina",
				"Lubna", "Maana", "Malak", "Malika", "Maram", "Marwa", "Matabintain", "Mazel", "Medea", "Monat",
				"Mouna", "Muminah", "Nadira", "Nadiyya", "Naila", "Nashwa", "Nasim", "Nasira", "Nawal", "Noora",
				"Nur", "Rabab", "Rabiah", "Rafiqa", "Rahat", "Raja", "Rala", "Ramla", "Rana", "Ranya", "Rasima",
				"Rehema", "Rim", "Rizwana", "Roya", "Ruya", "Sabah", "Sadaf", "Safa", "Safiya", "Sahar", "Saidah",
				"Salama", "Salha", "Salima", "Salma", "Samar", "Sameera", "Samia", "Samsi", "Samya", "Sana",
				"Sauda", "Sawaha", "Selima", "Shadi", "Shafaqat", "Shahira", "Shakira", "Sharifa", "Shems",
				"Sultana", "Sumayya", "Tahira", "Talab", "Talibah", "Thalulah", "Tasnim", "Thamina", "Thurayya",
				"Tijah", "Umayma", "Uzma", "Wafa", "Waheeda", "Walidah", "Widad", "Yaminah", "Yapaa", "Yasmin",
				"Zabibe", "Zahia", "Zahida", "Zahira", "Zahrah", "Zaida", "Zaina", "Zara", "Zayna", "Zoraida",
				"Zubaida", "Aaliyah", "Amal", "Amber", "Atefeh", "Ehsan", "Elham", "Eshe", "Farah", "Iesha",
				"Inbar", "Ishtar", "Kamaria", "Lila", "Nabila", "Naima", "Niesha", "Nura", "Oma", "Qadira", "Qamar",
				"Rihanna", "Saffron", "Takisha", "Thana", "Aafia", "Aafreen", "Aairah", "Aala", "Aamanee", "Aazeen",
				"Abeela", "Abeer", "Ada", "Ad'ifaah", "Adeena", "Afrah", "Afroza", "Ahd", "Aleena", "Alika", "Alma",
				"Alvina", "Amatullah", "Amaya", "Amel", "Amelia", "Amna", "Amrah", "Amtullah", "Ana", "Amreen",
				"Anah", "Anbar", "Andaleeb", "Aneeqa", "Aneezah", "Angib", "Anousha", "Ara", "Aresha", "Ariana",
				"Aribah", "Arisha", "Arjumand", "Aroosa", "Arub", "Arya", "Ashalina", "Asiya", "Atika", "Az-zahra",
				"Azhaar", "Azeeza", "Azka", "Azra", "Badia", "Badra", "Bahar", "Bahira", "Bakht", "Bariah",
				"Barika", "Baseema", "Beena", "Chaman", "Chanda", "Chandi", "Daania", "Dahab", "Dahma", "Daleela",
				"Daliya", "Dawlat Khatoon", "Deeba", "Dina", "Duba'ah", "Dunia", "Eiliyah", "Eimaan", "Eliza",
				"Elma", "Enisa", "Eraj", "Ermina", "Esha'al", "Ezzah", "Fadilah", "Faizah", "Famya", "Fareedah",
				"Farhat", "Faryal", "Fida", "Ghania", "Ghitbah", "Gul-e-Rana", "Hafsa", "Halah", "Halimah",
				"Haneefah", "Hanfa", "Hasinah", "Heba", "Hessa", "Hindah", "Hirah", "Iffat", "Ifra", "Ilaaf",
				"Ilham", "In'am", "Inaaya", "Inas", "'Insha", "Isir", "Izdihar", "Izz an-Nisa", "Izzah",
				"Jahan Aara", "Jalilah", "Jamila", "Jaseena", "Jessenia", "Jihan", "Junnut", "Kainat", "Kaleemah",
				"Kali", "Kaltham", "Kamaliyah", "Kaneez", "Kardawiyah", "Kas", "Khairah", "Khidrah", "Koila",
				"Komal", "Lama", "Lamis", "Lana", "Lanika", "Leem", "Lu Luah", "Luna", "Lutfiyah", "Ma'isah",
				"Madia", "Maheen", "Maisha", "Malmal", "Manal", "Manar", "Maram", "Mariam", "Maridah", "Masabeeh",
				"Mashaal", "Maya", "Maysa", "Medina", "Meena", "Mehr", "Mid'haa", "Mina", "Mishel", "Mobena",
				"Mona", "Mysha", "Na'eemah", "Na'ilah", "Naba", "Nabilah", "Nadia", "Nafisah", "Nagina", "Namra",
				"Namyla", "Naraiman", "Nargis", "Narmin", "Nasha", "Nasheed", "Naureen", "Naushaba", "Naveen",
				"Navil", "Nazeefah", "Nazimah", "Neelam", "Neelofer", "Neeshad", "Neeya", "Nelofar", "Ni'ja",
				"Nimah", "Nimerah", "Nimrah", "Nina", "Nissa", "Noorie", "Noreen", "Nosheen", "Nurayda", "Nyla",
				"Omera", "Raameen", "Rabia", "Rabitah", "Rafiah", "Rafida", "Raheemah", "Rahiliah", "Raina",
				"Rameesha", "Rana", "Rani", "Reema", "Rinaaz", "Robina", "Rohaan", "Romana", "Rubina", "Saara",
				"Sabburah", "Sabeen", "Sabina", "Sabreen", "Sadoof", "Safa", "Safoorah", "Sagheerah", "Saharish",
				"Saimah", "Saleemah", "Samina", "Samrah", "Sanari", "Sanika", "Shabana", "Shafiah", "Shahreen",
				"Shamis", "Shanika", "Sharifah", "Sharmin", "Sheila", "Sherana", "Sitarah", "Soha", "Sonia",
				"Soraya", "Suhana", "Sumia", "Taalia", "Tabalah", "Tafida", "Tahani", "Tahira", "Tala", "Tamanna",
				"Tanisha", "Tamara", "Tara", "Taseefa", "Tasheen", "Tasnim", "Taybah", "Tazim", "Thaminah", "Thana",
				"Thara", "Tharya", "Tisha", "Ubah", "Ulfah", "Umm Kulthum", "Ummayyah", "Umnia", "Valiqa", "Vardah",
				"Varisha", "Wabisa", "Wafaa", "Wafiza", "Wahida", "Wania", "Warda", "Warizah", "Warsan", "Wazeera",
				"Widad", "Yalina", "Yaminah", "Yara", "Yusur", "Zaahirah", "Zafeerah", "Zafreen", "Zahara", "Zahra",
				"Zakia", "Zalfa", "Zamrud", "Zareen", "Zarina", "Zarmina", "Zeba", "Zehra", "Zerina", "Zihan",
				"Zoeya", "Zorah", "Zoya", "Zubi", "Zuhur", "Zulfa", "Zumzum", "Zynah"};
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
				"Day", "Lamont", "Flores"};
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
		getDriver().findElement(createMaterialButton).click();
		PageHelpers.waitForSeconds(3500);
		return new EditPortfolioPage();
	}


}
