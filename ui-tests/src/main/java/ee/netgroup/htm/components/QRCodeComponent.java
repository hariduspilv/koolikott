package ee.netgroup.htm.components;

import ee.netgroup.htm.helpers.Helpers;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import static ee.netgroup.htm.util.JSWaiter.waitForJQueryAndAngularToFinishRendering;

public class QRCodeComponent {

    private By qrCodeContentLocator = By.cssSelector("form[name='qrCodeModal'] > md-content");
    private By closeBtnLocator = By.cssSelector("form[name='qrCodeModal'] > div > button > md-icon");


    public QRCodeComponent() {
        waitForJQueryAndAngularToFinishRendering();
    }

    public static QRCodeComponent getQRCodeComponent() {
        return new QRCodeComponent();
    }


    public WebElement getQRCodeContentElement() {
        return Helpers.waitForVisibility(qrCodeContentLocator);
    }

    public void closeQRCodeModal(){
        Helpers.waitForClickable(closeBtnLocator).click();
        Helpers.waitUntilNotVisible(By.className("md-dialog-container"));
    }
}
