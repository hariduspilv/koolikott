package ee.netgroup.htm.components;

import com.codeborne.selenide.Condition;
import ee.netgroup.htm.helpers.Helpers;
import ee.netgroup.htm.page.MaterialPage;
import ee.netgroup.htm.page.PortfolioPage;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;

public class ReportImproperModal {

    private static By reportDescription = By.xpath("//textarea[@data-ng-model='data.reportingText']");
    private By reportContent = By.name("LO_CONTENT");
    private By notifyBtn = By.xpath("//button[@data-ng-click='sendReport()']");

    public static ReportImproperModal getReportImproperPopUp() {
        return new ReportImproperModal();
    }

    public ReportImproperModal setImproperReason() {
        $(reportContent).shouldBe(Condition.visible).click();
        return this;
    }

    public ReportImproperModal setImproperDescription() {
        $(reportDescription).click();
        Helpers.sendKeysByChar($(reportDescription), "Hab");
        return this;
    }

    public PortfolioPage clickNotifyPortfolio() {
        $(notifyBtn).click();
        return new PortfolioPage();
    }

    public MaterialPage clickNotifyMaterial() {
        $(notifyBtn).click();
        return new MaterialPage();
    }

    public String getLogInIsRequiredText() {
        return LoginModal.getLoginIsRequiredText();
    }

    public LoginModal getLogInPopUp() {
        return LoginModal.getLoginModal();
    }


}
