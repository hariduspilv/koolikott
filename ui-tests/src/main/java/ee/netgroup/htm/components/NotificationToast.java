package ee.netgroup.htm.components;

import ee.netgroup.htm.util.JSWaiter;
import org.openqa.selenium.By;

public class NotificationToast {
    private static By toastLocation = By.className("md-toast-text");

    public NotificationToast(){
        JSWaiter.waitForJQueryAndAngularToFinishRendering();
    }

    public static By getElementLocator() {
        return toastLocation;
    }
}
