package ee.hm.dop.guice.provider;

import static org.junit.Assert.assertNotNull;

import javax.inject.Inject;

import ee.hm.dop.common.test.GuiceTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(GuiceTestRunner.class)
public class SOAPConnectionProviderTest {

    @Inject
    private SOAPConnectionProvider soapConnectionProvider;

    @Test
    public void get() {
        assertNotNull(soapConnectionProvider.get());
    }
}