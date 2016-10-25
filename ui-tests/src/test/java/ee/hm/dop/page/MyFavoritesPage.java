package ee.hm.dop.page;

import org.openqa.selenium.By;

import ee.hm.dop.helpers.PageHelpers;

public class MyFavoritesPage extends Page {
	
private By addFavoriteMessage = By.cssSelector("h3");
	
	public boolean getAddFavoritesMessageText() {
		PageHelpers.waitForVisibility(addFavoriteMessage);
		return getDriver().findElement(addFavoriteMessage).getText().contains("Sul ei ole veel lemmikuid");

	}


}
