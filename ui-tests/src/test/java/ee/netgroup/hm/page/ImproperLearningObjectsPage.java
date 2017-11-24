package ee.netgroup.hm.page;

import org.openqa.selenium.By;

public class ImproperLearningObjectsPage extends Page{
	
	private By openLearningObjectButton = By.xpath("//a[@data-ng-bind='$ctrl.getCorrectLanguageTitle(item)']");
	
	
	public Page openImproperLearningObject() {
		getDriver().findElement(openLearningObjectButton).click();
		return this;
	}

}
