package ee.netgroup.htm.components;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ex.UIAssertionError;
import ee.netgroup.htm.helpers.Helpers;
import ee.netgroup.htm.page.PortfolioPage;
import org.openqa.selenium.By;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.codeborne.selenide.Selenide.$;

public class PortfolioPrivacyModal {

    private static Logger logger = LoggerFactory.getLogger(PortfolioPrivacyModal.class);
    private static By exitAndMakePublic = By.xpath("//button[text()='Välju ja tee avalikuks']");
    private static By exitAndStayPrivate = By.xpath("//button[text()='Välju avalikuks tegemata']");


    public static PortfolioPage makePortfolioPublic() {
        try {
            $(exitAndMakePublic).shouldBe(Condition.visible).click();
            logger.info("Confirmation control modal displayed - make portfolio public chosen");
        } catch (UIAssertionError ignored) {
            logger.warn("Confirmation control modal not displayed");
        }
        Helpers.waitUntilNotVisible(NotificationToast.getElementLocator());
        return new PortfolioPage();
    }

    public static PortfolioPage clickExitAndStayPrivate() {
        try {
            $(exitAndStayPrivate).shouldBe(Condition.visible).click();
            logger.info("Confirmation control modal displayed - stay private chosen");
        } catch (UIAssertionError ignored) {
            logger.warn("Confirmation control modal not displayed");
        }
        Helpers.waitUntilNotVisible(NotificationToast.getElementLocator());
        return new PortfolioPage();
    }

}
