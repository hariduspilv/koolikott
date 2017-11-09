package ee.netgroup.hm.page;

import org.openqa.selenium.By;

import ee.netgroup.hm.helpers.Helpers;

public class DeletedLearningObjectsPage extends Page {
	
	private By deletedLearningObject = By.xpath("//a[@data-ng-bind='$ctrl.getCorrectLanguageTitle(item)']");

	public Page openDeletedLearningObject() {
		Helpers.waitForVisibility(deletedLearningObject);
		getDriver().findElement(deletedLearningObject).click();
		return this;
	}

}
