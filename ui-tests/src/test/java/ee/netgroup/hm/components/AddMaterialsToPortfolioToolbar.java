package ee.netgroup.hm.components;

import org.openqa.selenium.By;
import ee.netgroup.hm.helpers.Helpers;
import ee.netgroup.hm.page.EditPortfolioPage;
import ee.netgroup.hm.page.MyMaterialsPage;

public class AddMaterialsToPortfolioToolbar extends Component{
	
	
	private static By materialBox = By.xpath("//div[@class='card-hover material description']"); //TODO: kordub?
	private static By uncheckedCircle = By.xpath("//div/md-icon[text()='check']");
	private static By checkedCircle = By.xpath("//div/md-icon[text()='check_circle']");
	private By selectPortfolioDropdown = By.xpath("//md-select[@aria-label='Vali kogumik']");
	private By selectFirstPortfolio = By.xpath("/html/body/div[5]/md-select-menu/md-content/md-option[1]");
	private By selectChapterDropdown = By.xpath("//md-select[@aria-label='Vali peatükk']");
	private By selectFirstChapter = By.xpath("/html/body/div[6]/md-select-menu/md-content/md-option[1]");
	private By doneButton = By.xpath("//md-icon[text()='done']");
	
	
	public static AddMaterialsToPortfolioToolbar clickToSelectMaterial() {
		Helpers.moveToElement(materialBox);
		Helpers.moveToElement(uncheckedCircle);
		getDriver().findElement(checkedCircle).click();
		return new AddMaterialsToPortfolioToolbar();
	}
	
	public static AddMaterialsToPortfolioToolbar clickToSelectMaterial2() {
		Helpers.moveToElement(materialBox);
		Helpers.moveToElement(uncheckedCircle);
		getDriver().findElement(uncheckedCircle).click();
		return new AddMaterialsToPortfolioToolbar();
	}	

	public AddMaterialsToPortfolioToolbar selectPortfolio() {
		Helpers.waitForVisibility(selectPortfolioDropdown);
		getDriver().findElement(selectPortfolioDropdown).click();
		Helpers.waitForVisibility(selectFirstPortfolio);
		getDriver().findElement(selectFirstPortfolio).click();
		return this;
	}

	public AddMaterialsToPortfolioToolbar selectChapter() {
		Helpers.waitForVisibility(selectChapterDropdown );
		getDriver().findElement(selectChapterDropdown).click();
		getDriver().findElement(selectFirstChapter).click();
		return this;
	}

	public MyMaterialsPage clickDone() {
		getDriver().findElement(doneButton).click();
		return new MyMaterialsPage();
	}

	public EditPortfolioPage clickAddMaterialToPortfolio() {
		getDriver().findElement(doneButton).click();
		return new EditPortfolioPage();
	}


}
