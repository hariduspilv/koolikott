package ee.hm.dop.page;

import org.openqa.selenium.By;

import ee.hm.dop.helpers.PageHelpers;

public class MyFavoritesPage extends Page {
	
private By addFavoriteMessage = By.cssSelector("h3");
private By addPortfolioButton = By.id("add-portfolio");
	
	public boolean getAddFavoritesMessageText() {
		PageHelpers.waitForVisibility(addPortfolioButton);
		PageHelpers.waitForSeconds(1500);
		return getDriver().findElement(addFavoriteMessage).getText().contains("Sul ei ole veel lemmikuid");

	}


}
