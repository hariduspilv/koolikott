package ee.netgroup.hm.page;

import org.openqa.selenium.By;

import ee.netgroup.hm.helpers.Helpers;

public class UnreviewedLearningObjectsPage extends Page{
	
	private By unreviewedLearningObject = By.cssSelector(".md-cell:nth-child(4)");

	public Page clickOpenLearningObject() {
		Helpers.waitForVisibility(unreviewedLearningObject);
		getDriver().findElement(unreviewedLearningObject).click();
		return this;
	}

}
