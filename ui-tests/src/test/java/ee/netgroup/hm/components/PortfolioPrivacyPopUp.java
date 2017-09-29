package ee.netgroup.hm.components;

import org.openqa.selenium.By;
import ee.netgroup.hm.page.PortfolioPage;

public class PortfolioPrivacyPopUp extends Component{
	
	private static By exitAndMakePublic = By.xpath("//button[text()='Välju ja tee avalikuks']");
	private static By exitAndStayPrivate = By.xpath("//button[text()='Välju avalikuks tegemata']");

	
	public static PortfolioPage makePortfolioPublic() {
		getDriver().findElement(exitAndMakePublic).click();
		return new PortfolioPage();
	}
	
	public static PortfolioPage clickExitAndStayPrivate() {
        getDriver().findElement(exitAndStayPrivate).click();
		return new PortfolioPage();
	}

}
