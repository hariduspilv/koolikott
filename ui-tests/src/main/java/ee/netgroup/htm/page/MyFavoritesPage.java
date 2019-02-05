package ee.netgroup.htm.page;

import ee.netgroup.htm.helpers.Constants;
import ee.netgroup.htm.helpers.Helpers;
import org.openqa.selenium.By;

import static com.codeborne.selenide.WebDriverRunner.getWebDriver;

public class MyFavoritesPage extends Page {

    private static By addFavoriteMessage = By.cssSelector("h2[data-translate='NO_FAVORITES']");

    public static MyFavoritesPage getMyFavoritesPage(){
        return new MyFavoritesPage();
    }

    public String getAddFavoritesMessageText() {
        return getWebDriver().findElement(addFavoriteMessage).getText();
    }

    public MaterialPage openMaterial() {
        getWebDriver().findElement(Constants.firstMaterial).click();
        return new MaterialPage();
    }

}
