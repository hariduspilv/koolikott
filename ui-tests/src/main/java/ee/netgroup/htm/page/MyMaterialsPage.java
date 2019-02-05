package ee.netgroup.htm.page;

import com.codeborne.selenide.Condition;
import ee.netgroup.htm.components.AddMaterialsToPortfolioToolbar;
import ee.netgroup.htm.helpers.Constants;
import ee.netgroup.htm.helpers.Helpers;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$$;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;

public class MyMaterialsPage extends Page {

    private static By addMaterialMessage = By.cssSelector("h2[data-translate='TO_ADD_MATERIAL_PRESS_ADD_MATERIAL']");
    private static By materialTitle = By.xpath("//h2[@data-ng-if='$ctrl.isMaterial(learningObject)']");
    private By materialMessage = By.cssSelector("span.md-toast-text");
    private By starIcon = By.xpath("//div[@class='md-icon-button md-button favorite']");

    public static MyMaterialsPage getMyMaterialsPage() {
        return new MyMaterialsPage();
    }

    public String getAddMaterialMessageText() {
        return getWebDriver().findElement(addMaterialMessage).getText();
    }

    public static String getFirstMaterialTitleFromMaterialsList() {
       return  $$(materialTitle).first().shouldBe(Condition.visible).getText();
    }

    public MaterialPage openMaterial() {
        Helpers.waitForVisibility(Constants.firstMaterial);
        getWebDriver().findElement(Constants.firstMaterial).click();
        return new MaterialPage();
    }

    public String getSuccessMessage() {
        Helpers.waitForVisibility(materialMessage);
        return getWebDriver().findElement(materialMessage).getText();
    }

    public AddMaterialsToPortfolioToolbar clickToSelectMaterial() {
        return AddMaterialsToPortfolioToolbar.clickToSelectMaterial();
    }

    public MyMaterialsPage clickToSelectStar() {
        Helpers.waitForVisibility(Constants.firstMaterial);
        Helpers.moveToElement(Constants.firstMaterial);
        Helpers.waitForVisibility(starIcon);
        getWebDriver().findElement(starIcon).click();
        return this;
    }


}
