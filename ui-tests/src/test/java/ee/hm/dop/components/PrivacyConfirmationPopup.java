package ee.hm.dop.components;

import org.openqa.selenium.By;

import ee.hm.dop.helpers.PageHelpers;
import ee.hm.dop.page.PortfolioViewPage;

public class PrivacyConfirmationPopup extends PageComponent {

	private By exitAndMakePublic = By.xpath("//button[text()='Välju ja tee avalikuks']");
	private By exitAndStayPrivate = By.xpath("//button[text()='Välju avalikuks tegemata']");

	public PortfolioViewPage makePortfolioPublic() {
           getDriver().findElement(exitAndMakePublic).click();
           PageHelpers.waitForSeconds(2500);
		   return new PortfolioViewPage();

	}
	
	public PortfolioViewPage clickExitAndStayPrivate() {
        getDriver().findElement(exitAndStayPrivate).click();
        PageHelpers.waitForSeconds(2500);
		   return new PortfolioViewPage();

	}

}
