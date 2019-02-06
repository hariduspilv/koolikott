package ee.netgroup.htm.components;

import com.codeborne.selenide.Condition;
import ee.netgroup.htm.helpers.Helpers;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static ee.netgroup.htm.util.JSWaiter.waitForJQueryAndAngularToFinishRendering;

public class FabButton {

    private static By addPortfolioButton = By.id("add-portfolio");
    private static By copyPortfolioButton = By.xpath("//button[@data-ng-click='copyPortfolio()']");
    private static By addMaterialButton = By.id("add-material");

    public FabButton() {
        waitForJQueryAndAngularToFinishRendering();
    }

    public static FabButton getFabButton() {
        return new FabButton();
    }

    public static CreatePortfolioModal clickCopyPortfolio() {
        Helpers.moveToElement(addPortfolioButton);
        Helpers.waitForClickable(copyPortfolioButton).click();
        return new CreatePortfolioModal();
    }

    public CreatePortfolioModal clickAddPortfolio() {
        $(addPortfolioButton).shouldBe(visible).click();
        return new CreatePortfolioModal();
    }

    public CreateMaterialModal clickAddMaterial() {
        Helpers.moveToElement(addPortfolioButton);
        Helpers.waitForVisibility(addMaterialButton);
        $(addMaterialButton).shouldBe(Condition.visible).click();
        Helpers.waitForVisibility(CreateMaterialModal.getModalLocator());
        return new CreateMaterialModal();
    }

    public boolean isAddPortfolioButtonVisible() {
        return $(addPortfolioButton).isDisplayed();
    }

}
