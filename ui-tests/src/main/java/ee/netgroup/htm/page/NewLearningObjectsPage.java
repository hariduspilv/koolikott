package ee.netgroup.htm.page;

import ee.netgroup.htm.helpers.Helpers;
import ee.netgroup.htm.util.JSWaiter;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class NewLearningObjectsPage extends Page {

    private By firstLearningObject = By.xpath("(//td/a)[1]");
    private By learningObjectTypeSort = By.cssSelector("th[md-order-by='byType']");
    private By sortTypeByArrowIcon = By.cssSelector("th[md-order-by='byType'] > md-icon");

    public static NewLearningObjectsPage getNewLearningObjectsPage() {
        return new NewLearningObjectsPage();
    }

    public NewLearningObjectsPage() {
        JSWaiter.waitForJQueryAndAngularToFinishRendering();
    }

    public LearningObjectPage openFirstNewLearningObject() {
        Helpers.waitForClickable(firstLearningObject).click();
        return new LearningObjectPage();
    }

    public NewLearningObjectsPage sortByLearningObjectTypePortfoliosFirst() {
        $(learningObjectTypeSort).shouldBe(visible).click();
        if (!$(sortTypeByArrowIcon).attr("class").contains("md-asc")) {
            $(learningObjectTypeSort).click();
        }
        return this;
    }

    public NewLearningObjectsPage sortByLearningObjectTypeMaterialsFirst() {
        $(learningObjectTypeSort).click();
        if (!$(sortTypeByArrowIcon).attr("class").contains("md-desc")) {
            $(learningObjectTypeSort).click();
        }
        return this;
    }

    public PortfolioPage openFirstNewLearningObject_Portfolio() {
        sortByLearningObjectTypePortfoliosFirst();
        openFirstNewLearningObject();
        return new PortfolioPage();
    }

    public PortfolioPage openFirstNewLearningObject_Material() {
        sortByLearningObjectTypeMaterialsFirst();
        openFirstNewLearningObject();
        return new PortfolioPage();
    }

}
