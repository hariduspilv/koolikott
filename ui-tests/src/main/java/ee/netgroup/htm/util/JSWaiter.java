package ee.netgroup.htm.util;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

import static com.codeborne.selenide.WebDriverRunner.getWebDriver;

/**
 * @author https://github.com/swtestacademy/JSWaiter
 */
public class JSWaiter {

    private static WebDriver jsWaitDriver = getWebDriver();
    private static WebDriverWait jsWait = new WebDriverWait(jsWaitDriver, 60);
    private static JavascriptExecutor jsExec = (JavascriptExecutor) jsWaitDriver;

    private static void waitUntilJSReady() {
        //Wait for Javascript to load
        ExpectedCondition<Boolean> jsLoad = driver -> ((JavascriptExecutor) jsWaitDriver)
                .executeScript("return document.readyState").toString().equals("complete");

        //Get JS is Ready
        boolean jsReady = jsExec.executeScript("return document.readyState").toString().equals("complete");

        //Wait Javascript until it is Ready!
        if (!jsReady) {
            jsWait.until(jsLoad);
        }
    }

    private static void waitForAngularLoad() {
        String angularReadyScript = "return angular.element(document).injector().get('$http').pendingRequests.length === 0";

        //Wait for ANGULAR to load
        ExpectedCondition<Boolean> angularLoad = driver -> Boolean.valueOf(((JavascriptExecutor) jsWaitDriver)
                .executeScript(angularReadyScript).toString());

        //Get Angular is Ready
        boolean angularReady = Boolean.valueOf(jsExec.executeScript(angularReadyScript).toString());

        //Wait ANGULAR until it is Ready!
        if (!angularReady) {
            jsWait.until(angularLoad);
        }
    }

    private static void waitUntilAngularReady() {
        Boolean angularUnDefined = (Boolean) jsExec.executeScript("return window.angular === undefined");
        if (!angularUnDefined) {
            Boolean angularInjectorUnDefined = (Boolean) jsExec.executeScript("return angular.element(document).injector() === undefined");
            if (!angularInjectorUnDefined) {

                //Wait Angular Load
                waitForAngularLoad();

                //Wait JS Load
                waitUntilJSReady();
            }
        }
    }

    private static void waitForJQueryLoad() {
        //Wait for jQuery to load
        ExpectedCondition<Boolean> jQueryLoad = driver -> ((Long) ((JavascriptExecutor) jsWaitDriver)
                .executeScript("return jQuery.active") == 0);

        //Get JQuery is Ready
        boolean jqueryReady = (Boolean) jsExec.executeScript("return jQuery.active==0");

        //Wait JQuery until it is Ready!
        if (!jqueryReady) {
            jsWait.until(jQueryLoad);
        }
    }

    private static void waitUntilJQueryReady() {

        //First check that JQuery is defined on the page. If it is, then wait AJAX
        Boolean jQueryDefined = (Boolean) jsExec.executeScript("return typeof jQuery != 'undefined'");
        if (jQueryDefined) {
            //Pre Wait for stability (Optional)
            //Wait JQuery Load
            waitForJQueryLoad();

            //Wait JS Load
            waitUntilJSReady();
        }
    }

    public static void waitForJQueryAndAngularToFinishRendering() {
        waitUntilJQueryReady();
        waitUntilAngularReady();
    }
}
