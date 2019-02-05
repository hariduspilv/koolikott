package ee.netgroup.htm.page;

import com.codeborne.selenide.Condition;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;

public class ImproperLearningObjectsPage extends Page {

    private By openLearningObjectButton = By.xpath("//a[@data-ng-bind='$ctrl.getCorrectLanguageTitle(item)']");


    public Page openImproperLearningObject() {
        $(openLearningObjectButton).shouldBe(Condition.visible).click();
        return this;
    }

}
