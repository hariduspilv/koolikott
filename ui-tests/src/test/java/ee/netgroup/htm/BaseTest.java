package ee.netgroup.htm;

import com.codeborne.selenide.Configuration;
import org.testng.annotations.BeforeSuite;

import static com.codeborne.selenide.Selenide.open;
import static ee.netgroup.htm.helpers.Constants.EKOOLIKOTT_HOME_PAGE_URL;

public class BaseTest {

    @BeforeSuite
    public void setup() {
        Configuration.browser = "chrome";
        Configuration.timeout = 10000L;
        Configuration.startMaximized = true;
        open(EKOOLIKOTT_HOME_PAGE_URL);
    }
}
