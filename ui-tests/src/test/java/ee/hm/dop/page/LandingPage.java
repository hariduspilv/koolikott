package ee.hm.dop.page;

import org.openqa.selenium.By;


import ee.hm.dop.helpers.PageHelpers;

public class LandingPage extends Page {

    private By searchResultPortfolio = By.xpath("//h4[@data-ng-bind='portfolio.title']");
    private By searchResultMaterial = By.xpath("//h4[@data-ng-bind='getCorrectLanguageTitle(material)']");
    private By recommendationsList = By.xpath("//h4[@data-ng-bind='item.title']");
    private By sortDropdown = By.xpath("//md-select[@ng-model='sortDropdown']");
    private By newFirst = By.xpath("//md-option/div[text()='Uusimad eespool']");
    private By userName = By.xpath("//strong");
    
    
	public MyPortfoliosPage chooseUserType(String userType) {

		if (userType == "Admin")
			getDriver().get("oxygen.netgroupdigital.com/#/dev/login/89898989898");
		    PageHelpers.waitForSeconds(1500);

		if (userType == "SmallPublisher")
			getDriver().get("oxygen.netgroupdigital.com/#/dev/login/12345678900");
		    PageHelpers.waitForSeconds(1500);

		if (userType == "User")
			getDriver().get("oxygen.netgroupdigital.com/#/dev/login/38202020234");
		    PageHelpers.waitForSeconds(1500);
		
		if (userType == "Moderator")
			getDriver().get("oxygen.netgroupdigital.com/dev/login/35012025932");
		    PageHelpers.waitForSeconds(1500);

		return new MyPortfoliosPage();

	}

	public static LandingPage goToLandingPage() {
		getDriver().get("https://oxygen.netgroupdigital.com/");
		return new LandingPage();
	}
	
	public LandingPage sortMaterial() {
		PageHelpers.waitForVisibility(sortDropdown);
		getDriver().findElement(sortDropdown).click();
		getDriver().findElement(newFirst).click();
		PageHelpers.pressEsc();
		return this;
	}
	
	public PortfolioViewPage openSearchResultPortfolio() {
		getDriver().findElement(searchResultPortfolio).click();
		PageHelpers.waitForSeconds(1500);
		return new PortfolioViewPage();

	}
	
	public PortfolioViewPage openRecommendedPortfolio() {
		getDriver().findElement(recommendationsList).click();
		PageHelpers.waitForSeconds(1500);
		return new PortfolioViewPage();

	}
	
	public MaterialPage openSearchResultMaterial() {
		PageHelpers.waitForSeconds(2000);
		PageHelpers.waitForVisibility(searchResultMaterial);
		getDriver().findElement(searchResultMaterial).click();
		PageHelpers.waitForSeconds(1500);
		return new MaterialPage();

	}
	
	public String getUserName() {
		return getDriver().findElement(userName).getText();
	}

}
	