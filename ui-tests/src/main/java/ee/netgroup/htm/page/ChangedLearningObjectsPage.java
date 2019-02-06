package ee.netgroup.htm.page;

import ee.netgroup.htm.helpers.Helpers;
import org.openqa.selenium.By;

public class ChangedLearningObjectsPage extends Page {

    private By changedLearningObject = By.xpath("//a[@data-ng-bind='$ctrl.getCorrectLanguageTitle(item)']");

    public Page openChangedLearningObject() {
        Helpers.waitForClickable(changedLearningObject).click();
        return this;
    }
}
