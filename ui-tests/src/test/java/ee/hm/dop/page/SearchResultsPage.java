package ee.hm.dop.page;

import org.openqa.selenium.By;

public class SearchResultsPage extends Page {
	
	private By educationalTaxon = By.cssSelector("span.hide-overflow");
	
	public String getEducationalTaxonText() {
		return getDriver().findElement(educationalTaxon).getText();

	}

}
