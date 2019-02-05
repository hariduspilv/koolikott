package ee.netgroup.htm.components;

import ee.netgroup.htm.helpers.Helpers;
import ee.netgroup.htm.page.FaqPage;
import ee.netgroup.htm.page.LandingPage;
import ee.netgroup.htm.page.MaterialPage;
import ee.netgroup.htm.page.PortfolioPage;
import org.openqa.selenium.By;

import static com.codeborne.selenide.WebDriverRunner.getWebDriver;

public class ConfirmationModal {

    private static By confirm = By.xpath("//button[@class='md-primary md-confirm-button md-button md-ink-ripple md-default-theme']");
    private By confirmDeletePortfolioButton = By.xpath(("//button[@ng-click='dialog.hide()']"));

    public LandingPage clickConfirm() {
        getWebDriver().findElement(confirm).click();
        return new LandingPage();
    }

    public MaterialPage clickConfirmDeleteMaterial() {
        getWebDriver().findElement(confirm).click();
        return new MaterialPage();
    }

    public PortfolioPage confirmDeletePortfolio() {
        Helpers.waitForClickable(confirmDeletePortfolioButton);
        getWebDriver().findElement(confirmDeletePortfolioButton).click();
        return new PortfolioPage();
    }

    public FaqPage clickConfirmDeleteFaq() {
        Helpers.waitForClickable(confirmDeletePortfolioButton).click();
        return new FaqPage();
    }

}
