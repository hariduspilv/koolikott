package ee.netgroup.hm.helpers;

import org.openqa.selenium.By;

public class Constants {
	

	public static String landingPage = "https://oxygen.netgroupdigital.com/";
	
	
	// Users
	public static String admin = "https://oxygen.netgroupdigital.com/dev/login/89898989898";
    public static String publisher = "https://oxygen.netgroupdigital.com/dev/login/12345678900";
    public static String user = "https://oxygen.netgroupdigital.com/dev/login/38202020234";
    public static String moderator = "https://oxygen.netgroupdigital.com/dev/login/35012025932";
    public static String restricted = "https://oxygen.netgroupdigital.com/dev/login/89898989890";
    
    public static String mobileIdCode = "11412090004";
    public static String mobilePhoneNumber = "+37200000766";
    public static String mobileIdUser = "Mary Änn O’connež-Šuslik";
    
    public static String eSchoolUser = "peeter.paan";
    public static String eSchoolPswd = "parool";
    public static String eSchoolUserName = "Peeter Paan";
    
    public static String stuudiumUser = "Netgroup Test Kaks";
    public static String stuudiumPswd = "meie teine saladus";
    
    public static By loginConfirmationText = By.xpath("//span[contains(text(), 'Oled sisse loginud')]");
    
   // Search
    public static String searchTag = "epic";
    public static String searchPublisher = "Pegasus";

    // Notifications
    public static By toastText = By.cssSelector("span.md-toast-text");
    public static String reportedText = "Teade saadetud administraatorile";
    
    // Open first portfolio or material on the list
    public static By firstPortfolio = By.xpath("//h3[@data-ng-bind='$ctrl.learningObject.title']");
    public static By firstMaterial = By.xpath("//h3[@data-ng-bind='$ctrl.getCorrectLanguageTitle($ctrl.learningObject)']");
    
    // Detail view 
    public static By actionsMenu = By.xpath("//md-icon[text()='more_vert']");
    
    // Materials
    public static String deletedMaterial = "https://www.gfycat.com/gifs/search/pug/detail/RingedWarpedCrayfish";
    public static String existingMaterial= "https://test.ee";
    
    
	public static String unreviewedBannerText = "See õppevara on üle vaatamata";
    
    
}
