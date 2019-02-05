package ee.netgroup.htm.components;

import ee.netgroup.htm.helpers.Constants;
import ee.netgroup.htm.helpers.Helpers;
import ee.netgroup.htm.page.EditPortfolioPage;
import ee.netgroup.htm.page.MyMaterialsPage;
import org.openqa.selenium.By;

import static com.codeborne.selenide.WebDriverRunner.getWebDriver;

public class AddMaterialsToPortfolioToolbar {


    private static By materialBox = By.xpath("//div[@class='card-hover material description']");
    private static By uncheckedCircle = By.xpath("//div/md-icon[text()='check']");
    private static By checkedCircle = By.xpath("//div/md-icon[text()='check_circle']");
    private static By selectPortfolioDropdown = By.xpath("//md-select[@aria-label='Vali kogumik']");
    private static By selectFirstPortfolio = By.xpath("/html/body/div[3]/md-select-menu/md-content/md-option[1]");
    private By selectChapterDropdown = By.xpath("//md-select[@aria-label='Vali peatükk']");
    private By selectFirstChapter = By.cssSelector("md-option[data-translate='ADD_TO_NEW_CHAPTER']");
    private By doneButton = By.xpath("//md-icon[text()='done']");


    public static AddMaterialsToPortfolioToolbar clickToSelectMaterial() {
        Helpers.moveToElement(Constants.firstMaterial);
        Helpers.moveToElement(uncheckedCircle);
        getWebDriver().findElement(checkedCircle).click();
        return new AddMaterialsToPortfolioToolbar();
    }

    public static AddMaterialsToPortfolioToolbar clickToSelectMaterial2() {
        Helpers.moveToElement(Constants.firstMaterial);
        Helpers.moveToElement(uncheckedCircle);
        getWebDriver().findElement(checkedCircle).click();
        return new AddMaterialsToPortfolioToolbar();
    }

    public static AddMaterialsToPortfolioToolbar selectPortfolio() {
        Helpers.waitForClickable(selectPortfolioDropdown).click();
        Helpers.waitForClickable(selectFirstPortfolio).click();
        return new AddMaterialsToPortfolioToolbar();
    }

    public AddMaterialsToPortfolioToolbar selectChapter() {
        Helpers.waitForClickable(selectChapterDropdown).click();
        Helpers.waitForClickable(selectFirstChapter).click();
        return this;
    }

    public MyMaterialsPage clickDone() {
        getWebDriver().findElement(doneButton).click();
        return new MyMaterialsPage();
    }

    public EditPortfolioPage clickAddMaterialToPortfolio() {
        getWebDriver().findElement(doneButton).click();
        return new EditPortfolioPage();
    }


}
