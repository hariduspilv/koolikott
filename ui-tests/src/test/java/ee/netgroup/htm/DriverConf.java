package ee.netgroup.htm;

import com.codeborne.selenide.Configuration;
import org.testng.annotations.BeforeSuite;

public class DriverConf {

    @BeforeSuite
    public void setup() {
        Configuration.timeout = 10000L;
        Configuration.startMaximized = true;
    }
}
