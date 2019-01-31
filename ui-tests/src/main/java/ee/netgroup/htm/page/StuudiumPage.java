package ee.netgroup.htm.page;

import org.openqa.selenium.By;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class StuudiumPage extends Page {

    private By username = By.id("username");
    private By password = By.id("password");
    private By submit = By.xpath("//input[@type='submit']");
    private By permissionButton = By.name("data[Authorize][allow]");

    public StuudiumPage insertUsernameAndPassword(String user, String pswd) {
        $(username).clear();
        $(username).sendKeys(user);
        $(password).clear();
        $(password).sendKeys(pswd);
        return this;
    }

    public StuudiumPage submitStuudium() {
        $(submit).click();
        return this;
    }

    public LandingPage clickGivePermission() {
        $(permissionButton).waitUntil(visible, 5000).click();
        return new LandingPage();
    }

}
