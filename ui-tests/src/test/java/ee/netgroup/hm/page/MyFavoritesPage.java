package ee.netgroup.hm.page;

import org.openqa.selenium.By;

import ee.netgroup.hm.helpers.Constants;
import ee.netgroup.hm.helpers.Helpers;

public class MyFavoritesPage extends Page{
	
	private static By favoritesText = By.xpath("//h2[contains(text(), 'Lemmikud')]");
	private static By addFavoriteMessage = By.cssSelector("h3");

	public static boolean getAddFavoritesMessageText() {
		Helpers.waitForVisibility(favoritesText);
		Helpers.waitForMilliseconds(1000);
		return getDriver().findElement(addFavoriteMessage).getText().contains("Sul ei ole veel lemmikuid");
	}

	public MaterialPage openMaterial() {
		getDriver().findElement(Constants.firstMaterial).click();
		return new MaterialPage();
	}

}
