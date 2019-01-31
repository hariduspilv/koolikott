package ee.netgroup.htm.components;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ex.UIAssertionError;
import ee.netgroup.htm.helpers.Helpers;
import ee.netgroup.htm.page.EKoolPage;
import ee.netgroup.htm.page.LandingPage;
import ee.netgroup.htm.page.StuudiumPage;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.sleep;
import static ee.netgroup.htm.util.JSWaiter.waitForJQueryAndAngularToFinishRendering;

public class LoginModal {

    private static Logger logger = LoggerFactory.getLogger(LoginModal.class);
    private static By logInText = By.xpath("//h2[@data-ng-bind='title']");
    private By mobileIdCodeField = By.name("idCode");
    private By mobilePhoneNumberField = By.name("phoneNumber");
    private By mobileLoginButton = By.xpath("//button[@class='md-raised login-button-mobiilid md-button md-ink-ripple']");
    private By eKoolLoginButton = By.xpath("//button[@data-ng-click='ekoolAuth()']");
    private By stuudiumButton = By.xpath("//button[@data-ng-click='stuudiumAuth()']");
    private final By closeModalBtn = By.cssSelector("button[ng-click='hideLogin()']");
    private static By loginModal = By.className("login-dialog");

    public LoginModal() {
        waitForJQueryAndAngularToFinishRendering();
    }

    public static String getLoginIsRequiredText() {
        return $(logInText).text();
    }

    public static LoginModal getLoginModal() {
        return new LoginModal();
    }

    public static boolean isDisplayed() {
        try {
            $(loginModal).shouldBe(Condition.visible);
        } catch (UIAssertionError e) {
            logger.error("Login Modal is not displayed:\n" + e.getCause().getMessage());
            return false;
        }
        return true;
    }

    public LoginModal insertMobileIDCode(String mobileIdCode) {
        WebElement element = Helpers.waitForVisibility(mobileIdCodeField);
        Helpers.sendKeysByChar(element, mobileIdCode);
        return this;
    }

    public LoginModal insertMobilePhoneNumber(String mobilePhoneNumber) {
        WebElement element = Helpers.waitForVisibility(mobilePhoneNumberField);
        Helpers.sendKeysByChar(element, mobilePhoneNumber);
        return this;
    }

    public LandingPage clickLoginWithMobileID() {
        waitForJQueryAndAngularToFinishRendering();
        Helpers.waitForClickable(mobileLoginButton).click();
        By mobileIdCodeModal = By.cssSelector("span[data-translate='LOGIN_MOBILEID_CODE']");
        Helpers.waitForVisibility(mobileIdCodeModal);
        try {
            Helpers.waitUntilNotVisible(mobileIdCodeModal);
        } catch (TimeoutException ignored) {
            // lets try one last time
            Helpers.waitUntilNotVisible(mobileIdCodeModal);
        }
        return new LandingPage();
    }

    public EKoolPage clickLoginWithEKool() {
        waitForJQueryAndAngularToFinishRendering();
        Helpers.waitForClickable(eKoolLoginButton).click();
        return new EKoolPage();
    }

    public StuudiumPage clickLoginWithStuudium() {
        waitForJQueryAndAngularToFinishRendering();
        Helpers.waitForClickable(stuudiumButton).click();
        return new StuudiumPage();
    }

    public void closeModal() {
        sleep(500);
        try {
            $(closeModalBtn).shouldBe(Condition.visible).click();
        } catch (TimeoutException ignored) {
            logger.warn("Restore my last location modal was not displayed");
        }
        Helpers.waitUntilNotVisible(NotificationToast.getElementLocator());
    }
}
