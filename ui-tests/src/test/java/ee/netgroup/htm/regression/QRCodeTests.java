package ee.netgroup.htm.regression;

import com.google.zxing.NotFoundException;
import ee.netgroup.htm.DriverConf;
import ee.netgroup.htm.components.UserMenu;
import ee.netgroup.htm.page.PortfolioPage;
import ee.netgroup.htm.util.QRCodeUtil;
import ee.netgroup.htm.util.ScreenshotUtils;
import org.openqa.selenium.WebElement;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static com.codeborne.selenide.WebDriverRunner.url;
import static ee.netgroup.htm.components.FabButton.getFabButton;
import static ee.netgroup.htm.components.QRCodeComponent.getQRCodeComponent;
import static ee.netgroup.htm.enums.VocationalEducationSubjectArea.AGRICULTURE;
import static ee.netgroup.htm.enums.VocationalEducationSubjectArea.ARTS;
import static ee.netgroup.htm.page.LandingPage.goToLandingPage;
import static ee.netgroup.htm.page.MaterialPage.getMaterialPage;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.testng.Assert.assertTrue;

public class QRCodeTests extends DriverConf {

    @BeforeClass
    public void login_as_registered_user() {
        goToLandingPage()
                .chooseUserType("User")
                .waitForLoggedInNotificationToClose();
        assertTrue(UserMenu.isLoggedIn());
    }

    @AfterMethod
    public void back_to_front_page() {
        goToLandingPage();
    }

    @Test
    public void create_material_and_generate_qr_code_the_code_corresponds_to_material_url() throws NotFoundException {
        getFabButton()
                .clickAddMaterial()
                .setRandomHyperLink()
                .setTitle()
                .selectDefaultLicenseType()
                .selectVocationalEducation()
                .selectSubjectAreaForVocationEducation(ARTS.getTranslationKey())
                .setIsSelfAuthored()
                .clickCreateMaterial();
        String materialUrl = url();

        WebElement qrCodeContentWebElement = getMaterialPage().clickGenerateQRCode().getQRCodeContentElement();

        String qrCodeText = QRCodeUtil.decodeQRCodeFromBufferedImage(ScreenshotUtils.captureElementScreenshotAsBufferedImage(qrCodeContentWebElement));

        assertThat(qrCodeText, is(materialUrl));
        getQRCodeComponent().closeQRCodeModal();
    }

    @Test
    public void create_portfolio_and_generate_qr_code_should_correspond_to_portfolio_url() throws NotFoundException {

        getFabButton()
                .clickAddPortfolio()
                .setTitle()
                .setDefaultLicenseType()
                .selectVocationalEducation()
                .selectSubjectAreaForVocationEducation(AGRICULTURE.getTranslationKey())
                .clickCreate()
                .clickSaveAndExitConfirmationControl();
        String portfolioUrl = url();

        WebElement qrCodeContentWebElement = PortfolioPage.getPortfolioPage()
                .clickGenerateQRCode()
                .getQRCodeContentElement();

        String qrCodeText = QRCodeUtil.decodeQRCodeFromBufferedImage(ScreenshotUtils.captureElementScreenshotAsBufferedImage(qrCodeContentWebElement));

        assertThat(qrCodeText, is(portfolioUrl));
        getQRCodeComponent().closeQRCodeModal();
    }
}
