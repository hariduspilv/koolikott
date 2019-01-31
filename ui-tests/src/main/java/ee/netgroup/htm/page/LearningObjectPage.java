package ee.netgroup.htm.page;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ex.UIAssertionError;
import ee.netgroup.htm.components.ConfirmationModal;
import ee.netgroup.htm.components.QRCodeComponent;
import ee.netgroup.htm.helpers.Constants;
import ee.netgroup.htm.helpers.Helpers;
import org.openqa.selenium.By;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.switchTo;
import static ee.netgroup.htm.util.JSWaiter.waitForJQueryAndAngularToFinishRendering;

public class LearningObjectPage extends Page {

    private static Logger logger = LoggerFactory.getLogger(LearningObjectPage.class);
    private static By searchField = By.xpath("//input[@id='header-search-input']");
    private By generateQRCodeBtnLocator = By.xpath("(//button[contains(@class, 'qr-code')])[1]");
    private By fullscreenBtn = By.cssSelector("button[data-ng-click='toggleFullScreen()']");
    private By LOBanner = By.cssSelector("md-toolbar[class='error-message _md _md-toolbar-transitions']");
    private By similarObjectsAside = By.cssSelector("div[data-ng-if='similar.length > 0']");
    private By recommendedObjectsAside = By.cssSelector("div[data-ng-if='recommendations.length > 0']");
    private By leftNavBar = By.cssSelector("div[role='tablist']");
    private By customerSupportBtn = By.id("customerSupportButton");
    private By sideNavHeader = By.id("sidenavHeader");
    private By sideNavFirstChapter = By.cssSelector("a[data-slug='chapter-1']");
    private By deletePortfolio = By.xpath("//button[@data-ng-click='confirmPortfolioDeletion()']");
    private By deleteMaterial = By.xpath("//button[@data-ng-click='confirmMaterialDeletion()']");
    private By editPortfolio = By.cssSelector("button[data-ng-click='editPortfolio()']");
    private By editMaterial = By.cssSelector("button[data-ng-click='edit()']");
    private By shareBtn = By.cssSelector("button[class='md-icon-button share__btn share__btn--trigger md-button md-ink-ripple']");
    private By shareViaEmail = By.cssSelector("button[class='md-fab md-raised md-mini share__btn share__btn--email md-button']");
    private By shareViaGoogle = By.cssSelector("button[class='md-fab md-raised md-mini share__btn share__btn--google md-button']");
    private By shareViaTwitter = By.cssSelector("button[class='md-fab md-raised md-mini share__btn share__btn--twitter md-button']");
    private By shareViaFacebook = By.cssSelector("button[class='md-fab md-raised md-mini share__btn share__btn--facebook md-button']");


    public LearningObjectPage() {
        waitForJQueryAndAngularToFinishRendering();
    }

    public static LearningObjectPage getLearningObjectPage() {
        return new LearningObjectPage();
    }

    public QRCodeComponent clickGenerateQRCode() {
        Helpers.waitForClickable(generateQRCodeBtnLocator).click();
        return new QRCodeComponent();
    }

    public LearningObjectPage clickFullscreenButton() {
        Helpers.waitForClickable(fullscreenBtn).click();
        return this;
    }

    /**
     * Clicks on external link and switches to a specified tab.
     */
    public MaterialPage clickOnExternalLink(int tabIndex) {
        $(By.xpath("(//a[@href=\"" + Constants.materialWithCommentsUrl + "\"])")).scrollIntoView(true).click();
        switchTo().window(tabIndex);
        return new MaterialPage();
    }

    /**
     * Clicks on external link, opens it in a new tab and switches to it.
     */
    public MaterialPage clickOnExternalLink() {
        $(By.xpath("(//a[@href=\"" + Constants.materialWithCommentsUrl + "\"])")).scrollIntoView(true).click();
        switchTo().window(1);
        return new MaterialPage();
    }

    public boolean fsModeBtnIsVisible() {
        return Helpers.isWebElementVisible(fullscreenBtn);
    }

    public boolean isLOBannerVisible() {
        return Helpers.isWebElementVisible(LOBanner);
    }

    public boolean isQRBtnVisible() {
        return Helpers.isWebElementVisible(generateQRCodeBtnLocator);
    }

    public boolean isActionsMenuBtnVisible() {
        return Helpers.isWebElementVisible(Constants.actionsMenu);
    }

    public boolean canViewSearchField() {
        return Helpers.isWebElementVisible(searchField);
    }

    public boolean canGetCustomerSupport() {
        return Helpers.isWebElementVisible(customerSupportBtn);
    }

    public boolean canViewSimilarObjectsAside() {
        return Helpers.isWebElementVisible(similarObjectsAside);
    }

    public boolean canViewRecommendedObjectsAside() {
        return Helpers.isWebElementVisible(recommendedObjectsAside);
    }

    public LearningObjectPage clickOnSideNavHeader() {
        Helpers.waitForClickable(sideNavHeader).click();
        return this;
    }

    public LearningObjectPage clickOnSideNav_Header_Five_Times() {
        for (int i = 0; i < 5; i++) {
            Helpers.waitForClickable(sideNavHeader).click();
        }
        return this;
    }

    public LearningObjectPage clickOnSideNav_FirstChapter_Five_Times() {
        for (int i = 0; i < 5; i++) {
            Helpers.waitForClickable(sideNavFirstChapter).click();
        }
        return this;
    }

    public ConfirmationModal clickDeleteLearningObject() {
        if ($(deletePortfolio).shouldBe(Condition.visible) != null) {
            $(deletePortfolio).click();
        } else {
            $(deleteMaterial).shouldBe(Condition.visible).click();
        }
        return new ConfirmationModal();
    }

    public boolean isLeftNavBarVisible() {
        return Helpers.isWebElementVisible(leftNavBar);
    }

    public LearningObjectPage closeFSModeIfOpen() {
        try {
            $(LOBanner).shouldBe(Condition.hidden);
            Helpers.waitForClickable(fullscreenBtn).click();
            logger.info("Fullscreen Mode is open, closing it");
        } catch (UIAssertionError ignored) {
            logger.info("Fullscreen Mode is not open");
        }
        return this;
    }

}
