package ee.hm.dop.guice.provider;

import ee.hm.dop.common.test.GuiceTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;

import static org.junit.Assert.assertNotNull;

@RunWith(GuiceTestRunner.class)
public class SOAPConnectionProviderTest {

    @Inject
    private SOAPConnectionProvider soapConnectionProvider;

    @Test
    public void get() {
        assertNotNull(soapConnectionProvider.get());
    }
}