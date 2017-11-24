package ee.netgroup.hm.page;

import org.openqa.selenium.By;

import ee.netgroup.hm.helpers.Helpers;

public class ChangedLearningObjectsPage extends Page {
	
	private By changedLearningObject = By.xpath("//a[@data-ng-bind='$ctrl.getCorrectLanguageTitle(item)']");

	public Page openChangedLearningObject() {
		Helpers.waitForVisibility(changedLearningObject);
		getDriver().findElement(changedLearningObject).click();
		return this;
	}
}
