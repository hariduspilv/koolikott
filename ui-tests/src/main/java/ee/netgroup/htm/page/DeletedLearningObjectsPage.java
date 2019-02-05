package ee.netgroup.htm.page;

import ee.netgroup.htm.helpers.Helpers;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import static com.codeborne.selenide.WebDriverRunner.getWebDriver;

public class DeletedLearningObjectsPage extends Page {

    private By deletedLearningObjects = By.cssSelector(".ellipsis.ellipsis--2");
    private By deletedLearningObjectsPageTitle = By.cssSelector("h2[data-translate='DELETED_LEARNING_OBJECTS']");

    public Page openDeletedLearningObject() {
        WebDriverWait wait = new WebDriverWait(getWebDriver(), 60);
        wait.until(ExpectedConditions.visibilityOfElementLocated(deletedLearningObjectsPageTitle));
        Helpers.waitForClickable(deletedLearningObjects);
        getWebDriver().findElements(deletedLearningObjects).get(0).click();
        return this;
    }

}
